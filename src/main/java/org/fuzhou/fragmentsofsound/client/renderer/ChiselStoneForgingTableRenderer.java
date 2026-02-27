package org.fuzhou.fragmentsofsound.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.fuzhou.fragmentsofsound.block.entity.ChiselStoneForgingTableBlockEntity;

public class ChiselStoneForgingTableRenderer implements BlockEntityRenderer<ChiselStoneForgingTableBlockEntity> {

    private final ItemRenderer itemRenderer;

    public ChiselStoneForgingTableRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

    @Override
    public void render(ChiselStoneForgingTableBlockEntity blockEntity, float partialTick, PoseStack poseStack, 
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        
        ItemStack itemStack = blockEntity.getItemHandler().getStackInSlot(0);
        if (itemStack.isEmpty()) return;

        poseStack.pushPose();
        
        poseStack.translate(0.5, 0.6875, 0.5);
        
        poseStack.mulPose(Axis.XP.rotationDegrees(90));
        
        poseStack.mulPose(Axis.ZP.rotationDegrees(blockEntity.getLevel().getGameTime() % 360));
        
        poseStack.scale(0.5f, 0.5f, 0.5f);
        
        itemRenderer.renderStatic(itemStack, ItemDisplayContext.FIXED, packedLight, 
            OverlayTexture.NO_OVERLAY, poseStack, bufferSource, blockEntity.getLevel(), 0);
        
        poseStack.popPose();
    }
}
