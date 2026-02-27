package org.fuzhou.fragmentsofsound.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Creeper;

import java.util.HashMap;
import java.util.Map;

public class MonsterConfigManager {
    
    private static final Map<String, IMonsterConfig> CONFIGS = new HashMap<>();
    
    static {
        registerConfig("zombie", new ZombieConfig());
    }
    
    public static void registerConfig(String name, IMonsterConfig config) {
        CONFIGS.put(name, config);
    }
    
    public static IMonsterConfig getConfig(String name) {
        return CONFIGS.get(name);
    }
    
    public static IMonsterConfig getDefaultConfig() {
        return CONFIGS.get("zombie");
    }
}
