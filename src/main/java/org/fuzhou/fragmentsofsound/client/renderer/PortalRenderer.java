package org.fuzhou.fragmentsofsound.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.fuzhou.fragmentsofsound.Fragmentsofsound;
import org.fuzhou.fragmentsofsound.entity.PortalEntity;

public class PortalRenderer extends EntityRenderer<PortalEntity> {
    
    private static final ResourceLocation TEXTURE = ResourceLocation.tryParse(Fragmentsofsound.MODID + ":textures/entity/portal.png");
    private static final float WIDTH = 2.0f;
    private static final float HEIGHT = 4.0f;
    
    public PortalRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
    
    @Override
    public void render(PortalEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        
        Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        double dx = cameraPos.x - entity.getX();
        double dz = cameraPos.z - entity.getZ();
        float yaw = (float) (Math.atan2(dz, dx) * 180.0 / Math.PI) - 90.0f;
        
        poseStack.mulPose(Axis.YP.rotationDegrees(-yaw));
        
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        PoseStack.Pose pose = poseStack.last();
        
        float halfWidth = WIDTH / 2.0f;
        float halfHeight = HEIGHT / 2.0f;
        
        vertexConsumer.vertex(pose.pose(), -halfWidth, -halfHeight, 0)
            .color(255, 255, 255, 255)
            .uv(0, 1)
            .overlayCoords(OverlayTexture.NO_OVERLAY)
            .uv2(packedLight)
            .normal(pose.normal(), 0, 0, 1)
            .endVertex();
        
        vertexConsumer.vertex(pose.pose(), -halfWidth, halfHeight, 0)
            .color(255, 255, 255, 255)
            .uv(0, 0)
            .overlayCoords(OverlayTexture.NO_OVERLAY)
            .uv2(packedLight)
            .normal(pose.normal(), 0, 0, 1)
            .endVertex();
        
        vertexConsumer.vertex(pose.pose(), halfWidth, halfHeight, 0)
            .color(255, 255, 255, 255)
            .uv(1, 0)
            .overlayCoords(OverlayTexture.NO_OVERLAY)
            .uv2(packedLight)
            .normal(pose.normal(), 0, 0, 1)
            .endVertex();
        
        vertexConsumer.vertex(pose.pose(), halfWidth, -halfHeight, 0)
            .color(255, 255, 255, 255)
            .uv(1, 1)
            .overlayCoords(OverlayTexture.NO_OVERLAY)
            .uv2(packedLight)
            .normal(pose.normal(), 0, 0, 1)
            .endVertex();
        
        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }
    
    @Override
    public ResourceLocation getTextureLocation(PortalEntity entity) {
        return TEXTURE;
    }
}
