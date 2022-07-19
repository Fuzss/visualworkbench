package fuzs.visualworkbench.compat.jei;

import fuzs.visualworkbench.VisualWorkbench;
import fuzs.visualworkbench.registry.ModRegistry;
import fuzs.visualworkbench.world.inventory.ModCraftingMenu;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

@JeiPlugin
public class VisualWorkbenchJEIPlugin implements IModPlugin {

    @Nonnull
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(VisualWorkbench.MOD_ID, "crafting");
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(ModCraftingMenu.class, ModRegistry.CRAFTING_MENU_TYPE.get(), RecipeTypes.CRAFTING, 1, 9, 10, 36);
    }
}
