package org.fuzhou.fragmentsofsound;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.IntValue OUTPOST_BASE_SPAWN_COUNT;
    public static final ForgeConfigSpec.IntValue OUTPOST_SPAWN_COUNT_INCREMENT;
    public static final ForgeConfigSpec.IntValue OUTPOST_MAX_LEVEL_SPAWN_COUNT;
    public static final ForgeConfigSpec.IntValue OUTPOST_SPAWN_RADIUS_BASE;
    public static final ForgeConfigSpec.IntValue OUTPOST_SPAWN_RADIUS_INCREMENT;
    public static final ForgeConfigSpec.DoubleValue OUTPOST_MONSTER_HEALTH_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue OUTPOST_MONSTER_DAMAGE_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue OUTPOST_MONSTER_SPEED_MULTIPLIER;
    
    public static final ForgeConfigSpec.BooleanValue OUTPOST_NATURAL_SPAWN_ENABLED;
    public static final ForgeConfigSpec.DoubleValue OUTPOST_CHUNK_SPAWN_CHANCE;
    public static final ForgeConfigSpec.IntValue OUTPOST_SPAWN_START_DAY;
    public static final ForgeConfigSpec.BooleanValue OUTPOST_LEVEL_BY_DISTANCE;
    
    public static final ForgeConfigSpec.BooleanValue CG_ENABLED;
    public static final ForgeConfigSpec.BooleanValue CG_DAY2_CINEMATIC_ENABLED;
    public static final ForgeConfigSpec.IntValue CG_FADE_OUT_DURATION;
    public static final ForgeConfigSpec.IntValue CG_CINEMATIC_DURATION;
    public static final ForgeConfigSpec.IntValue CG_FADE_IN_DURATION;
    public static final ForgeConfigSpec.BooleanValue CG_HIDE_GUI;
    public static final ForgeConfigSpec.BooleanValue CG_DISABLE_INPUT;
    
    public static final ForgeConfigSpec.BooleanValue PORTAL_ENABLED;
    public static final ForgeConfigSpec.IntValue PORTAL_COUNT;
    public static final ForgeConfigSpec.DoubleValue PORTAL_RADIUS;
    public static final ForgeConfigSpec.IntValue PORTAL_HEIGHT_OFFSET;
    public static final ForgeConfigSpec.BooleanValue PORTAL_AVOID_WATER;

    static {
        BUILDER.comment("=== Monster Outpost Settings ===");
        OUTPOST_BASE_SPAWN_COUNT = BUILDER
            .comment("Base monster spawn count for outpost level 1 (default: 3)")
            .defineInRange("outpost_base_spawn_count", 3, 1, 20);
        OUTPOST_SPAWN_COUNT_INCREMENT = BUILDER
            .comment("Monster count increment per outpost level (default: 1)")
            .defineInRange("outpost_spawn_count_increment", 1, 0, 5);
        OUTPOST_MAX_LEVEL_SPAWN_COUNT = BUILDER
            .comment("Monster spawn count for max level outpost (default: 10)")
            .defineInRange("outpost_max_level_spawn_count", 10, 1, 30);
        OUTPOST_SPAWN_RADIUS_BASE = BUILDER
            .comment("Base spawn radius for outpost (default: 6)")
            .defineInRange("outpost_spawn_radius_base", 6, 3, 20);
        OUTPOST_SPAWN_RADIUS_INCREMENT = BUILDER
            .comment("Spawn radius increment per outpost level (default: 3)")
            .defineInRange("outpost_spawn_radius_increment", 3, 0, 10);
        OUTPOST_MONSTER_HEALTH_MULTIPLIER = BUILDER
            .comment("Health multiplier for outpost monsters (default: 1.5)")
            .defineInRange("outpost_monster_health_multiplier", 1.5, 0.5, 5.0);
        OUTPOST_MONSTER_DAMAGE_MULTIPLIER = BUILDER
            .comment("Damage multiplier for outpost monsters (default: 1.2)")
            .defineInRange("outpost_monster_damage_multiplier", 1.2, 0.5, 5.0);
        OUTPOST_MONSTER_SPEED_MULTIPLIER = BUILDER
            .comment("Speed multiplier for outpost monsters (default: 1.1)")
            .defineInRange("outpost_monster_speed_multiplier", 1.1, 0.5, 3.0);
        
        BUILDER.comment("=== Outpost World Generation Settings ===");
        OUTPOST_NATURAL_SPAWN_ENABLED = BUILDER
            .comment("Enable natural outpost spawning (default: true)")
            .define("outpost_natural_spawn_enabled", true);
        OUTPOST_CHUNK_SPAWN_CHANCE = BUILDER
            .comment("Chance for an outpost to spawn per chunk (default: 0.2 = 20%)")
            .defineInRange("outpost_chunk_spawn_chance", 0.2, 0.0, 1.0);
        OUTPOST_SPAWN_START_DAY = BUILDER
            .comment("Day when outposts start spawning naturally (default: 2)")
            .defineInRange("outpost_spawn_start_day", 2, 1, 30);
        OUTPOST_LEVEL_BY_DISTANCE = BUILDER
            .comment("Outpost level based on distance from spawn (default: true)")
            .define("outpost_level_by_distance", true);
        
        BUILDER.comment("=== Cinematic Settings ===");
        CG_ENABLED = BUILDER
            .comment("Enable cinematic system (default: true)")
            .define("cg_enabled", true);
        CG_DAY2_CINEMATIC_ENABLED = BUILDER
            .comment("Enable day 2 cinematic (default: true)")
            .define("cg_day2_cinematic_enabled", true);
        CG_FADE_OUT_DURATION = BUILDER
            .comment("Black screen fade out duration in ticks (default: 40 = 2 seconds)")
            .defineInRange("cg_fade_out_duration", 40, 10, 200);
        CG_CINEMATIC_DURATION = BUILDER
            .comment("Cinematic duration in ticks (default: 200 = 10 seconds)")
            .defineInRange("cg_cinematic_duration", 200, 50, 600);
        CG_FADE_IN_DURATION = BUILDER
            .comment("Black screen fade in duration in ticks (default: 40 = 2 seconds)")
            .defineInRange("cg_fade_in_duration", 40, 10, 200);
        CG_HIDE_GUI = BUILDER
            .comment("Hide GUI during cinematic (default: true)")
            .define("cg_hide_gui", true);
        CG_DISABLE_INPUT = BUILDER
            .comment("Disable player input during cinematic (default: true)")
            .define("cg_disable_input", true);
        
        BUILDER.comment("=== Portal Settings ===");
        PORTAL_ENABLED = BUILDER
            .comment("Enable portal spawning (default: true)")
            .define("portal_enabled", true);
        PORTAL_COUNT = BUILDER
            .comment("Number of portals to spawn (default: 5)")
            .defineInRange("portal_count", 5, 1, 12);
        PORTAL_RADIUS = BUILDER
            .comment("Distance from spawn for portals (default: 25000)")
            .defineInRange("portal_radius", 25000.0, 1000.0, 100000.0);
        PORTAL_HEIGHT_OFFSET = BUILDER
            .comment("Height offset above ground for portals (default: 2)")
            .defineInRange("portal_height_offset", 2, 0, 10);
        PORTAL_AVOID_WATER = BUILDER
            .comment("Avoid spawning portals in water (default: true)")
            .define("portal_avoid_water", true);
    }

    static final ForgeConfigSpec SPEC = BUILDER.build();
}
