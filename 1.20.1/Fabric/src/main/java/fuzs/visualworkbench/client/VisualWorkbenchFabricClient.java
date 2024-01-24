package fuzs.visualworkbench.client;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.visualworkbench.VisualWorkbench;
import net.fabricmc.api.ClientModInitializer;

public class VisualWorkbenchFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientModConstructor.construct(VisualWorkbench.MOD_ID, VisualWorkbenchClient::new);
    }
}
