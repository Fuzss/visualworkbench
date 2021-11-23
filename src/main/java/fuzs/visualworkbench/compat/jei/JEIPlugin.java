package fuzs.visualworkbench.compat.jei;

import fuzs.visualworkbench.VisualWorkbench;
import fuzs.visualworkbench.world.inventory.ModCraftingMenu;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
    @Nonnull
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(VisualWorkbench.MOD_ID, "crafting");
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(ModCraftingMenu.class, VanillaRecipeCategoryUid.CRAFTING, 1, 9, 10, 36);
    }
}
