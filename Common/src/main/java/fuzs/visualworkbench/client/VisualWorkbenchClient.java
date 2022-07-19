package fuzs.visualworkbench.client;

import fuzs.puzzleslib.client.core.ClientModConstructor;
import fuzs.visualworkbench.client.renderer.blockentity.WorkbenchTileEntityRenderer;
import fuzs.visualworkbench.registry.ModRegistry;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;

public class VisualWorkbenchClient implements ClientModConstructor {

    @Override
    public void onRegisterBlockEntityRenderers(BlockEntityRenderersContext context) {
        context.registerBlockEntityRenderer(ModRegistry.CRAFTING_TABLE_BLOCK_ENTITY.get(), WorkbenchTileEntityRenderer::new);
    }

    @Override
    public void onRegisterMenuScreens(MenuScreensContext context) {
        context.registerMenuScreen(ModRegistry.CRAFTING_MENU_TYPE.get(), CraftingScreen::new);
    }
}
