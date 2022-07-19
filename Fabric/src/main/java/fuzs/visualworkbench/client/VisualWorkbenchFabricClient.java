package fuzs.visualworkbench.client;

import fuzs.puzzleslib.client.core.ClientCoreServices;
import fuzs.visualworkbench.VisualWorkbench;
import net.fabricmc.api.ClientModInitializer;

public class VisualWorkbenchFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientCoreServices.FACTORIES.clientModConstructor(VisualWorkbench.MOD_ID).accept(new VisualWorkbenchClient());
    }
}
