package org.fuzhou.fragmentsofsound.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.fuzhou.fragmentsofsound.Fragmentsofsound;
import org.fuzhou.fragmentsofsound.block.CorruptBlock;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = Fragmentsofsound.MODID)
public class CorruptBlockEventHandler {
    private static final UUID SPEED_MODIFIER_ID = UUID.fromString("b2c3d4e5-f6a7-8901-bcde-f23456789012");
    private static final AttributeModifier SPEED_MODIFIER = new AttributeModifier(
        SPEED_MODIFIER_ID,
        "corrupt_speed_boost",
        0.05,
        AttributeModifier.Operation.MULTIPLY_TOTAL
    );

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.player.level().isClientSide) return;
        
        Player player = event.player;
        Level level = player.level();
        BlockPos pos = player.blockPosition();
        BlockState state = level.getBlockState(pos);
        
        AttributeInstance speedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttribute == null) return;
        
        if (state.getBlock() instanceof CorruptBlock) {
            if (speedAttribute.getModifier(SPEED_MODIFIER_ID) == null) {
                speedAttribute.addTransientModifier(SPEED_MODIFIER);
            }
            
            handleDamage(player, level);
        } else {
            if (speedAttribute.getModifier(SPEED_MODIFIER_ID) != null) {
                speedAttribute.removeModifier(SPEED_MODIFIER_ID);
            }
        }
    }
    
    private static void handleDamage(Player player, Level level) {
        String COOLDOWN_TAG = "fragmentsofsound.corrupt_damage_cooldown";
        int lastDamageTick = player.getPersistentData().getInt(COOLDOWN_TAG);
        long currentTick = level.getGameTime();
        
        if (currentTick - lastDamageTick >= 100) {
            player.getPersistentData().putInt(COOLDOWN_TAG, (int) currentTick);
            player.hurt(level.damageSources().magic(), player.getMaxHealth() * 0.01f);
        }
    }
}
