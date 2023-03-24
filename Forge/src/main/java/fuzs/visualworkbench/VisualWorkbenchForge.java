package fuzs.visualworkbench;

import fuzs.puzzleslib.core.CoreServices;
import fuzs.visualworkbench.handler.OpenMenuHandler;
import net.minecraft.world.InteractionResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

import java.util.Optional;

@Mod(VisualWorkbench.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class VisualWorkbenchForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        CoreServices.FACTORIES.modConstructor(VisualWorkbench.MOD_ID).accept(new VisualWorkbench());
        registerHandlers();
    }

    private static void registerHandlers() {
        MinecraftForge.EVENT_BUS.addListener((final PlayerInteractEvent.RightClickBlock evt) -> {
            Optional<InteractionResult> result = OpenMenuHandler.onUseBlock(evt.getEntity(), evt.getLevel(), evt.getHand(), evt.getHitVec());
            if (result.isPresent()) {
                evt.setCancellationResult(result.get());
                evt.setCanceled(true);
            }
        });
    }
}
