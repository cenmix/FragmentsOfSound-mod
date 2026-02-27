package org.fuzhou.fragmentsofsound.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import org.fuzhou.fragmentsofsound.item.ChiselStoneItem;

import java.util.List;

public class ChiselStoneOreBlock extends Block {
    private final int level;

    public ChiselStoneOreBlock(Properties properties, int level) {
        super(properties);
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
    
    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        
        for (ItemStack stack : drops) {
            if (stack.getItem() instanceof ChiselStoneItem) {
                ChiselStoneItem.setPurity(stack, ChiselStoneItem.generateRandomPurity(builder.getLevel().random));
            }
        }
        
        return drops;
    }
}
