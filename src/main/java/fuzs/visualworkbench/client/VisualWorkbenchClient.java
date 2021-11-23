package fuzs.visualworkbench.client;

import fuzs.visualworkbench.VisualWorkbench;
import fuzs.visualworkbench.client.renderer.blockentity.WorkbenchTileEntityRenderer;
import fuzs.visualworkbench.registry.ModRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = VisualWorkbench.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class VisualWorkbenchClient {
    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent evt) {
        MenuScreens.register(ModRegistry.CRAFTING_MENU_TYPE.get(), CraftingScreen::new);
        BlockEntityRenderers.register(ModRegistry.CRAFTING_TABLE_BLOCK_ENTITY.get(), WorkbenchTileEntityRenderer::new);
    }
}
