package fuzs.visualworkbench.forge;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.visualworkbench.VisualWorkbench;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod(VisualWorkbench.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class VisualWorkbenchForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ModConstructor.construct(VisualWorkbench.MOD_ID, VisualWorkbench::new);
    }
}
