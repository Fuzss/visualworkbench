package fuzs.visualworkbench.client;

import fuzs.puzzleslib.client.core.ClientCoreServices;
import fuzs.visualworkbench.VisualWorkbench;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod.EventBusSubscriber(modid = VisualWorkbench.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class VisualWorkbenchForgeClient {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ClientCoreServices.FACTORIES.clientModConstructor(VisualWorkbench.MOD_ID).accept(new VisualWorkbenchClient());
    }
}
