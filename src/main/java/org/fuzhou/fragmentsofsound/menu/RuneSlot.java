package org.fuzhou.fragmentsofsound.menu;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.fuzhou.fragmentsofsound.item.ArmorBreakRuneItem;
import org.fuzhou.fragmentsofsound.item.ChiselStoneItem;
import org.fuzhou.fragmentsofsound.item.PierceRuneItem;
import org.fuzhou.fragmentsofsound.item.PhysicalRuneItem;

public class RuneSlot extends SlotItemHandler {

    public RuneSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        if (stack.isEmpty()) return false;
        
        return stack.getItem() instanceof PhysicalRuneItem 
            || stack.getItem() instanceof ArmorBreakRuneItem
            || stack.getItem() instanceof PierceRuneItem
            || stack.getItem() instanceof ChiselStoneItem;
    }
}
