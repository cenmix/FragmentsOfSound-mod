package org.fuzhou.fragmentsofsound.cinematic;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class CinematicData {
    
    public enum CinematicType {
        OUTPOST_SPAWN,
        CUSTOM
    }
    
    private final CinematicType type;
    private BlockPos targetPosition;
    private ResourceLocation dimension;
    private int duration;
    private float startHeight;
    private float endHeight;
    private float rotationSpeed;
    private boolean applyChanges;
    private int outpostLevel;
    private String monsterConfig = "zombie";
    
    public CinematicData(CinematicType type) {
        this.type = type;
        this.duration = 200;
        this.startHeight = 5.0f;
        this.endHeight = 10.0f;
        this.rotationSpeed = 1.8f;
        this.applyChanges = true;
        this.outpostLevel = 1;
        this.monsterConfig = "zombie";
    }
    
    public static CinematicData createOutpostSpawn(BlockPos pos, ResourceLocation dimension, int level, String monsterConfig, boolean applyChanges) {
        CinematicData data = new CinematicData(CinematicType.OUTPOST_SPAWN);
        data.targetPosition = pos;
        data.dimension = dimension;
        data.outpostLevel = level;
        data.monsterConfig = monsterConfig;
        data.applyChanges = applyChanges;
        return data;
    }
    
    public CinematicType getType() {
        return type;
    }
    
    public BlockPos getTargetPosition() {
        return targetPosition;
    }
    
    public void setTargetPosition(BlockPos targetPosition) {
        this.targetPosition = targetPosition;
    }
    
    public ResourceLocation getDimension() {
        return dimension;
    }
    
    public void setDimension(ResourceLocation dimension) {
        this.dimension = dimension;
    }
    
    public int getDuration() {
        return duration;
    }
    
    public void setDuration(int duration) {
        this.duration = duration;
    }
    
    public float getStartHeight() {
        return startHeight;
    }
    
    public void setStartHeight(float startHeight) {
        this.startHeight = startHeight;
    }
    
    public float getEndHeight() {
        return endHeight;
    }
    
    public void setEndHeight(float endHeight) {
        this.endHeight = endHeight;
    }
    
    public float getRotationSpeed() {
        return rotationSpeed;
    }
    
    public void setRotationSpeed(float rotationSpeed) {
        this.rotationSpeed = rotationSpeed;
    }
    
    public boolean shouldApplyChanges() {
        return applyChanges;
    }
    
    public void setApplyChanges(boolean applyChanges) {
        this.applyChanges = applyChanges;
    }
    
    public int getOutpostLevel() {
        return outpostLevel;
    }
    
    public int getMonsterLevel() {
        return 0;
    }
    
    public void setOutpostLevel(int outpostLevel) {
        this.outpostLevel = outpostLevel;
    }
    
    public String getMonsterConfig() {
        return monsterConfig;
    }
    
    public void setMonsterConfig(String monsterConfig) {
        this.monsterConfig = monsterConfig;
    }
    
    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Type", type.name());
        if (targetPosition != null) {
            tag.putInt("PosX", targetPosition.getX());
            tag.putInt("PosY", targetPosition.getY());
            tag.putInt("PosZ", targetPosition.getZ());
        }
        if (dimension != null) {
            tag.putString("Dimension", dimension.toString());
        }
        tag.putInt("Duration", duration);
        tag.putFloat("StartHeight", startHeight);
        tag.putFloat("EndHeight", endHeight);
        tag.putFloat("RotationSpeed", rotationSpeed);
        tag.putBoolean("ApplyChanges", applyChanges);
        tag.putInt("OutpostLevel", outpostLevel);
        tag.putString("MonsterConfig", monsterConfig);
        return tag;
    }
    
    public static CinematicData load(CompoundTag tag) {
        CinematicType type = CinematicType.valueOf(tag.getString("Type"));
        CinematicData data = new CinematicData(type);
        
        if (tag.contains("PosX")) {
            data.targetPosition = new BlockPos(
                tag.getInt("PosX"),
                tag.getInt("PosY"),
                tag.getInt("PosZ")
            );
        }
        if (tag.contains("Dimension")) {
            data.dimension = ResourceLocation.tryParse(tag.getString("Dimension"));
        }
        data.duration = tag.getInt("Duration");
        data.startHeight = tag.getFloat("StartHeight");
        data.endHeight = tag.getFloat("EndHeight");
        data.rotationSpeed = tag.getFloat("RotationSpeed");
        data.applyChanges = tag.getBoolean("ApplyChanges");
        data.outpostLevel = tag.getInt("OutpostLevel");
        data.monsterConfig = tag.getString("MonsterConfig");
        
        return data;
    }
}
