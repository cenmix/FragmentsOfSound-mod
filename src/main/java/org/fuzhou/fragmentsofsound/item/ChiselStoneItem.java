package org.fuzhou.fragmentsofsound.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class ChiselStoneItem extends Item {
    private final int level;
    private static final String PURITY_TAG = "Purity";

    public ChiselStoneItem(Properties properties, int level) {
        super(properties);
        this.level = level;
    }
    
    public int getChiselLevel() {
        return this.level;
    }
    
    public static int getPurity(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(PURITY_TAG)) {
            return tag.getInt(PURITY_TAG);
        }
        return 20;
    }
    
    public static void setPurity(ItemStack stack, int purity) {
        stack.getOrCreateTag().putInt(PURITY_TAG, Math.max(0, Math.min(30, purity)));
    }
    
    public static int generateRandomPurity(RandomSource random) {
        float chance = random.nextFloat();
        
        if (chance < 0.10f) {
            return 20 + random.nextInt(11);
        } else if (chance < 0.20f) {
            return random.nextInt(11);
        } else {
            return 10 + random.nextInt(11);
        }
    }
    
    public static ItemStack createStackWithPurity(int level, int purity) {
        ItemStack stack = new ItemStack(getItemForLevel(level));
        setPurity(stack, purity);
        return stack;
    }
    
    private static Item getItemForLevel(int level) {
        return switch (level) {
            case 1 -> org.fuzhou.fragmentsofsound.Fragmentsofsound.CHISEL_STONE_1.get();
            case 2 -> org.fuzhou.fragmentsofsound.Fragmentsofsound.CHISEL_STONE_2.get();
            case 3 -> org.fuzhou.fragmentsofsound.Fragmentsofsound.CHISEL_STONE_3.get();
            case 4 -> org.fuzhou.fragmentsofsound.Fragmentsofsound.CHISEL_STONE_4.get();
            case 5 -> org.fuzhou.fragmentsofsound.Fragmentsofsound.CHISEL_STONE_5.get();
            case 6 -> org.fuzhou.fragmentsofsound.Fragmentsofsound.CHISEL_STONE_6.get();
            case 7 -> org.fuzhou.fragmentsofsound.Fragmentsofsound.CHISEL_STONE_7.get();
            case 8 -> org.fuzhou.fragmentsofsound.Fragmentsofsound.CHISEL_STONE_8.get();
            case 9 -> org.fuzhou.fragmentsofsound.Fragmentsofsound.CHISEL_STONE_9.get();
            case 10 -> org.fuzhou.fragmentsofsound.Fragmentsofsound.CHISEL_STONE_10.get();
            default -> org.fuzhou.fragmentsofsound.Fragmentsofsound.CHISEL_STONE_1.get();
        };
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        
        int purity = getPurity(stack);
        String purityColor;
        
        if (purity >= 25) {
            purityColor = "§d";
        } else if (purity >= 20) {
            purityColor = "§b";
        } else if (purity >= 15) {
            purityColor = "§a";
        } else if (purity >= 10) {
            purityColor = "§e";
        } else if (purity >= 5) {
            purityColor = "§6";
        } else {
            purityColor = "§c";
        }
        
        tooltip.add(Component.literal("§7纯度: " + purityColor + purity));
    }
    
    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        setPurity(stack, 20);
        return stack;
    }
}
