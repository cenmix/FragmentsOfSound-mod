package org.fuzhou.fragmentsofsound.compat;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.fuzhou.fragmentsofsound.Fragmentsofsound;
import org.fuzhou.fragmentsofsound.event.RuneEventHandler;
import org.fuzhou.fragmentsofsound.rune.PierceRune;
import org.fuzhou.fragmentsofsound.rune.RuneData;

@Mod.EventBusSubscriber(modid = Fragmentsofsound.MODID)
public class VanillaHeavyAttackHandler {

    private static final String ATTACK_COUNT_TAG = "PierceRuneAttackCount";
    private static final int ATTACKS_FOR_HEAVY = 5;

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        
        LivingEntity target = event.getEntity();
        ItemStack mainHandItem = player.getMainHandItem();
        
        if (!RuneData.hasRune(mainHandItem)) return;
        
        int allocatedPotential = RuneData.getAllocatedPotential(mainHandItem, PierceRune.TYPE);
        if (allocatedPotential <= 0) return;
        
        int attackCount = getAttackCount(player);
        attackCount++;
        
        if (attackCount >= ATTACKS_FOR_HEAVY) {
            RuneEventHandler.addArmorBreakStack(target, allocatedPotential);
            resetAttackCount(player);
        } else {
            setAttackCount(player, attackCount);
        }
    }

    private static int getAttackCount(Player player) {
        return player.getPersistentData().getInt(ATTACK_COUNT_TAG);
    }

    private static void setAttackCount(Player player, int count) {
        player.getPersistentData().putInt(ATTACK_COUNT_TAG, count);
    }

    private static void resetAttackCount(Player player) {
        player.getPersistentData().putInt(ATTACK_COUNT_TAG, 0);
    }
}
