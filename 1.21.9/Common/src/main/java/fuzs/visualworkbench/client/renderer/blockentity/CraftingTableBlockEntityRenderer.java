package fuzs.visualworkbench.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import fuzs.visualworkbench.VisualWorkbench;
import fuzs.visualworkbench.client.renderer.blockentity.state.CraftingTableRenderState;
import fuzs.visualworkbench.config.ClientConfig;
import fuzs.visualworkbench.world.level.block.entity.CraftingTableAnimationController;
import fuzs.visualworkbench.world.level.block.entity.WorkbenchVisualsProvider;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class CraftingTableBlockEntityRenderer<T extends BlockEntity & Container & WorkbenchVisualsProvider> implements BlockEntityRenderer<T, CraftingTableRenderState> {
    private final ItemModelResolver itemModelResolver;

    public CraftingTableBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public CraftingTableRenderState createRenderState() {
        return new CraftingTableRenderState();
    }

    @Override
    public void extractRenderState(T blockEntity, CraftingTableRenderState renderState, float partialTick, Vec3 cameraPosition, @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity,
                renderState,
                partialTick,
                cameraPosition,
                breakProgress);
        renderState.result.clear();
        int position = (int) blockEntity.getBlockPos().asLong();
        if (VisualWorkbench.CONFIG.get(ClientConfig.class).resultRendering) {
            this.itemModelResolver.updateForTopItem(renderState.result,
                    blockEntity.getCraftingResult(),
                    ItemDisplayContext.GROUND,
                    blockEntity.getLevel(),
                    null,
                    position);
        }

        renderState.items = new ArrayList<>();
        if (VisualWorkbench.CONFIG.get(ClientConfig.class).ingredientRendering != ClientConfig.IngredientRendering.NONE) {
            for (int j = 0; j < blockEntity.getContainerSize(); j++) {
                ItemStackRenderState itemStackRenderState = new ItemStackRenderState();
                this.itemModelResolver.updateForTopItem(itemStackRenderState,
                        blockEntity.getItem(j),
                        ItemDisplayContext.FIXED,
                        blockEntity.getLevel(),
                        null,
                        position + j);
                renderState.items.add(itemStackRenderState);
            }
        }

        CraftingTableAnimationController animationController = blockEntity.getAnimationController();
        renderState.time = animationController.getTime() + partialTick;
        renderState.angle = Mth.lerp(partialTick,
                animationController.getCurrentAngle(),
                animationController.getNextAngle());
        // light is normally always 0 since it checks inside the crafting table block which is solid, but contents are rendered in the block above
        renderState.itemLightCoords = blockEntity.getLevel() != null ?
                LevelRenderer.getLightColor(blockEntity.getLevel(), blockEntity.getBlockPos().above()) : 0XF000F0;
    }

    @Override
    public void submit(CraftingTableRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        for (int i = 0; i < renderState.items.size(); ++i) {
            switch (VisualWorkbench.CONFIG.get(ClientConfig.class).ingredientRendering) {
                case FLAT -> this.submitFlatItemList(renderState, poseStack, nodeCollector, i);
                case FLOATING -> this.submitFloatingItemList(renderState, poseStack, nodeCollector, i);
            }
        }

        this.submitFloatingItemStack(renderState, poseStack, nodeCollector);
    }

    private void submitFlatItemList(CraftingTableRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, int index) {
        ItemStackRenderState itemStackRenderState = renderState.items.get(index);
        if (!itemStackRenderState.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.5, 0.0, 0.5);
            poseStack.mulPose(Axis.YP.rotationDegrees(renderState.angle));
            poseStack.translate((double) (index % 3) * 3.0 / 16.0 + 0.3125 - 0.5,
                    isGui3d(itemStackRenderState) ? 1.0625 : 1.005,
                    (double) (index / 3) * 3.0 / 16.0 + 0.3125 - 0.5);
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
            float scale = isGui3d(itemStackRenderState) ? 0.25F : 0.175F;
            poseStack.scale(scale, scale, scale);
            itemStackRenderState.submit(poseStack,
                    nodeCollector,
                    renderState.itemLightCoords,
                    OverlayTexture.NO_OVERLAY,
                    0);
            poseStack.popPose();
        }
    }

    private void submitFloatingItemList(CraftingTableRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, int index) {
        ItemStackRenderState itemStackRenderState = renderState.items.get(index);
        if (!itemStackRenderState.isEmpty()) {
            poseStack.pushPose();
            // -0.0125 to 0.0125
            float shift = (float) Math.abs((renderState.time * 50.0 + (index * 1000L)) % 5000L - 2500L) / 200000.0F;
            poseStack.translate(0.5, shift, 0.5);
            poseStack.mulPose(Axis.YP.rotationDegrees(renderState.angle));
            poseStack.translate((double) (index % 3) * 3.0 / 16.0 + 0.3125 - 0.5,
                    1.09375,
                    (double) (index / 3) * 3.0 / 16.0 + 0.3125 - 0.5);
            float scale = isGui3d(itemStackRenderState) ? 0.24F : 0.18F;
            poseStack.scale(scale, scale, scale);
            itemStackRenderState.submit(poseStack,
                    nodeCollector,
                    renderState.itemLightCoords,
                    OverlayTexture.NO_OVERLAY,
                    0);
            poseStack.popPose();
        }
    }

    private void submitFloatingItemStack(CraftingTableRenderState renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector) {
        ItemStackRenderState itemStackRenderState = renderState.result;
        if (!itemStackRenderState.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.5F, 1.15F, 0.5F);
            float hoverOffset = Mth.sin(renderState.time / 10.0F) * 0.04F + 0.1F;
            AABB aABB = itemStackRenderState.getModelBoundingBox();
            float modelYScale = -((float) aABB.minY) + 0.0625F;
            poseStack.translate(0.0, hoverOffset + modelYScale, 0.0);
            poseStack.mulPose(Axis.YP.rotation(renderState.time / 20.0F));
            if (!isGui3d(itemStackRenderState)) {
                poseStack.scale(0.75F, 0.75F, 0.75F);
            }

            itemStackRenderState.submit(poseStack,
                    nodeCollector,
                    renderState.itemLightCoords,
                    OverlayTexture.NO_OVERLAY,
                    0);
            poseStack.popPose();
        }
    }

    private static boolean isGui3d(ItemStackRenderState renderState) {
        return renderState.getModelBoundingBox().getZsize() > 0.0625;
    }
}
