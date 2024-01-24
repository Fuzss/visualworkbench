package fuzs.visualworkbench.client;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.core.v1.context.BlockEntityRenderersContext;
import fuzs.puzzleslib.api.core.v1.context.PackRepositorySourcesContext;
import fuzs.puzzleslib.api.resources.v1.DynamicPackResources;
import fuzs.puzzleslib.api.resources.v1.PackResourcesHelper;
import fuzs.visualworkbench.VisualWorkbench;
import fuzs.visualworkbench.client.renderer.blockentity.WorkbenchBlockEntityRenderer;
import fuzs.visualworkbench.data.client.DynamicModelProvider;
import fuzs.visualworkbench.init.ModRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;

public class VisualWorkbenchClient implements ClientModConstructor {

    @Override
    public void onClientSetup() {
        MenuScreens.register(ModRegistry.CRAFTING_MENU_TYPE.get(), CraftingScreen::new);
    }

    @Override
    public void onRegisterBlockEntityRenderers(BlockEntityRenderersContext context) {
        context.registerBlockEntityRenderer(ModRegistry.CRAFTING_TABLE_BLOCK_ENTITY.get(), WorkbenchBlockEntityRenderer::new);
    }

    @Override
    public void onAddResourcePackFinders(PackRepositorySourcesContext context) {
        context.addRepositorySource(PackResourcesHelper.buildClientPack(VisualWorkbench.id("default_block_models"), DynamicPackResources.create(DynamicModelProvider::new), true));
    }
}
