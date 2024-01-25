package fuzs.visualworkbench.neoforge;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import fuzs.visualworkbench.VisualWorkbench;
import fuzs.visualworkbench.data.ModBlockTagsProvider;
import fuzs.visualworkbench.data.client.ModLanguageProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;

@Mod(VisualWorkbench.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class VisualWorkbenchNeoForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ModConstructor.construct(VisualWorkbench.MOD_ID, VisualWorkbench::new);
        DataProviderHelper.registerDataProviders(VisualWorkbench.MOD_ID, ModBlockTagsProvider::new, ModLanguageProvider::new);
    }
}
