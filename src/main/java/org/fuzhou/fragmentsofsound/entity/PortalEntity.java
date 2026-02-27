package org.fuzhou.fragmentsofsound.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.util.FakePlayer;
import org.fuzhou.fragmentsofsound.Config;
import org.fuzhou.fragmentsofsound.Fragmentsofsound;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class PortalEntity extends Entity {
    
    private static final Logger LOGGER = Fragmentsofsound.LOGGER;
    
    private static final List<BlockPos> portalPositions = new ArrayList<>();
    
    public PortalEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }
    
    @Override
    protected void defineSynchedData() {
    }
    
    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
    }
    
    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
    }
    
    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
    
    @Override
    public void tick() {
        super.tick();
    }
    
    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        double d = this.getBoundingBox().getSize() * 64.0;
        return distance < d * d;
    }
    
    public static List<BlockPos> getPortalPositions() {
        return new ArrayList<>(portalPositions);
    }
    
    public static void spawnPortalsAroundSpawn(ServerLevel level) {
        if (!Config.PORTAL_ENABLED.get()) {
            LOGGER.info("[破碎余声] 传送门生成已禁用");
            return;
        }
        
        portalPositions.clear();
        
        double radius = Config.PORTAL_RADIUS.get();
        int count = Config.PORTAL_COUNT.get();
        int heightOffset = Config.PORTAL_HEIGHT_OFFSET.get();
        boolean avoidWater = Config.PORTAL_AVOID_WATER.get();
        
        BlockPos spawnPos = level.getSharedSpawnPos();
        double centerX = spawnPos.getX() + 0.5;
        double centerZ = spawnPos.getZ() + 0.5;
        
        LOGGER.info("[破碎余声] 开始生成传送门，出生点: ({}, {})", centerX, centerZ);
        LOGGER.info("[破碎余声] 传送门半径: {}, 数量: {}", radius, count);
        
        for (int i = 0; i < count; i++) {
            double angle = 2 * Math.PI * i / count;
            double x = centerX + radius * Math.cos(angle);
            double z = centerZ + radius * Math.sin(angle);
            
            int chunkX = (int) x >> 4;
            int chunkZ = (int) z >> 4;
            
            ChunkAccess chunk = level.getChunk(chunkX, chunkZ, ChunkStatus.FULL, true);
            if (chunk == null) {
                LOGGER.warn("[破碎余声] 无法加载区块 ({}, {})", chunkX, chunkZ);
                continue;
            }
            
            int y = chunk.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) x & 15, (int) z & 15);
            
            BlockPos groundPos = new BlockPos((int) x, y - 1, (int) z);
            BlockState groundState = level.getBlockState(groundPos);
            
            while ((groundState.isAir() || (avoidWater && groundState.getFluidState().getType() != Fluids.EMPTY.defaultFluidState().getType())) && y > level.getMinBuildHeight()) {
                y--;
                groundPos = new BlockPos((int) x, y - 1, (int) z);
                groundState = level.getBlockState(groundPos);
            }
            
            if (avoidWater) {
                if (groundState.getFluidState().getType() != Fluids.EMPTY.defaultFluidState().getType() || 
                    level.getBlockState(new BlockPos((int) x, y, (int) z)).getFluidState().getType() != Fluids.EMPTY.defaultFluidState().getType() ||
                    level.getBlockState(new BlockPos((int) x, y + 1, (int) z)).getFluidState().getType() != Fluids.EMPTY.defaultFluidState().getType()) {
                    LOGGER.warn("[破碎余声] 传送门 #{} 位置在水中，跳过", i + 1);
                    continue;
                }
            }
            
            int finalY = y + heightOffset;
            
            BlockPos pos = new BlockPos((int) x, finalY, (int) z);
            portalPositions.add(pos);
            
            PortalEntity portal = ModEntities.PORTAL.get().create(level);
            if (portal != null) {
                portal.setPos(x + 0.5, finalY, z + 0.5);
                level.addFreshEntity(portal);
                
                LOGGER.info("[破碎余声] 传送门 #{} 生成于: ({}, {}, {})", i + 1, (int) x, finalY, (int) z);
            }
        }
        
        LOGGER.info("[破碎余声] 所有传送门生成完成");
    }
}
