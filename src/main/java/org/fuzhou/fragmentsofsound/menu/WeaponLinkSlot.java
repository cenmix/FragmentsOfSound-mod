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

public class WeaponLinkSlot extends SlotItemHandler {

    private final boolean isLocked;

    public WeaponLinkSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, boolean isLocked) {
        super(itemHandler, index, xPosition, yPosition);
        this.isLocked = isLocked;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (isLocked) return false;
        
        if (stack.is(ModItems.WEAPONS)) return true;
        
        if (stack.getItem() instanceof SwordItem) return true;
        if (stack.getItem() instanceof TieredItem) return true;
        if (stack.getItem() instanceof BowItem) return true;
        if (stack.getItem() instanceof CrossbowItem) return true;
        if (stack.getItem() instanceof TridentItem) return true;
        if (stack.getItem() instanceof ShieldItem) return true;
        
        return false;
    }

    @Override
    public boolean mayPickup(net.minecraft.world.entity.player.Player player) {
        return !isLocked;
    }

    public boolean isLocked() {
        return isLocked;
    }
}
