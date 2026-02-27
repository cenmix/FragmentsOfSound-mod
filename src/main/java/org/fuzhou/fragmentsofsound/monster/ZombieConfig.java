package org.fuzhou.fragmentsofsound.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Zombie;

public class ZombieConfig implements IMonsterConfig {
    
    private static final float[] HEALTH_MULTIPLIERS = {
        0.7f,
        1.1f,
        1.5f,
        1.9f,
        2.3f,
        2.7f,
        3.5f
    };
    
    @Override
    public EntityType<? extends Mob> getEntityType() {
        return EntityType.ZOMBIE;
    }
    
    @Override
    public float getHealthMultiplier(int outpostLevel) {
        int index = Math.min(Math.max(outpostLevel - 1, 0), HEALTH_MULTIPLIERS.length - 1);
        return HEALTH_MULTIPLIERS[index];
    }
    
    @Override
    public int getMinMonsters(int outpostLevel) {
        return 3;
    }
    
    @Override
    public int getMaxMonsters(int outpostLevel) {
        return Math.min(3 + outpostLevel - 1, 10);
    }
}
