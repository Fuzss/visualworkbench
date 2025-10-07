package fuzs.visualworkbench.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import fuzs.visualworkbench.VisualWorkbench;
import fuzs.visualworkbench.config.ClientConfig;
import fuzs.visualworkbench.world.level.block.entity.CraftingTableAnimationController;
import fuzs.visualworkbench.world.level.block.entity.WorkbenchVisualsProvider;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class CraftingTableBlockEntityRenderer<T extends BlockEntity & Container & WorkbenchVisualsProvider> implements BlockEntityRenderer<T> {
    private final ItemStackRenderState itemStackRenderState = new ItemStackRenderState();
    private final ItemRenderer itemRenderer;
    public final ItemModelResolver itemModelResolver;

    public CraftingTableBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
        this.itemModelResolver = context.getItemModelResolver();
    }

    @Override
    public void render(T blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, int packedOverlay, Vec3 cameraPosition) {
        Level level = blockEntity.getLevel();
        if (level != null) {
            // light is normally always 0 since it checks inside the crafting table block which is solid, but contents are rendered in the block above
            int packedLightAbove = LevelRenderer.getLightColor(level, blockEntity.getBlockPos().above());
            for (int i = 0; i < blockEntity.getContainerSize(); ++i) {
                ItemStack itemStack = blockEntity.getItem(i);
                if (!itemStack.isEmpty()) {
                    this.renderIngredientItem(blockEntity,
                            level,
                            partialTick,
                            poseStack,
                            multiBufferSource,
                            packedLightAbove,
                            packedOverlay,
                            i,
                            itemStack);
                }
            }
            ItemStack itemStack = blockEntity.getCraftingResult();
            if (!itemStack.isEmpty()) {
                this.renderResultItem(itemStack,
                        level,
                        blockEntity.getAnimationController().renderState().ticks + partialTick,
                        poseStack,
                        multiBufferSource,
                        packedLightAbove,
                        packedOverlay);
            }
        }
    }

    private void renderIngredientItem(T blockEntity, Level level, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, int packedOverlay, int i, ItemStack itemStack) {
        this.itemModelResolver.updateForTopItem(this.itemStackRenderState,
                itemStack,
                ItemDisplayContext.GROUND,
                level,
                null,
                0);
        poseStack.pushPose();
        if (VisualWorkbench.CONFIG.get(ClientConfig.class).flatRendering) {
            this.setupFlatRenderer(blockEntity.getAnimationController().renderState(),
                    isGui3d(this.itemStackRenderState),
                    partialTick,
                    poseStack,
                    i);
        } else {
            this.setupFloatingRenderer(blockEntity.getAnimationController().renderState(),
                    isGui3d(this.itemStackRenderState),
                    partialTick,
                    poseStack,
                    i);
        }
        this.itemRenderer.renderStatic(itemStack,
                ItemDisplayContext.FIXED,
                packedLight,
                packedOverlay,
                poseStack,
                multiBufferSource,
                blockEntity.getLevel(),
                (int) blockEntity.getBlockPos().asLong() + i);
        poseStack.popPose();
    }

    private void setupFloatingRenderer(CraftingTableAnimationController.RenderState renderState, boolean isBlockItem, float partialTick, PoseStack poseStack, int index) {
        // -0.0125 to 0.0125
        float shift = (float) Math.abs(((renderState.ticks + partialTick) * 50.0 + (index * 1000L)) % 5000L - 2500L)
                / 200000.0F;
        poseStack.translate(0.5, shift, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick,
                renderState.currentAngle,
                renderState.nextAngle)));
        poseStack.translate((double) (index % 3) * 3.0 / 16.0 + 0.3125 - 0.5,
                1.09375,
                (double) (index / 3) * 3.0 / 16.0 + 0.3125 - 0.5);
        float scale = isBlockItem ? 0.24F : 0.18F;
        poseStack.scale(scale, scale, scale);
    }

    private void setupFlatRenderer(CraftingTableAnimationController.RenderState renderState, boolean isBlockItem, float partialTick, PoseStack poseStack, int index) {
        poseStack.translate(0.5, 0.0, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick,
                renderState.currentAngle,
                renderState.nextAngle)));
        poseStack.translate((double) (index % 3) * 3.0 / 16.0 + 0.3125 - 0.5,
                isBlockItem ? 1.0625 : 1.005,
                (double) (index / 3) * 3.0 / 16.0 + 0.3125 - 0.5);
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        float scale = isBlockItem ? 0.25F : 0.175F;
        poseStack.scale(scale, scale, scale);
    }

    private void renderResultItem(ItemStack itemStack, Level level, float time, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (!VisualWorkbench.CONFIG.get(ClientConfig.class).renderResult) return;
        this.itemModelResolver.updateForTopItem(this.itemStackRenderState,
                itemStack,
                ItemDisplayContext.GROUND,
                level,
                null,
                0);
        poseStack.pushPose();
        poseStack.translate(0.5F, 1.15F, 0.5F);
        float hoverOffset = Mth.sin(time / 10.0F) * 0.04F + 0.1F;
        AABB aABB = this.itemStackRenderState.getModelBoundingBox();
        float modelYScale = -((float) aABB.minY) + 0.0625F;
        poseStack.translate(0.0, hoverOffset + modelYScale, 0.0);
        poseStack.mulPose(Axis.YP.rotation(time / 20.0F));
        if (!isGui3d(this.itemStackRenderState)) {
            poseStack.scale(0.75F, 0.75F, 0.75F);
        }
        this.itemStackRenderState.render(poseStack, bufferSource, packedLight, packedOverlay);
        poseStack.popPose();
    }

    static boolean isGui3d(ItemStackRenderState renderState) {
        return renderState.getModelBoundingBox().getZsize() > 0.0625;
    }
}
