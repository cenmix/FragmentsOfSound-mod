package org.fuzhou.fragmentsofsound.rune;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ArmorBreakRune extends Rune {

    public static final String TYPE = "armor_break";
    private static final int MAX_POTENTIAL = 10;

    public ArmorBreakRune() {
        super(TYPE, "破防", "造成额外伤害，伤害公式：潜力×攻击力×破防层数×5%");
    }

    @Override
    public void appendTooltip(ItemStack stack, List<Component> tooltip, int allocatedPotential) {
        tooltip.add(Component.literal("§c⚔ §e破防契纹"));
        tooltip.add(Component.literal("§7  " + description));
        tooltip.add(Component.literal("§7  已分配潜力: §b" + allocatedPotential + "§7/§b" + MAX_POTENTIAL));
        tooltip.add(Component.literal("§7  当前伤害加成: §a" + (allocatedPotential * 5) + "%"));
    }

    @Override
    public int getMaxPotential() {
        return MAX_POTENTIAL;
    }

    public static float calculateExtraDamage(int allocatedPotential, float baseDamage, int armorBreakStacks) {
        if (allocatedPotential <= 0 || armorBreakStacks <= 0) return 0;
        return allocatedPotential * baseDamage * armorBreakStacks * 0.05f;
    }
}
