package org.fuzhou.fragmentsofsound.effect;

import java.util.UUID;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class CorrosionEffect extends MobEffect {
    private static final String CORROSION_TAG = "fragmentsofsound.corrosion_count";
    private static final double HEALTH_REDUCTION_PERCENT = 0.02;
    
    public CorrosionEffect() {
        super(MobEffectCategory.HARMFUL, 0x4A7C2B);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (!entity.level().isClientSide && entity instanceof Player player) {
            reduceMaxHealth(player);
        }
    }
    
    private void reduceMaxHealth(Player player) {
        AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth == null) return;
        
        double currentMaxHealth = player.getMaxHealth();
        double reductionAmount = currentMaxHealth * HEALTH_REDUCTION_PERCENT;
        
        int count = player.getPersistentData().getInt(CORROSION_TAG);
        count++;
        player.getPersistentData().putInt(CORROSION_TAG, count);
        
        UUID modifierId = UUID.nameUUIDFromBytes(("corrosion_" + count).getBytes());
        AttributeModifier modifier = new AttributeModifier(
            modifierId,
            "corrosion_health_reduction_" + count,
            -reductionAmount,
            AttributeModifier.Operation.ADDITION
        );
        
        if (maxHealth.getModifier(modifierId) == null) {
            maxHealth.addPermanentModifier(modifier);
        }
        
        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration <= 1;
    }
}
