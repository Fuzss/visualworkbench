package fuzs.visualworkbench;

import fuzs.puzzleslib.core.CoreServices;
import fuzs.visualworkbench.handler.OpenMenuHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;

public class VisualWorkbenchFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        CoreServices.FACTORIES.modConstructor(VisualWorkbench.MOD_ID).accept(new VisualWorkbench());
        registerHandlers();
    }

    private static void registerHandlers() {
        OpenMenuHandler openMenuHandler = new OpenMenuHandler();
        UseBlockCallback.EVENT.register(openMenuHandler::onUseBlock);
    }
}
