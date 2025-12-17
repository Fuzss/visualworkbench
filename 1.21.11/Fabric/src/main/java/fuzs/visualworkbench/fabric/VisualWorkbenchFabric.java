package fuzs.visualworkbench.fabric;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.visualworkbench.VisualWorkbench;
import net.fabricmc.api.ModInitializer;

public class VisualWorkbenchFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ModConstructor.construct(VisualWorkbench.MOD_ID, VisualWorkbench::new);
    }
}
