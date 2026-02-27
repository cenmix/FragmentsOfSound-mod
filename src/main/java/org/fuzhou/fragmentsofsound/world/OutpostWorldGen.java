package org.fuzhou.fragmentsofsound.world;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.fuzhou.fragmentsofsound.Config;
import org.fuzhou.fragmentsofsound.Fragmentsofsound;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Mod.EventBusSubscriber(modid = Fragmentsofsound.MODID)
public class OutpostWorldGen {
    
    private static final Set<ChunkPos> processedChunks = new HashSet<>();
    
    public static void clearProcessedChunks() {
        processedChunks.clear();
    }
    
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!Config.OUTPOST_NATURAL_SPAWN_ENABLED.get()) {
            return;
        }
        
        if (event.getLevel() == null || event.getLevel().isClientSide()) {
            return;
        }
        
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        
        ChunkAccess chunk = event.getChunk();
        ChunkPos chunkPos = chunk.getPos();
        
        int startDay = Config.OUTPOST_SPAWN_START_DAY.get();
        long startDayTicks = startDay * 24000L;
        
        long dayTime = level.getGameTime();
        if (dayTime < startDayTicks) {
            return;
        }
        
        if (processedChunks.contains(chunkPos)) {
            return;
        }
        
        processedChunks.add(chunkPos);
        
        double chance = Config.OUTPOST_CHUNK_SPAWN_CHANCE.get();
        Random random = new Random(chunkPos.toLong());
        if (random.nextDouble() >= chance) {
            return;
        }
        
        int x = chunkPos.getMinBlockX() + random.nextInt(16);
        int z = chunkPos.getMinBlockZ() + random.nextInt(16);
        
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        
        BlockPos pos = new BlockPos(x, y, z);
        BlockState groundState = level.getBlockState(pos);
        
        if (groundState.isAir() || groundState.canBeReplaced()) {
            return;
        }
        
        int outpostLevel;
        if (Config.OUTPOST_LEVEL_BY_DISTANCE.get()) {
            BlockPos spawnPos = level.getSharedSpawnPos();
            double distance = Math.sqrt(Math.pow(x - spawnPos.getX(),2) + Math.pow(z - spawnPos.getZ(),2));
            outpostLevel = calculateOutpostLevel(distance);
        } else {
            outpostLevel = 1;
        }
        
        BlockState outpostState = getOutpostBlockState(outpostLevel);
        if (outpostState != null) {
            level.setBlock(pos.above(), outpostState, 2);
        }
    }
    
    private static int calculateOutpostLevel(double distance) {
        if (distance < 1000) {
            return 1;
        } else if (distance < 3000) {
            return 2;
        } else if (distance < 6000) {
            return 3;
        } else if (distance < 10000) {
            return 4;
        } else if (distance < 15000) {
            return 5;
        } else if (distance < 20000) {
            return 6;
        } else {
            return 7;
        }
    }
    
    private static BlockState getOutpostBlockState(int level) {
        return switch (level) {
            case 1 -> Fragmentsofsound.MONSTER_OUTPOST_1.get().defaultBlockState();
            case 2 -> Fragmentsofsound.MONSTER_OUTPOST_2.get().defaultBlockState();
            case 3 -> Fragmentsofsound.MONSTER_OUTPOST_3.get().defaultBlockState();
            case 4 -> Fragmentsofsound.MONSTER_OUTPOST_4.get().defaultBlockState();
            case 5 -> Fragmentsofsound.MONSTER_OUTPOST_5.get().defaultBlockState();
            case 6 -> Fragmentsofsound.MONSTER_OUTPOST_6.get().defaultBlockState();
            case 7 -> Fragmentsofsound.MONSTER_OUTPOST_7.get().defaultBlockState();
            default -> Fragmentsofsound.MONSTER_OUTPOST_1.get().defaultBlockState();
        };
    }
}
