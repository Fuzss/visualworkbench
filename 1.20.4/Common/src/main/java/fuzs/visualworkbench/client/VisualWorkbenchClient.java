package fuzs.visualworkbench.client;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.core.v1.context.BlockEntityRenderersContext;
import fuzs.puzzleslib.api.client.core.v1.context.MenuScreensContext;
import fuzs.puzzleslib.api.client.event.v1.ModelEvents;
import fuzs.puzzleslib.api.core.v1.context.PackRepositorySourcesContext;
import fuzs.puzzleslib.api.event.v1.LoadCompleteCallback;
import fuzs.puzzleslib.api.resources.v1.DynamicPackResources;
import fuzs.puzzleslib.api.resources.v1.PackResourcesHelper;
import fuzs.visualworkbench.VisualWorkbench;
import fuzs.visualworkbench.client.handler.BlockModelHandler;
import fuzs.visualworkbench.client.renderer.blockentity.CraftingTableBlockEntityRenderer;
import fuzs.visualworkbench.data.client.DynamicModelProvider;
import fuzs.visualworkbench.init.ModRegistry;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;

public class VisualWorkbenchClient implements ClientModConstructor {

    @Override
    public void onConstructMod() {
        registerHandlers();
    }

    private static void registerHandlers() {
        ModelEvents.MODIFY_UNBAKED_MODEL.register(BlockModelHandler::onModifyUnbakedModel);
        LoadCompleteCallback.EVENT.register(BlockModelHandler::onLoadComplete);
    }

    @Override
    public void onRegisterMenuScreens(MenuScreensContext context) {
        context.registerMenuScreen(ModRegistry.CRAFTING_MENU_TYPE.value(), CraftingScreen::new);
    }

    @Override
    public void onRegisterBlockEntityRenderers(BlockEntityRenderersContext context) {
        context.registerBlockEntityRenderer(ModRegistry.CRAFTING_TABLE_BLOCK_ENTITY.value(), CraftingTableBlockEntityRenderer::new);
    }

    @Override
    public void onAddResourcePackFinders(PackRepositorySourcesContext context) {
        context.addRepositorySource(PackResourcesHelper.buildClientPack(VisualWorkbench.id("default_block_models"), DynamicPackResources.create(DynamicModelProvider::new), true));
    }
}
