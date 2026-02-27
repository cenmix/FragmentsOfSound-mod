package org.fuzhou.fragmentsofsound.event;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.fuzhou.fragmentsofsound.Fragmentsofsound;
import org.fuzhou.fragmentsofsound.entity.PortalEntity;

@Mod.EventBusSubscriber(modid = Fragmentsofsound.MODID)
public class PortalSpawnHandler {
    
    private static boolean spawned = false;
    
    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        
        if (level.dimension() != Level.OVERWORLD) {
            return;
        }
        
        if (spawned) {
            return;
        }
        spawned = true;
        
        PortalEntity.spawnPortalsAroundSpawn(level);
    }
}
