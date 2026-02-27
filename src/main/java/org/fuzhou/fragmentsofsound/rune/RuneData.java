package org.fuzhou.fragmentsofsound.rune;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import org.fuzhou.fragmentsofsound.item.ChiselStoneItem;

import java.util.ArrayList;
import java.util.List;

public class RuneData {

    public static final String RUNE_TAG = "RuneData";
    public static final String RUNES_LIST = "Runes";
    public static final String RUNE_TYPE = "RuneType";
    public static final String RUNE_NAME = "RuneName";
    public static final String ALLOCATED_POTENTIAL = "AllocatedPotential";
    public static final String EMBEDDED_CHISEL = "EmbeddedChisel";
    public static final String CHISEL_LEVEL = "ChiselLevel";
    public static final String CHISEL_PURITY = "ChiselPurity";
    public static final String UNALLOCATED_PURITY = "UnallocatedPurity";

    public static boolean hasRune(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains(RUNE_TAG);
    }

    public static CompoundTag getOrCreateRuneData(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains(RUNE_TAG)) {
            tag.put(RUNE_TAG, new CompoundTag());
        }
        return tag.getCompound(RUNE_TAG);
    }

    public static void addRune(ItemStack stack, Rune rune) {
        CompoundTag runeData = getOrCreateRuneData(stack);
        ListTag runesList = runeData.getList(RUNES_LIST, 10);
        
        CompoundTag runeTag = new CompoundTag();
        runeTag.putString(RUNE_TYPE, rune.getType());
        runeTag.putString(RUNE_NAME, rune.getName());
        runeTag.putInt(ALLOCATED_POTENTIAL, 0);
        
        runesList.add(runeTag);
        runeData.put(RUNES_LIST, runesList);
    }

    public static List<CompoundTag> getRunes(ItemStack stack) {
        List<CompoundTag> runes = new ArrayList<>();
        if (!hasRune(stack)) return runes;
        
        CompoundTag runeData = stack.getTag().getCompound(RUNE_TAG);
        ListTag runesList = runeData.getList(RUNES_LIST, 10);
        
        for (int i = 0; i < runesList.size(); i++) {
            runes.add(runesList.getCompound(i));
        }
        return runes;
    }

    public static CompoundTag getRuneByType(ItemStack stack, String type) {
        List<CompoundTag> runes = getRunes(stack);
        for (CompoundTag rune : runes) {
            if (rune.getString(RUNE_TYPE).equals(type)) {
                return rune;
            }
        }
        return null;
    }

    public static void setAllocatedPotential(ItemStack stack, String runeType, int potential) {
        CompoundTag rune = getRuneByType(stack, runeType);
        if (rune != null) {
            rune.putInt(ALLOCATED_POTENTIAL, potential);
        }
    }

    public static int getAllocatedPotential(ItemStack stack, String runeType) {
        CompoundTag rune = getRuneByType(stack, runeType);
        return rune != null ? rune.getInt(ALLOCATED_POTENTIAL) : 0;
    }

    public static int getTotalAllocatedPotential(ItemStack stack) {
        int total = 0;
        List<CompoundTag> runes = getRunes(stack);
        for (CompoundTag rune : runes) {
            total += rune.getInt(ALLOCATED_POTENTIAL);
        }
        return total;
    }

    public static boolean hasEmbeddedChisel(ItemStack stack) {
        if (stack.getItem() instanceof ChiselStoneItem) {
            return hasRune(stack);
        }
        if (!hasRune(stack)) return false;
        return stack.getTag().getCompound(RUNE_TAG).contains(EMBEDDED_CHISEL);
    }

    public static void embedChiselStone(ItemStack stack, int level, int purity) {
        CompoundTag runeData = getOrCreateRuneData(stack);
        CompoundTag chiselTag = new CompoundTag();
        chiselTag.putInt(CHISEL_LEVEL, level);
        chiselTag.putInt(CHISEL_PURITY, purity);
        chiselTag.putInt(UNALLOCATED_PURITY, purity);
        runeData.put(EMBEDDED_CHISEL, chiselTag);
    }

    public static int getChiselLevel(ItemStack stack) {
        if (stack.getItem() instanceof ChiselStoneItem chisel) {
            return chisel.getChiselLevel();
        }
        if (!hasRune(stack)) return 0;
        return stack.getTag().getCompound(RUNE_TAG).getCompound(EMBEDDED_CHISEL).getInt(CHISEL_LEVEL);
    }

    public static int getChiselPurity(ItemStack stack) {
        if (stack.getItem() instanceof ChiselStoneItem) {
            return ChiselStoneItem.getPurity(stack);
        }
        if (!hasRune(stack)) return 0;
        return stack.getTag().getCompound(RUNE_TAG).getCompound(EMBEDDED_CHISEL).getInt(CHISEL_PURITY);
    }

    public static int getMaxPotential(ItemStack stack) {
        int level = getChiselLevel(stack);
        if (level <= 0) return 0;
        return level + 4;
    }

    public static int getUnallocatedPurity(ItemStack stack) {
        if (stack.getItem() instanceof ChiselStoneItem) {
            return ChiselStoneItem.getPurity(stack);
        }
        if (!hasRune(stack)) return 0;
        CompoundTag runeData = stack.getTag().getCompound(RUNE_TAG);
        if (!runeData.contains(EMBEDDED_CHISEL)) return 0;
        return runeData.getCompound(EMBEDDED_CHISEL).getInt(UNALLOCATED_PURITY);
    }

    public static int getRemainingPotential(ItemStack stack) {
        return getMaxPotential(stack) - getTotalAllocatedPotential(stack);
    }

    public static void setUnallocatedPurity(ItemStack stack, int purity) {
        if (stack.getItem() instanceof ChiselStoneItem) {
            ChiselStoneItem.setPurity(stack, purity);
            return;
        }
        if (!hasRune(stack)) return;
        CompoundTag runeData = stack.getTag().getCompound(RUNE_TAG);
        CompoundTag chiselData = runeData.getCompound(EMBEDDED_CHISEL);
        chiselData.putInt(UNALLOCATED_PURITY, purity);
        runeData.put(EMBEDDED_CHISEL, chiselData);
    }

    public static boolean allocatePotential(ItemStack stack, String runeType, int amount) {
        int maxPotential = getMaxPotential(stack);
        int current = getAllocatedPotential(stack, runeType);
        int remaining = maxPotential - current;
        
        if (remaining < amount) return false;
        
        int unallocated = getUnallocatedPurity(stack);
        if (unallocated < amount) return false;
        
        setAllocatedPotential(stack, runeType, current + amount);
        setUnallocatedPurity(stack, unallocated - amount);
        return true;
    }
    
    public static void copyFrom(ItemStack target, ItemStack source) {
        if (source.hasTag() && source.getTag().contains(RUNE_TAG)) {
            target.getOrCreateTag().put(RUNE_TAG, source.getTag().getCompound(RUNE_TAG).copy());
        }
        
        if (source.getItem() instanceof ChiselStoneItem chisel) {
            int level = chisel.getChiselLevel();
            int purity = ChiselStoneItem.getPurity(source);
            embedChiselStone(target, level, purity);
        }
    }
}
