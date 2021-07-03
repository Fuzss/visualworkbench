package fuzs.visualworkbench.compat.jei;

import fuzs.visualworkbench.VisualWorkbench;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.client.gui.screen.inventory.CraftingScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

/**
 * mostly copied from https://github.com/Tfarcenim/crafting-station
 */
@SuppressWarnings("unused")
@JeiPlugin
public class JEIPlugin implements IModPlugin, IGuiContainerHandler<CraftingScreen> {

    @Nonnull
    @Override
    public ResourceLocation getPluginUid() {

        return new ResourceLocation(VisualWorkbench.MODID, VisualWorkbench.MODID);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {

        registration.addRecipeTransferHandler(new WorkbenchTransferInfo());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {

        registration.addRecipeCatalyst(new ItemStack(Items.CRAFTING_TABLE), VanillaRecipeCategoryUid.CRAFTING);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {

        registration.addGuiContainerHandler(CraftingScreen.class,this);
    }

}
