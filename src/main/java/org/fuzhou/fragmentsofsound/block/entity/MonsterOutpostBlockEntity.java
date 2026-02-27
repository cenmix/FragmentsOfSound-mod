package org.fuzhou.fragmentsofsound.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.fuzhou.fragmentsofsound.Config;
import org.fuzhou.fragmentsofsound.block.MonsterOutpostBlock;
import org.fuzhou.fragmentsofsound.monster.IMonsterConfig;
import org.fuzhou.fragmentsofsound.monster.MonsterConfigManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MonsterOutpostBlockEntity extends BlockEntity {
    
    private final int outpostLevel;
    private final List<UUID> spawnedMonsters = new ArrayList<>();
    private String monsterConfigKey = "zombie";
    
    public MonsterOutpostBlockEntity(BlockPos pos, BlockState state) {
        this(pos, state, 1);
    }
    
    public MonsterOutpostBlockEntity(BlockPos pos, BlockState state, int outpostLevel) {
        super(BlockEntityTypes.MONSTER_OUTPOST.get(), pos, state);
        this.outpostLevel = outpostLevel;
    }
    
    @Override
    public void onLoad() {
        Level level = getLevel();
        if (level instanceof ServerLevel serverLevel) {
            if (spawnedMonsters.isEmpty()) {
                spawnAllMonsters(serverLevel);
                setChanged();
            }
        }
    }
    
    @Override
    public void onChunkUnloaded() {
        Level level = getLevel();
        if (level instanceof ServerLevel serverLevel) {
            removeAllMonsters(serverLevel);
            spawnedMonsters.clear();
            setChanged();
        }
    }
    
    public static void tick(Level level, BlockPos pos, BlockState state, MonsterOutpostBlockEntity entity) {
        if (level.isClientSide) return;
        
        ServerLevel serverLevel = (ServerLevel) level;
        entity.cleanupDeadMonsters(serverLevel);
    }
    
    private void spawnAllMonsters(ServerLevel serverLevel) {
        IMonsterConfig config = MonsterConfigManager.getConfig(monsterConfigKey);
        if (config == null) config = MonsterConfigManager.getDefaultConfig();
        
        int spawnCount = getSpawnCount();
        int spawnRadius = getSpawnRadius();
        
        for (int i = 0; i < spawnCount; i++) {
            BlockPos spawnPos = findSpawnPosition(serverLevel, spawnRadius);
            if (spawnPos != null) {
                Mob monster = config.getEntityType().create(serverLevel);
                if (monster != null) {
                    monster.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
                    configureMonster(monster);
                    serverLevel.addFreshEntity(monster);
                    spawnedMonsters.add(monster.getUUID());
                }
            }
        }
    }
    
    private int getSpawnCount() {
        if (outpostLevel >= 7) {
            return Config.OUTPOST_MAX_LEVEL_SPAWN_COUNT.get();
        }
        return Config.OUTPOST_BASE_SPAWN_COUNT.get() + (outpostLevel - 1) * Config.OUTPOST_SPAWN_COUNT_INCREMENT.get();
    }
    
    private int getSpawnRadius() {
        return Config.OUTPOST_SPAWN_RADIUS_BASE.get() + (outpostLevel - 1) * Config.OUTPOST_SPAWN_RADIUS_INCREMENT.get();
    }
    
    private void configureMonster(Mob monster) {
        IMonsterConfig config = MonsterConfigManager.getConfig(monsterConfigKey);
        if (config != null) {
            config.configureMonster(monster, outpostLevel);
        }
        
        if (monster.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH) != null) {
            double baseHealth = monster.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH).getBaseValue();
            monster.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH).setBaseValue(baseHealth * Config.OUTPOST_MONSTER_HEALTH_MULTIPLIER.get());
            monster.setHealth((float) (baseHealth * Config.OUTPOST_MONSTER_HEALTH_MULTIPLIER.get()));
        }
        
        if (monster.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE) != null) {
            double baseDamage = monster.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE).getBaseValue();
            monster.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE).setBaseValue(baseDamage * Config.OUTPOST_MONSTER_DAMAGE_MULTIPLIER.get());
        }
        
        if (monster.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED) != null) {
            double baseSpeed = monster.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED).getBaseValue();
            monster.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED).setBaseValue(baseSpeed * Config.OUTPOST_MONSTER_SPEED_MULTIPLIER.get());
        }
    }
    
    private BlockPos findSpawnPosition(ServerLevel level, int radius) {
        BlockPos center = this.worldPosition;
        
        for (int attempt = 0; attempt < 20; attempt++) {
            int offsetX = level.getRandom().nextInt(radius * 2) - radius;
            int offsetZ = level.getRandom().nextInt(radius * 2) - radius;
            BlockPos pos = new BlockPos(center.getX() + offsetX, center.getY(), center.getZ() + offsetZ);
            
            for (int y = -5; y <= 5; y++) {
                BlockPos checkPos = pos.offset(0, y, 0);
                if (level.getBlockState(checkPos).isAir() && !level.getBlockState(checkPos.below()).isAir()) {
                    return checkPos;
                }
            }
        }
        return null;
    }
    
    private void removeAllMonsters(ServerLevel serverLevel) {
        for (UUID uuid : spawnedMonsters) {
            var entity = serverLevel.getEntity(uuid);
            if (entity != null && entity.isAlive()) {
                entity.discard();
            }
        }
    }
    
    private void cleanupDeadMonsters(ServerLevel serverLevel) {
        spawnedMonsters.removeIf(uuid -> {
            var entity = serverLevel.getEntity(uuid);
            return entity == null || !entity.isAlive();
        });
    }
    
    public int getOutpostLevel() {
        return outpostLevel;
    }
    
    public void setMonsterConfig(String configKey) {
        this.monsterConfigKey = configKey;
        setChanged();
    }
    
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putString("MonsterConfig", monsterConfigKey);
    }
    
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("MonsterConfig")) {
            monsterConfigKey = tag.getString("MonsterConfig");
        }
    }
}
