package fuzs.visualworkbench.integration.jei;

import fuzs.visualworkbench.VisualWorkbench;
import fuzs.visualworkbench.init.ModRegistry;
import fuzs.visualworkbench.world.inventory.VisualCraftingMenu;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class VisualWorkbenchJEIPlugin implements IModPlugin {
    private static final ResourceLocation PLUGIN_ID = VisualWorkbench.id("crafting");

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_ID;
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(VisualCraftingMenu.class,
                ModRegistry.CRAFTING_MENU_TYPE.value(),
                RecipeTypes.CRAFTING,
                1,
                9,
                10,
                36);
    }
}
