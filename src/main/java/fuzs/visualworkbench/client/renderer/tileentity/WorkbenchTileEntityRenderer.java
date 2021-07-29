package fuzs.visualworkbench.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import fuzs.visualworkbench.VisualWorkbench;
import fuzs.visualworkbench.element.VisualWorkbenchElement;
import fuzs.visualworkbench.tileentity.WorkbenchTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class WorkbenchTileEntityRenderer extends TileEntityRenderer<WorkbenchTileEntity> {

    private final ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

    public WorkbenchTileEntityRenderer(TileEntityRendererDispatcher tileEntityRendererDispatcher) {

        super(tileEntityRendererDispatcher);
    }

    @Override
    public void render(WorkbenchTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {

        // there is a block above this workbench
        if (tileEntityIn.combinedLight == 0) {

            return;
        }

        VisualWorkbenchElement element = (VisualWorkbenchElement) VisualWorkbench.VISUAL_WORKBENCH;
        for (int i = 0; i < tileEntityIn.getContainerSize(); ++i) {

            ItemStack itemStack = tileEntityIn.getItem(i);
            if (itemStack != ItemStack.EMPTY) {

                matrixStackIn.pushPose();
                if (element.extension.flatRendering) {

                    this.renderLaying(tileEntityIn, partialTicks, matrixStackIn, itemStack, i);
                } else {

                    this.renderFloating(tileEntityIn, partialTicks, matrixStackIn, itemStack, i);
                }

                Minecraft.getInstance().getItemRenderer().renderStatic(itemStack, ItemCameraTransforms.TransformType.FIXED, tileEntityIn.combinedLight, combinedOverlayIn, matrixStackIn, bufferIn);
                matrixStackIn.popPose();
            }
        }
    }

    private void renderFloating(WorkbenchTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, ItemStack itemStack, int index) {

        // -0.0125 to 0.0125
        float shift = (float) Math.abs(((tileEntityIn.ticks + partialTicks) * 50.0 + (index * 1000L)) % 5000L - 2500L) / 200000.0F;
        IBakedModel ibakedmodel = this.itemRenderer.getModel(itemStack, null, null);
        boolean isBlockItem = ibakedmodel.isGui3d();
        float scale = isBlockItem ? 0.24F : 0.18F;

        matrixStackIn.translate(0.5, shift, 0.5);
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTicks, tileEntityIn.currentAngle, tileEntityIn.nextAngle)));
        matrixStackIn.translate((double) (index % 3) * 3.0 / 16.0 + 0.3125 - 0.5, 1.09375, (double) (index / 3) * 3.0 / 16.0 + 0.3125 - 0.5);
        matrixStackIn.scale(scale, scale, scale);
    }

    private void renderLaying(WorkbenchTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, ItemStack itemStack, int index) {

        IBakedModel ibakedmodel = this.itemRenderer.getModel(itemStack, null, null);
        boolean isBlockItem = ibakedmodel.isGui3d();
        float scale = isBlockItem ? 0.25F : 0.175F;

        matrixStackIn.translate(0.5, 0.0, 0.5);
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTicks, tileEntityIn.currentAngle, tileEntityIn.nextAngle)));
        matrixStackIn.translate((double) (index % 3) * 3.0 / 16.0 + 0.3125 - 0.5, isBlockItem ? 1.0625 : 1.005, (double) (index / 3) * 3.0 / 16.0 + 0.3125 - 0.5);
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(90.0F));
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
        matrixStackIn.scale(scale, scale, scale);
    }

}
