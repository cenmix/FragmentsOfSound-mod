package org.fuzhou.fragmentsofsound.event;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.fuzhou.fragmentsofsound.Fragmentsofsound;
import org.fuzhou.fragmentsofsound.rune.ArmorBreakRune;
import org.fuzhou.fragmentsofsound.rune.RuneData;

@Mod.EventBusSubscriber(modid = Fragmentsofsound.MODID)
public class RuneEventHandler {

    private static final String ARMOR_BREAK_STACKS_TAG = "ArmorBreakStacks";

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        Entity source = event.getSource().getEntity();
        if (!(source instanceof Player player)) return;
        
        LivingEntity target = event.getEntity();
        
        ItemStack mainHandItem = player.getMainHandItem();
        
        if (RuneData.hasRune(mainHandItem)) {
            int allocatedPotential = RuneData.getAllocatedPotential(mainHandItem, ArmorBreakRune.TYPE);
            if (allocatedPotential > 0) {
                int armorBreakStacks = getArmorBreakStacks(target);
                float extraDamage = ArmorBreakRune.calculateExtraDamage(allocatedPotential, event.getAmount(), armorBreakStacks);
                if (extraDamage > 0) {
                    event.setAmount(event.getAmount() + extraDamage);
                }
            }
        }
    }

    public static int getArmorBreakStacks(LivingEntity entity) {
        return entity.getPersistentData().getInt(ARMOR_BREAK_STACKS_TAG);
    }

    public static void addArmorBreakStack(LivingEntity entity, int maxStacks) {
        int current = getArmorBreakStacks(entity);
        if (current < maxStacks) {
            entity.getPersistentData().putInt(ARMOR_BREAK_STACKS_TAG, current + 1);
        }
    }

    public static void resetArmorBreakStacks(LivingEntity entity) {
        entity.getPersistentData().putInt(ARMOR_BREAK_STACKS_TAG, 0);
    }
}
