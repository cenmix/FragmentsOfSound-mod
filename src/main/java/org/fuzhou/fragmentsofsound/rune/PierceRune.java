package org.fuzhou.fragmentsofsound.rune;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class PierceRune extends Rune {

    public static final String TYPE = "pierce";
    private static final int MAX_POTENTIAL = 10;

    public PierceRune() {
        super(TYPE, "穿刺", "重击时给敌人叠加破防层数");
    }

    @Override
    public void appendTooltip(ItemStack stack, List<Component> tooltip, int allocatedPotential) {
        tooltip.add(Component.literal("§b⚔ §e穿刺契纹"));
        tooltip.add(Component.literal("§7  " + description));
        tooltip.add(Component.literal("§7  已分配潜力: §b" + allocatedPotential + "§7/§b" + MAX_POTENTIAL));
        tooltip.add(Component.literal("§7  最大破防层数: §a" + allocatedPotential));
    }

    @Override
    public int getMaxPotential() {
        return MAX_POTENTIAL;
    }
}
