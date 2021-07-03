package fuzs.visualworkbench.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import fuzs.visualworkbench.tileentity.WorkbenchTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class WorkbenchTileEntityRenderer extends TileEntityRenderer<WorkbenchTileEntity> {

    public WorkbenchTileEntityRenderer(TileEntityRendererDispatcher tileEntityRendererDispatcher) {

        super(tileEntityRendererDispatcher);
    }

    @Override
    public void render(WorkbenchTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {

        // there is a block above this workbench
        if (tileEntityIn.combinedLight == 0) {

            return;
        }

        for(int i = 0; i < tileEntityIn.getContainerSize(); ++i) {

            ItemStack itemStack = tileEntityIn.getItem(i);
            if (itemStack != ItemStack.EMPTY) {

                matrixStackIn.pushPose();
                // -0.0125 to 0.0125
                float shift = (float) Math.abs(((tileEntityIn.ticks + partialTicks) * 50.0 + (i * 1000L)) % 5000L - 2500L) / 200000.0F;
                matrixStackIn.translate(0.5, shift, 0.5);
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTicks, tileEntityIn.currentAngle, tileEntityIn.nextAngle)));
                matrixStackIn.translate((double) (i % 3) * 3.0 / 16.0 + 0.3125 - 0.5, 1.09375, (double)(i / 3) * 3.0 / 16.0 + 0.3125 - 0.5);
                float scale = itemStack.getItem() instanceof BlockItem ? 0.24F : 0.18F;
                matrixStackIn.scale(scale, scale, scale);
                Minecraft.getInstance().getItemRenderer().renderStatic(itemStack, ItemCameraTransforms.TransformType.FIXED, tileEntityIn.combinedLight, combinedOverlayIn, matrixStackIn, bufferIn);
                matrixStackIn.popPose();
            }
        }
    }

}
