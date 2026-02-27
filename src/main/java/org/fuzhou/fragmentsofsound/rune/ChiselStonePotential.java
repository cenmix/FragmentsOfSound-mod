package org.fuzhou.fragmentsofsound.rune;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.fuzhou.fragmentsofsound.item.ChiselStoneItem;

public class ChiselStonePotential {

    public static final String POTENTIAL_TAG = "PotentialData";
    public static final String MAX_POTENTIAL = "MaxPotential";
    public static final String ALLOCATED_POTENTIAL = "AllocatedPotential";
    public static final String UNALLOCATED_PURITY = "UnallocatedPurity";

    public static boolean hasPotentialData(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains(POTENTIAL_TAG);
    }

    public static CompoundTag getOrCreatePotentialData(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains(POTENTIAL_TAG)) {
            CompoundTag potentialTag = new CompoundTag();
            int level = getChiselStoneLevel(stack);
            int maxPotential = level + 4;
            potentialTag.putInt(MAX_POTENTIAL, maxPotential);
            potentialTag.putInt(ALLOCATED_POTENTIAL, 0);
            potentialTag.putInt(UNALLOCATED_PURITY, 0);
            tag.put(POTENTIAL_TAG, potentialTag);
        }
        return tag.getCompound(POTENTIAL_TAG);
    }

    public static int getChiselStoneLevel(ItemStack stack) {
        if (stack.getItem() instanceof ChiselStoneItem chiselStone) {
            return chiselStone.getChiselLevel();
        }
        return 0;
    }

    public static int getMaxPotential(ItemStack stack) {
        CompoundTag data = getOrCreatePotentialData(stack);
        return data.getInt(MAX_POTENTIAL);
    }

    public static int getAllocatedPotential(ItemStack stack) {
        CompoundTag data = getOrCreatePotentialData(stack);
        return data.getInt(ALLOCATED_POTENTIAL);
    }

    public static int getUnallocatedPurity(ItemStack stack) {
        CompoundTag data = getOrCreatePotentialData(stack);
        return data.getInt(UNALLOCATED_PURITY);
    }

    public static int getRemainingPotential(ItemStack stack) {
        return getMaxPotential(stack) - getAllocatedPotential(stack);
    }

    public static void addPurity(ItemStack stack, int amount) {
        CompoundTag data = getOrCreatePotentialData(stack);
        int current = data.getInt(UNALLOCATED_PURITY);
        data.putInt(UNALLOCATED_PURITY, current + amount);
    }

    public static boolean allocatePotential(ItemStack stack, int amount) {
        CompoundTag data = getOrCreatePotentialData(stack);
        int unallocated = data.getInt(UNALLOCATED_PURITY);
        int allocated = data.getInt(ALLOCATED_POTENTIAL);
        int max = data.getInt(MAX_POTENTIAL);
        
        if (unallocated >= amount && allocated + amount <= max) {
            data.putInt(UNALLOCATED_PURITY, unallocated - amount);
            data.putInt(ALLOCATED_POTENTIAL, allocated + amount);
            return true;
        }
        return false;
    }

    public static boolean deallocatePotential(ItemStack stack, int amount) {
        CompoundTag data = getOrCreatePotentialData(stack);
        int allocated = data.getInt(ALLOCATED_POTENTIAL);
        
        if (allocated >= amount) {
            int unallocated = data.getInt(UNALLOCATED_PURITY);
            data.putInt(ALLOCATED_POTENTIAL, allocated - amount);
            data.putInt(UNALLOCATED_PURITY, unallocated + amount);
            return true;
        }
        return false;
    }

    public static void initPotentialData(ItemStack stack) {
        getOrCreatePotentialData(stack);
    }
}
