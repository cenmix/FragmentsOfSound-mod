package org.fuzhou.fragmentsofsound.cinematic;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.fuzhou.fragmentsofsound.Fragmentsofsound;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CinematicManager extends SavedData {
    
    private static final String DATA_NAME = Fragmentsofsound.MODID + "_cinematic";
    
    private boolean day2CinematicPlayed = false;
    private final List<BlockPos> outpostSpawnPositions = new ArrayList<>();
    private int lastCheckedDay = -1;
    
    public CinematicManager() {
    }
    
    public static CinematicManager get(ServerLevel level) {
        DimensionDataStorage storage = level.getDataStorage();
        return storage.computeIfAbsent(CinematicManager::load, CinematicManager::new, DATA_NAME);
    }
    
    public boolean hasDay2CinematicPlayed() {
        return day2CinematicPlayed;
    }
    
    public void setDay2CinematicPlayed(boolean played) {
        this.day2CinematicPlayed = played;
        setDirty();
    }
    
    public int getLastCheckedDay() {
        return lastCheckedDay;
    }
    
    public void setLastCheckedDay(int day) {
        this.lastCheckedDay = day;
        setDirty();
    }
    
    public void addOutpostSpawnPosition(BlockPos pos) {
        if (!outpostSpawnPositions.contains(pos)) {
            outpostSpawnPositions.add(pos);
            setDirty();
        }
    }
    
    public BlockPos getRandomOutpostPosition(Random random) {
        if (outpostSpawnPositions.isEmpty()) {
            return null;
        }
        return outpostSpawnPositions.get(random.nextInt(outpostSpawnPositions.size()));
    }
    
    public List<BlockPos> getOutpostSpawnPositions() {
        return outpostSpawnPositions;
    }
    
    public static CinematicManager load(CompoundTag tag) {
        CinematicManager manager = new CinematicManager();
        manager.day2CinematicPlayed = tag.getBoolean("Day2CinematicPlayed");
        manager.lastCheckedDay = tag.getInt("LastCheckedDay");
        
        ListTag positions = tag.getList("OutpostPositions", 10);
        for (int i = 0; i < positions.size(); i++) {
            CompoundTag posTag = positions.getCompound(i);
            manager.outpostSpawnPositions.add(new BlockPos(
                posTag.getInt("X"),
                posTag.getInt("Y"),
                posTag.getInt("Z")
            ));
        }
        
        return manager;
    }
    
    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putBoolean("Day2CinematicPlayed", day2CinematicPlayed);
        tag.putInt("LastCheckedDay", lastCheckedDay);
        
        ListTag positions = new ListTag();
        for (BlockPos pos : outpostSpawnPositions) {
            CompoundTag posTag = new CompoundTag();
            posTag.putInt("X", pos.getX());
            posTag.putInt("Y", pos.getY());
            posTag.putInt("Z", pos.getZ());
            positions.add(posTag);
        }
        tag.put("OutpostPositions", positions);
        
        return tag;
    }
}
