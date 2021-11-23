package fuzs.visualworkbench.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import fuzs.visualworkbench.VisualWorkbench;
import fuzs.visualworkbench.world.level.block.entity.CraftingTableBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public class WorkbenchTileEntityRenderer implements BlockEntityRenderer<CraftingTableBlockEntity> {
    private final ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

    public WorkbenchTileEntityRenderer(BlockEntityRendererProvider.Context pContext) {
    }

    @Override
    public void render(CraftingTableBlockEntity tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        for (int i = 0; i < tileEntityIn.getContainerSize(); ++i) {
            ItemStack itemStack = tileEntityIn.getItem(i);
            if (itemStack != ItemStack.EMPTY) {
                matrixStackIn.pushPose();
                if (VisualWorkbench.CONFIG.client().flatRendering) {
                    this.renderLaying(tileEntityIn, partialTicks, matrixStackIn, itemStack, i);
                } else {
                    this.renderFloating(tileEntityIn, partialTicks, matrixStackIn, itemStack, i);
                }
                Minecraft.getInstance().getItemRenderer().renderStatic(itemStack, ItemTransforms.TransformType.FIXED, tileEntityIn.combinedLight, combinedOverlayIn, matrixStackIn, bufferIn, (int) tileEntityIn.getBlockPos().asLong() + i);
                matrixStackIn.popPose();
            }
        }
    }

    private void renderFloating(CraftingTableBlockEntity tileEntityIn, float partialTicks, PoseStack matrixStackIn, ItemStack itemStack, int index) {
        // -0.0125 to 0.0125
        float shift = (float) Math.abs(((tileEntityIn.ticks + partialTicks) * 50.0 + (index * 1000L)) % 5000L - 2500L) / 200000.0F;
        BakedModel ibakedmodel = this.itemRenderer.getModel(itemStack, null, null, 0);
        boolean isBlockItem = ibakedmodel.isGui3d();
        float scale = isBlockItem ? 0.24F : 0.18F;
        matrixStackIn.translate(0.5, shift, 0.5);
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(partialTicks, tileEntityIn.currentAngle, tileEntityIn.nextAngle)));
        matrixStackIn.translate((double) (index % 3) * 3.0 / 16.0 + 0.3125 - 0.5, 1.09375, (double) (index / 3) * 3.0 / 16.0 + 0.3125 - 0.5);
        matrixStackIn.scale(scale, scale, scale);
    }

    private void renderLaying(CraftingTableBlockEntity tileEntityIn, float partialTicks, PoseStack matrixStackIn, ItemStack itemStack, int index) {
        BakedModel ibakedmodel = this.itemRenderer.getModel(itemStack, null, null, 0);
        boolean isBlockItem = ibakedmodel.isGui3d();
        float scale = isBlockItem ? 0.25F : 0.175F;
        matrixStackIn.translate(0.5, 0.0, 0.5);
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(partialTicks, tileEntityIn.currentAngle, tileEntityIn.nextAngle)));
        matrixStackIn.translate((double) (index % 3) * 3.0 / 16.0 + 0.3125 - 0.5, isBlockItem ? 1.0625 : 1.005, (double) (index / 3) * 3.0 / 16.0 + 0.3125 - 0.5);
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(90.0F));
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
        matrixStackIn.scale(scale, scale, scale);
    }
}
