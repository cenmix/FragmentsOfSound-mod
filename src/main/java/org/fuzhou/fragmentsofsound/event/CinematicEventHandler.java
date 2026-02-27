package org.fuzhou.fragmentsofsound.event;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import org.fuzhou.fragmentsofsound.Config;
import org.fuzhou.fragmentsofsound.Fragmentsofsound;
import org.fuzhou.fragmentsofsound.cinematic.CinematicData;
import org.fuzhou.fragmentsofsound.cinematic.CinematicManager;
import org.fuzhou.fragmentsofsound.network.NetworkHandler;
import org.fuzhou.fragmentsofsound.network.StartCinematicPacket;

import java.util.Random;
import java.util.WeakHashMap;

@Mod.EventBusSubscriber(modid = Fragmentsofsound.MODID)
public class CinematicEventHandler {
    
    private static final int DAY_2_THRESHOLD = 24000 * 2;
    private static final WeakHashMap<ServerLevel, Integer> checkCooldowns = new WeakHashMap<>();
    
    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!Config.CG_ENABLED.get() || !Config.CG_DAY2_CINEMATIC_ENABLED.get()) {
            return;
        }
        
        if (event.getEntity() instanceof ServerPlayer player) {
            ServerLevel level = player.serverLevel();
            CinematicManager manager = CinematicManager.get(level);
            
            long dayTime = level.getDayTime();
            int currentDay = (int) (dayTime / 24000);
            
            if (currentDay >= 2 && !manager.hasDay2CinematicPlayed()) {
                triggerDay2Cinematic(player, level, manager);
            }
        }
    }
    
    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.level instanceof ServerLevel)) {
            return;
        }
        
        if (!Config.CG_ENABLED.get() || !Config.CG_DAY2_CINEMATIC_ENABLED.get()) {
            return;
        }
        
        ServerLevel level = (ServerLevel) event.level;
        
        if (level.players().isEmpty()) {
            return;
        }
        
        CinematicManager manager = CinematicManager.get(level);
        
        if (manager.hasDay2CinematicPlayed()) {
            return;
        }
        
        int cooldown = checkCooldowns.getOrDefault(level, 0);
        cooldown++;
        if (cooldown < 100) {
            checkCooldowns.put(level, cooldown);
            return;
        }
        checkCooldowns.put(level, 0);
        
        long dayTime = level.getDayTime();
        int currentDay = (int) (dayTime / 24000);
        
        if (currentDay >= 1 && !manager.hasDay2CinematicPlayed()) {
            ServerPlayer player = (ServerPlayer) level.players().get(0);
            triggerDay2Cinematic(player, level, manager);
        }
    }
    
    private static void triggerDay2Cinematic(ServerPlayer player, ServerLevel level, CinematicManager manager) {
        if (manager.hasDay2CinematicPlayed()) {
            return;
        }
        
        BlockPos spawnPos = findRandomOutpostPosition(level, player.blockPosition());
        
        if (spawnPos == null) {
            return;
        }
        
        manager.setDay2CinematicPlayed(true);
        
        manager.addOutpostSpawnPosition(spawnPos);
        
        Random random = new Random();
        int outpostLevel = random.nextInt(7) + 1;
        String monsterConfig = "zombie";
        
        CinematicData cinematicData = CinematicData.createOutpostSpawn(
            spawnPos,
            level.dimension().location(),
            outpostLevel,
            monsterConfig,
            true
        );
        
        NetworkHandler.INSTANCE.send(
            PacketDistributor.PLAYER.with(() -> player),
            new StartCinematicPacket(cinematicData)
        );
    }
    
    public static BlockPos findRandomOutpostPosition(ServerLevel level, BlockPos playerPos) {
        Random random = new Random();
        
        int minDistance = 100;
        int maxDistance = 200;
        
        for (int attempt = 0; attempt < 20; attempt++) {
            int offsetX = random.nextInt(maxDistance - minDistance) + minDistance;
            int offsetZ = random.nextInt(maxDistance - minDistance) + minDistance;
            
            if (random.nextBoolean()) offsetX = -offsetX;
            if (random.nextBoolean()) offsetZ = -offsetZ;
            
            int x = playerPos.getX() + offsetX;
            int z = playerPos.getZ() + offsetZ;
            
            int y = level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            
            BlockPos testPos = new BlockPos(x, y - 1, z);
            BlockState groundState = level.getBlockState(testPos);
            
            if (!groundState.isAir() && !groundState.canBeReplaced()) {
                return testPos;
            }
        }
        
        return null;
    }
    
    public static void triggerCinematicForPlayer(ServerPlayer player, BlockPos pos, int outpostLevel, String monsterConfig, boolean applyChanges) {
        ServerLevel level = player.serverLevel();
        
        CinematicData cinematicData = CinematicData.createOutpostSpawn(
            pos,
            level.dimension().location(),
            outpostLevel,
            monsterConfig,
            applyChanges
        );
        
        NetworkHandler.INSTANCE.send(
            PacketDistributor.PLAYER.with(() -> player),
            new StartCinematicPacket(cinematicData)
        );
    }
}
