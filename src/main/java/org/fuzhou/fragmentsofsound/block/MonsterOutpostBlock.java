package org.fuzhou.fragmentsofsound.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.fuzhou.fragmentsofsound.block.entity.BlockEntityTypes;
import org.fuzhou.fragmentsofsound.block.entity.MonsterOutpostBlockEntity;
import org.jetbrains.annotations.Nullable;

public class MonsterOutpostBlock extends BaseEntityBlock {
    
    private final int level;
    
    public MonsterOutpostBlock(Properties properties, int level) {
        super(properties);
        this.level = level;
    }
    
    public int getLevel() {
        return level;
    }
    
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
    
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MonsterOutpostBlockEntity(pos, state, level);
    }
    
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> blockEntityType) {
        return world.isClientSide ? null : createTickerHelper(blockEntityType, BlockEntityTypes.MONSTER_OUTPOST.get(), MonsterOutpostBlockEntity::tick);
    }
}
