package org.fuzhou.fragmentsofsound.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class PierceRuneItem extends Item {

    public PierceRuneItem(Properties properties) {
        super(properties.stacksTo(1).rarity(Rarity.UNCOMMON));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§8§m                          §r"));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§5✧ §b§l穿刺契纹 §5✧"));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§e✦ 效果说明"));
        tooltip.add(Component.literal("§7  史诗战斗重击时"));
        tooltip.add(Component.literal("§7  给敌人叠加一层破防层数"));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§6⚡ 可刻入武器或锲石"));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§8§m                          §r"));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
