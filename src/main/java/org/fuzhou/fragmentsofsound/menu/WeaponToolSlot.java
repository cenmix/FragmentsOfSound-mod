package org.fuzhou.fragmentsofsound.menu;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.ShieldItem;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.fuzhou.fragmentsofsound.item.ModItems;
import org.fuzhou.fragmentsofsound.item.ChiselStoneItem;

public class WeaponToolSlot extends SlotItemHandler {

    public WeaponToolSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        if (stack.isEmpty()) return false;
        
        if (stack.getItem() instanceof ChiselStoneItem) return true;
        
        if (stack.is(ModItems.WEAPONS)) return true;
        
        if (stack.getItem() instanceof SwordItem) return true;
        if (stack.getItem() instanceof TieredItem) return true;
        if (stack.getItem() instanceof BowItem) return true;
        if (stack.getItem() instanceof CrossbowItem) return true;
        if (stack.getItem() instanceof TridentItem) return true;
        if (stack.getItem() instanceof ShieldItem) return true;
        
        return false;
    }
}
