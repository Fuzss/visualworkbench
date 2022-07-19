package fuzs.visualworkbench;

import fuzs.puzzleslib.core.CoreServices;
import fuzs.visualworkbench.handler.OpenMenuHandler;
import net.minecraft.world.InteractionResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod(VisualWorkbench.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class VisualWorkbenchForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        CoreServices.FACTORIES.modConstructor(VisualWorkbench.MOD_ID).accept(new VisualWorkbench());
        registerHandlers();
    }

    private static void registerHandlers() {
        OpenMenuHandler openMenuHandler = new OpenMenuHandler();
        MinecraftForge.EVENT_BUS.addListener((final PlayerInteractEvent.RightClickBlock evt) -> {
            InteractionResult result = openMenuHandler.onUseBlock(evt.getEntity(), evt.getLevel(), evt.getHand(), evt.getHitVec());
            if (result != InteractionResult.PASS) {
                evt.setCancellationResult(result);
                evt.setCanceled(true);
            }
        });
    }
}
