package org.fuzhou.fragmentsofsound.event;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.fuzhou.fragmentsofsound.Fragmentsofsound;
import org.fuzhou.fragmentsofsound.rune.Rune;
import org.fuzhou.fragmentsofsound.rune.RuneData;

import java.util.List;

@Mod.EventBusSubscriber(modid = Fragmentsofsound.MODID)
public class RuneTooltipHandler {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        
        if (!RuneData.hasRune(stack)) return;
        
        List<Component> tooltip = event.getToolTip();
        
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§8§m                          §r"));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§5✧ §d§l契纹信息 §5✧"));
        
        if (RuneData.hasEmbeddedChisel(stack)) {
            int chiselLevel = RuneData.getChiselLevel(stack);
            int maxPotential = RuneData.getMaxPotential(stack);
            int allocated = RuneData.getTotalAllocatedPotential(stack);
            int unallocated = RuneData.getUnallocatedPurity(stack);
            
            tooltip.add(Component.literal("§b锲石等级: §f" + chiselLevel));
            tooltip.add(Component.literal("§b潜力上限: §f" + maxPotential));
            tooltip.add(Component.literal("§a已分配: §f" + allocated));
            tooltip.add(Component.literal("§e未分配纯度: §f" + unallocated));
        } else {
            tooltip.add(Component.literal("§c✦ 未嵌入锲石"));
        }
        
        tooltip.add(Component.literal(""));
        
        List<net.minecraft.nbt.CompoundTag> runeTags = RuneData.getRunes(stack);
        for (net.minecraft.nbt.CompoundTag tag : runeTags) {
            Rune rune = Rune.getRuneByType(tag.getString("RuneType"));
            if (rune != null) {
                int allocated = RuneData.getAllocatedPotential(stack, rune.getType());
                tooltip.add(Component.literal("§d✦ " + rune.getName() + " §7(潜力: " + allocated + ")"));
                tooltip.add(Component.literal("§7   " + rune.getDescription()));
            }
        }
        
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("§8§m                          §r"));
    }
}
