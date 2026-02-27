package org.fuzhou.fragmentsofsound.cinematic;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.fuzhou.fragmentsofsound.Config;
import org.fuzhou.fragmentsofsound.Fragmentsofsound;

import java.util.Random;

@Mod.EventBusSubscriber(modid = Fragmentsofsound.MODID, value = Dist.CLIENT)
public class CinematicRenderer {
    
    private static CinematicData currentCinematic = null;
    private static int cinematicTick = 0;
    private static boolean isActive = false;
    
    private static int FADE_OUT_BLACK_DURATION;
    private static int CINEMATIC_DURATION;
    private static int FADE_OUT_END_DURATION;
    
    private static float fadeAlpha = 1.0f;
    
    private static Vec3 originalPosition = null;
    private static float originalYaw = 0;
    private static float originalPitch = 0;
    private static boolean wasCreative = false;
    
    private static boolean outpostPlaced = false;
    private static BlockPos placedOutpostPos = null;
    
    public static boolean isCinematicActive() {
        return isActive;
    }
    
    public static float getFadeAlpha() {
        return fadeAlpha;
    }
    
    public static void startCinematic(CinematicData data) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        
        if (!Config.CG_ENABLED.get()) {
            return;
        }
        
        LocalPlayer player = mc.player;
        
        originalPosition = new Vec3(player.getX(), player.getY(), player.getZ());
        originalYaw = player.getYRot();
        originalPitch = player.getXRot();
        
        wasCreative = player.isCreative() || player.isSpectator();
        
        currentCinematic = data;
        cinematicTick = 0;
        fadeAlpha = 1.0f;
        isActive = true;
        outpostPlaced = false;
        placedOutpostPos = null;
        
        FADE_OUT_BLACK_DURATION = Config.CG_FADE_OUT_DURATION.get();
        CINEMATIC_DURATION = Config.CG_CINEMATIC_DURATION.get();
        FADE_OUT_END_DURATION = Config.CG_FADE_IN_DURATION.get();
        
        placeOutpost();
        
        if (Config.CG_DISABLE_INPUT.get()) {
            mc.options.keyUp.setDown(false);
            mc.options.keyDown.setDown(false);
            mc.options.keyLeft.setDown(false);
            mc.options.keyRight.setDown(false);
            mc.options.keyJump.setDown(false);
            mc.options.keyShift.setDown(false);
            mc.options.keySprint.setDown(false);
            mc.options.keyAttack.setDown(false);
            mc.options.keyUse.setDown(false);
        }
        
        if (!wasCreative) {
            player.connection.sendUnsignedCommand("gamemode spectator");
        }
    }
    
    public static void stopCinematic() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        
        LocalPlayer player = mc.player;
        
        if (currentCinematic != null && !currentCinematic.shouldApplyChanges() && outpostPlaced && placedOutpostPos != null) {
            player.connection.sendUnsignedCommand(
                String.format("setblock %d %d %d air",
                    placedOutpostPos.getX(), placedOutpostPos.getY(), placedOutpostPos.getZ())
            );
        }
        
        if (originalPosition != null) {
            player.setPos(originalPosition.x, originalPosition.y, originalPosition.z);
            player.moveTo(originalPosition.x, originalPosition.y, originalPosition.z, originalYaw, originalPitch);
            player.setYRot(originalYaw);
            player.setXRot(originalPitch);
            player.yHeadRot = originalYaw;
            player.yRotO = originalYaw;
            player.xRotO = originalPitch;
            player.lerpTargetX = originalPosition.x;
            player.lerpTargetY = originalPosition.y;
            player.lerpTargetZ = originalPosition.z;
            player.lerpTargetYaw = originalYaw;
            player.lerpTargetPitch = originalPitch;
        }
        
        if (!wasCreative) {
            player.connection.sendUnsignedCommand("gamemode survival");
        }
        
        isActive = false;
        currentCinematic = null;
        cinematicTick = 0;
        fadeAlpha = 0.0f;
        originalPosition = null;
        wasCreative = false;
        outpostPlaced = false;
        placedOutpostPos = null;
    }
    
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !isActive || currentCinematic == null) {
            return;
        }
        
        cinematicTick++;
        
        updateFade();
        
        if (cinematicTick > FADE_OUT_BLACK_DURATION && cinematicTick <= FADE_OUT_BLACK_DURATION + CINEMATIC_DURATION) {
            updatePlayerCamera();
            spawnParticles();
        }
        
        int totalDuration = FADE_OUT_BLACK_DURATION + CINEMATIC_DURATION + FADE_OUT_END_DURATION;
        if (cinematicTick >= totalDuration) {
            stopCinematic();
        }
    }
    
    private static void updateFade() {
        if (cinematicTick <= FADE_OUT_BLACK_DURATION) {
            fadeAlpha = 1.0f - (float) cinematicTick / FADE_OUT_BLACK_DURATION;
        } else if (cinematicTick <= FADE_OUT_BLACK_DURATION + CINEMATIC_DURATION) {
            fadeAlpha = 0.0f;
        } else {
            int fadeOutTick = cinematicTick - FADE_OUT_BLACK_DURATION - CINEMATIC_DURATION;
            fadeAlpha = (float) fadeOutTick / FADE_OUT_END_DURATION;
        }
    }
    
    private static void updatePlayerCamera() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || currentCinematic == null || currentCinematic.getTargetPosition() == null) return;
        
        LocalPlayer player = mc.player;
        BlockPos targetPos = currentCinematic.getTargetPosition();
        
        int cinematicProgress = cinematicTick - FADE_OUT_BLACK_DURATION;
        float progress = (float) cinematicProgress / CINEMATIC_DURATION;
        
        float startHeight = 1.0f;
        float endHeight = 6.0f;
        float currentHeight = startHeight + (endHeight - startHeight) * progress;
        
        float currentRotation = 360.0f * progress;
        
        double eyeX = targetPos.getX() + 0.5;
        double eyeY = targetPos.getY() + currentHeight;
        double eyeZ = targetPos.getZ() + 0.5;
        
        player.moveTo(eyeX, eyeY, eyeZ, currentRotation, 90.0f);
        player.setPos(eyeX, eyeY, eyeZ);
        
        player.setYRot(currentRotation);
        player.setXRot(90.0f);
        player.yHeadRot = currentRotation;
        player.yRotO = currentRotation;
        player.xRotO = 90.0f;
    }
    
    private static void spawnParticles() {
        Minecraft mc = Minecraft.getInstance();
        Level level = mc.level;
        if (level == null || currentCinematic == null || currentCinematic.getTargetPosition() == null) return;
        
        BlockPos targetPos = currentCinematic.getTargetPosition();
        Random random = new Random();
        
        int cinematicProgress = cinematicTick - FADE_OUT_BLACK_DURATION;
        if (cinematicProgress % 3 == 0) {
            for (int i = 0; i < 3; i++) {
                double x = targetPos.getX() + 0.5 + (random.nextDouble() - 0.5) * 2;
                double y = targetPos.getY() + random.nextDouble();
                double z = targetPos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 2;
                
                level.addParticle(ParticleTypes.SMOKE, x, y, z, 0, 0.02, 0);
            }
        }
    }
    
    private static void placeOutpost() {
        if (currentCinematic == null || currentCinematic.getTargetPosition() == null) return;
        
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        
        BlockPos pos = currentCinematic.getTargetPosition();
        int outpostLevel = currentCinematic.getOutpostLevel();
        
        mc.player.connection.sendUnsignedCommand(
            String.format("setblock %d %d %d fragmentsofsound:monster_outpost_%d",
                pos.getX(), pos.getY(), pos.getZ(), outpostLevel)
        );
        
        outpostPlaced = true;
        placedOutpostPos = pos;
    }
    
    public static Vec3 getCameraPosition() {
        if (!isActive || currentCinematic == null || currentCinematic.getTargetPosition() == null) {
            return null;
        }
        return originalPosition;
    }
    
    public static BlockPos getCameraTarget() {
        if (!isActive || currentCinematic == null) {
            return null;
        }
        return currentCinematic.getTargetPosition();
    }
    
    public static float getCinematicProgress() {
        if (currentCinematic == null) return 0;
        return (float) cinematicTick / (FADE_OUT_BLACK_DURATION + CINEMATIC_DURATION + FADE_OUT_END_DURATION);
    }
    
    public static int getCinematicTick() {
        return cinematicTick;
    }
}
