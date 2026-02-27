package org.fuzhou.fragmentsofsound.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class PhysicalRuneItem extends Item {

    private static final String CRACKED_TAG = "Cracked";
    private static final String RUNE_TYPE_TAG = "RuneType";

    public PhysicalRuneItem(Properties properties) {
        super(properties.stacksTo(1).rarity(Rarity.UNCOMMON));
    }

    public static boolean isCracked(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.getBoolean(CRACKED_TAG);
    }

    public static void setCracked(ItemStack stack, boolean cracked) {
        stack.getOrCreateTag().putBoolean(CRACKED_TAG, cracked);
    }

    public static String getRuneType(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(RUNE_TYPE_TAG)) {
            return tag.getString(RUNE_TYPE_TAG);
        }
        return "physical";
    }

    public static void setRuneType(ItemStack stack, String type) {
        stack.getOrCreateTag().putString(RUNE_TYPE_TAG, type);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        boolean cracked = isCracked(stack);
        
        if (!cracked) {
            tooltip.add(Component.literal(""));
            tooltip.add(Component.literal("§8§m                          §r"));
            tooltip.add(Component.literal(""));
            tooltip.add(Component.literal("§5✧ §d§l契纹状态 §5✧"));
            tooltip.add(Component.literal("§7  §o该契纹尚未被破解..."));
            tooltip.add(Component.literal("§7  §o蕴含着神秘的力量，或许可以作用在锲石上？"));
            tooltip.add(Component.literal(""));
            tooltip.add(Component.literal("§8§m                          §r"));
            tooltip.add(Component.literal(""));
            tooltip.add(Component.literal("§6⚡ §e§l提示 §6⚡"));
            tooltip.add(Component.literal("§7  在锻造台中尝试破解"));
        } else {
            tooltip.add(Component.literal(""));
            tooltip.add(Component.literal("§8§m                          §r"));
            tooltip.add(Component.literal(""));
            tooltip.add(Component.literal("§5✧ §a§l契纹状态 §5✧"));
            tooltip.add(Component.literal("§7  §a✦ 已破解"));
            tooltip.add(Component.literal(""));
            tooltip.add(Component.literal("§8§m                          §r"));
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        setCracked(stack, false);
        setRuneType(stack, "physical");
        return stack;
    }
}
