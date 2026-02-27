package org.fuzhou.fragmentsofsound.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;

public interface IMonsterConfig {
    EntityType<? extends Mob> getEntityType();
    
    default void configureMonster(Mob monster, int outpostLevel) {
        float healthMultiplier = getHealthMultiplier(outpostLevel);
        double baseHealth = monster.getAttribute(Attributes.MAX_HEALTH).getBaseValue();
        monster.getAttribute(Attributes.MAX_HEALTH).setBaseValue(baseHealth * healthMultiplier);
        monster.setHealth(monster.getMaxHealth());
        
        if (outpostLevel >= 2) {
            int strengthLevel = getStrengthLevel(outpostLevel);
            monster.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, Integer.MAX_VALUE, strengthLevel - 1, false, false));
        }
    }
    
    float getHealthMultiplier(int outpostLevel);
    
    default int getStrengthLevel(int outpostLevel) {
        return Math.max(1, outpostLevel - 1);
    }
    
    int getMinMonsters(int outpostLevel);
    
    int getMaxMonsters(int outpostLevel);
}
