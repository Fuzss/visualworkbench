package fuzs.visualworkbench.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import fuzs.visualworkbench.VisualWorkbench;
import fuzs.visualworkbench.config.ClientConfig;
import fuzs.visualworkbench.world.level.block.entity.CraftingTableAnimationController;
import fuzs.visualworkbench.world.level.block.entity.CraftingTableBlockEntity;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class CraftingTableBlockEntityRenderer implements BlockEntityRenderer<CraftingTableBlockEntity> {
    private final ItemRenderer itemRenderer;

    public CraftingTableBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(CraftingTableBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, int packedOverlay) {
        // light is normally always 0 since it checks inside the crafting table block which is solid, but contents are rendered in the block above
        packedLight = blockEntity.getLevel() != null ? LevelRenderer.getLightColor(blockEntity.getLevel(), blockEntity.getBlockPos().above()) : 15728880;
        for (int i = 0; i < blockEntity.getContainerSize(); ++i) {
            ItemStack itemStack = blockEntity.getItem(i);
            if (!itemStack.isEmpty()) {
                this.renderIngredientItem(blockEntity, partialTick, poseStack, multiBufferSource, packedLight, packedOverlay, i, itemStack);
            }
        }
        ItemStack itemStack = blockEntity.getResultItems().get(0);
        if (!itemStack.isEmpty()) {
            this.renderResultItem(itemStack, blockEntity.getLevel(), blockEntity.getAnimationController().ticks + partialTick, poseStack, multiBufferSource, packedLight);
        }
    }

    private void renderIngredientItem(CraftingTableBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, int packedOverlay, int i, ItemStack itemStack) {
        poseStack.pushPose();
        if (VisualWorkbench.CONFIG.get(ClientConfig.class).flatRendering) {
            this.setupFlatRenderer(blockEntity.getAnimationController(), partialTick, poseStack, itemStack, i);
        } else {
            this.setupFloatingRenderer(blockEntity.getAnimationController(), partialTick, poseStack, itemStack, i);
        }
        this.itemRenderer.renderStatic(itemStack, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, multiBufferSource, blockEntity.getLevel(), (int) blockEntity.getBlockPos().asLong() + i);
        poseStack.popPose();
    }

    private void setupFloatingRenderer(CraftingTableAnimationController animationController, float partialTick, PoseStack poseStack, ItemStack itemStack, int index) {
        // -0.0125 to 0.0125
        float shift = (float) Math.abs(((animationController.ticks + partialTick) * 50.0 + (index * 1000L)) % 5000L - 2500L) / 200000.0F;
        BakedModel model = this.itemRenderer.getModel(itemStack, null, null, 0);
        boolean blockItem = model.isGui3d();
        poseStack.translate(0.5, shift, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, animationController.currentAngle, animationController.nextAngle)));
        poseStack.translate((double) (index % 3) * 3.0 / 16.0 + 0.3125 - 0.5, 1.09375, (double) (index / 3) * 3.0 / 16.0 + 0.3125 - 0.5);
        float scale = blockItem ? 0.24F : 0.18F;
        poseStack.scale(scale, scale, scale);
    }

    private void setupFlatRenderer(CraftingTableAnimationController animationController, float partialTick, PoseStack poseStack, ItemStack itemStack, int index) {
        BakedModel bakedModel = this.itemRenderer.getModel(itemStack, null, null, 0);
        boolean gui3d = bakedModel.isGui3d();
        poseStack.translate(0.5, 0.0, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, animationController.currentAngle, animationController.nextAngle)));
        poseStack.translate((double) (index % 3) * 3.0 / 16.0 + 0.3125 - 0.5, gui3d ? 1.0625 : 1.005, (double) (index / 3) * 3.0 / 16.0 + 0.3125 - 0.5);
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        float scale = gui3d ? 0.25F : 0.175F;
        poseStack.scale(scale, scale, scale);
    }

    private void renderResultItem(ItemStack itemStack, @Nullable Level level, float time, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        if (!VisualWorkbench.CONFIG.get(ClientConfig.class).renderResult) return;
        poseStack.pushPose();
        poseStack.translate(0.5F, 1.15F, 0.5F);
        BakedModel model = this.itemRenderer.getModel(itemStack, level, null, 0);
        float hoverOffset = Mth.sin(time / 10.0F) * 0.04F + 0.1F;
        float modelYScale = model.getTransforms().getTransform(ItemDisplayContext.GROUND).scale.y();
        poseStack.translate(0.0, hoverOffset + 0.25F * modelYScale, 0.0);
        poseStack.mulPose(Axis.YP.rotation(time / 20.0F));
        if (!model.isGui3d()) poseStack.scale(0.75F, 0.75F, 0.75F);
        this.itemRenderer.render(itemStack, ItemDisplayContext.GROUND, false, poseStack, multiBufferSource, packedLight, OverlayTexture.NO_OVERLAY, model);
        poseStack.popPose();
    }
}
