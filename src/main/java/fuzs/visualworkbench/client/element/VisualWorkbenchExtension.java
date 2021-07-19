package fuzs.visualworkbench.client.element;

import fuzs.puzzleslib.config.option.OptionsBuilder;
import fuzs.puzzleslib.element.extension.ElementExtension;
import fuzs.puzzleslib.element.side.IClientElement;
import fuzs.visualworkbench.client.renderer.tileentity.WorkbenchTileEntityRenderer;
import fuzs.visualworkbench.element.VisualWorkbenchElement;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.inventory.CraftingScreen;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class VisualWorkbenchExtension extends ElementExtension<VisualWorkbenchElement> implements IClientElement {

    public boolean flatRendering;

    public VisualWorkbenchExtension(VisualWorkbenchElement parent) {

        super(parent);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void setupClient2() {

        ScreenManager.register(VisualWorkbenchElement.CRAFTING_CONTAINER, CraftingScreen::new);
        ClientRegistry.bindTileEntityRenderer(VisualWorkbenchElement.WORKBENCH_TILE_ENTITY, WorkbenchTileEntityRenderer::new);
    }

    @Override
    public void setupClientConfig(OptionsBuilder builder) {

        builder.define("Flat Rendering", false).comment("Makes crafting table contents lay flat on the table instead of floating above.").sync(v -> this.flatRendering = v);
    }

}
