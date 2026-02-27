package org.fuzhou.fragmentsofsound.compat;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;

public class EpicFightCompat {

    private static boolean epicFightLoaded = false;
    private static boolean checked = false;

    public static boolean isEpicFightLoaded() {
        if (!checked) {
            epicFightLoaded = ModList.get().isLoaded("epicfight");
            checked = true;
        }
        return epicFightLoaded;
    }

    public static boolean isPlayerInAnimation(Player player) {
        if (!isEpicFightLoaded()) {
            return false;
        }
        
        try {
            return checkEpicFightAnimation(player);
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean checkEpicFightAnimation(Player player) {
        try {
            Class<?> playerDataClass = Class.forName("yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch");
            Class<?> epicFightCapabilitiesClass = Class.forName("yesman.epicfight.world.capabilities.EpicFightCapabilities");
            
            Object entityPatch = epicFightCapabilitiesClass
                .getMethod("getEntityPatch", net.minecraft.world.entity.Entity.class, Class.class)
                .invoke(null, player, playerDataClass);
            
            if (entityPatch != null) {
                Class<?> stateClass = Class.forName("yesman.epicfight.gameasset.Animations");
                Object playerState = playerDataClass.getMethod("getPlayerState").invoke(entityPatch);
                
                return playerState != null;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public static void cancelCurrentAnimation(Player player) {
        if (!isEpicFightLoaded()) {
            return;
        }
        
        try {
            cancelAnimation(player);
        } catch (Exception e) {
        }
    }

    private static void cancelAnimation(Player player) {
        try {
            Class<?> playerDataClass = Class.forName("yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch");
            Class<?> epicFightCapabilitiesClass = Class.forName("yesman.epicfight.world.capabilities.EpicFightCapabilities");
            
            Object entityPatch = epicFightCapabilitiesClass
                .getMethod("getEntityPatch", net.minecraft.world.entity.Entity.class, Class.class)
                .invoke(null, player, playerDataClass);
            
            if (entityPatch != null) {
                playerDataClass.getMethod("resetLivingState").invoke(entityPatch);
            }
        } catch (Exception e) {
        }
    }
}
