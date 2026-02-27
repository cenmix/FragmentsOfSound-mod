package org.fuzhou.fragmentsofsound.rune;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public abstract class Rune {

    protected final String type;
    protected final String name;
    protected final String description;

    public Rune(String type, String name, String description) {
        this.type = type;
        this.name = name;
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public abstract void appendTooltip(ItemStack stack, List<Component> tooltip, int allocatedPotential);

    public abstract int getMaxPotential();

    public static Rune getRuneByType(String type) {
        return switch (type) {
            case "armor_break" -> ModRunes.ARMOR_BREAK;
            case "pierce" -> ModRunes.PIERCE;
            default -> null;
        };
    }
}
