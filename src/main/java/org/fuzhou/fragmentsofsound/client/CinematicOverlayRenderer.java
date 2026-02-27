package org.fuzhou.fragmentsofsound.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.fuzhou.fragmentsofsound.Config;
import org.fuzhou.fragmentsofsound.Fragmentsofsound;
import org.fuzhou.fragmentsofsound.cinematic.CinematicRenderer;

@Mod.EventBusSubscriber(modid = Fragmentsofsound.MODID, value = Dist.CLIENT)
public class CinematicOverlayRenderer {
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRenderGuiOverlayPre(RenderGuiOverlayEvent.Pre event) {
        if (!Config.CG_HIDE_GUI.get()) {
            return;
        }
        
        if (CinematicRenderer.isCinematicActive()) {
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRenderHand(RenderHandEvent event) {
        if (CinematicRenderer.isCinematicActive()) {
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderGuiOverlayPost(RenderGuiOverlayEvent.Post event) {
        if (!CinematicRenderer.isCinematicActive()) {
            return;
        }
        
        float alpha = CinematicRenderer.getFadeAlpha();
        if (alpha <= 0.001f) {
            return;
        }
        
        Minecraft mc = Minecraft.getInstance();
        GuiGraphics guiGraphics = event.getGuiGraphics();
        
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();
        
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        
        int alphaInt = ((int) (alpha * 255) & 0xFF) << 24;
        int color = alphaInt;
        
        guiGraphics.fill(0, 0, screenWidth, screenHeight, color);
        
        RenderSystem.enableDepthTest();
    }
}
