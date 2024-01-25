package fuzs.visualworkbench.forge.client;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.visualworkbench.VisualWorkbench;
import fuzs.visualworkbench.client.VisualWorkbenchClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod.EventBusSubscriber(modid = VisualWorkbench.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class VisualWorkbenchForgeClient {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ClientModConstructor.construct(VisualWorkbench.MOD_ID, VisualWorkbenchClient::new);
    }
}
