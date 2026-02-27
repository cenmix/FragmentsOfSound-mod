package org.fuzhou.fragmentsofsound.event;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.fuzhou.fragmentsofsound.Fragmentsofsound;
import org.fuzhou.fragmentsofsound.effect.ModEffects;
import org.fuzhou.fragmentsofsound.item.ChiselStoneItem;

@Mod.EventBusSubscriber(modid = Fragmentsofsound.MODID)
public class CorrosionEventHandler {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.player.level().isClientSide) return;
        
        Player player = event.player;
        ItemStack mainHandItem = player.getMainHandItem();
        
        if (mainHandItem.getItem() instanceof ChiselStoneItem chiselStone) {
            int level = chiselStone.getChiselLevel();
            
            if (level >= 9) {
                MobEffectInstance currentEffect = player.getEffect(ModEffects.CORROSION.get());
                if (currentEffect != null) {
                    player.removeEffect(ModEffects.CORROSION.get());
                }
                return;
            }
            
            int corrosionLevel = Math.min(level, 8);
            int duration = (90 - (corrosionLevel * 10)) * 20;
            if (duration < 20) duration = 20;
            
            MobEffectInstance currentEffect = player.getEffect(ModEffects.CORROSION.get());
            if (currentEffect == null) {
                player.addEffect(new MobEffectInstance(ModEffects.CORROSION.get(), duration, corrosionLevel - 1, false, true));
            } else if (currentEffect.getAmplifier() != corrosionLevel - 1) {
                player.removeEffect(ModEffects.CORROSION.get());
                player.addEffect(new MobEffectInstance(ModEffects.CORROSION.get(), duration, corrosionLevel - 1, false, true));
            }
        } else {
            MobEffectInstance currentEffect = player.getEffect(ModEffects.CORROSION.get());
            if (currentEffect != null) {
                player.removeEffect(ModEffects.CORROSION.get());
            }
        }
    }
}
