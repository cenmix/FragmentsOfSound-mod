package org.fuzhou.fragmentsofsound.event;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.fuzhou.fragmentsofsound.Fragmentsofsound;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = Fragmentsofsound.MODID)
public class PlayerHealthHandler {
    private static final UUID BASE_HEALTH_MODIFIER = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
    private static final String HEALTH_INITIALIZED = "fragmentsofsound.health_initialized";

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof Player) {
            setBaseHealth((Player) event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof Player) {
            setBaseHealth((Player) event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.getEntity() instanceof Player && event.getOriginal() instanceof Player) {
            Player player = (Player) event.getEntity();
            Player original = (Player) event.getOriginal();
            if (!event.isWasDeath()) {
                player.setHealth(original.getHealth());
            }
            setBaseHealth(player);
        }
    }

    private static void setBaseHealth(Player player) {
        AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth == null) return;

        if (maxHealth.getModifier(BASE_HEALTH_MODIFIER) == null) {
            AttributeModifier modifier = new AttributeModifier(
                BASE_HEALTH_MODIFIER,
                "base_health_bonus",
                80.0,
                AttributeModifier.Operation.ADDITION
            );
            maxHealth.addPermanentModifier(modifier);
        }

        if (!player.getPersistentData().getBoolean(HEALTH_INITIALIZED)) {
            player.setHealth(100.0f);
            player.getPersistentData().putBoolean(HEALTH_INITIALIZED, true);
        }
    }
}
