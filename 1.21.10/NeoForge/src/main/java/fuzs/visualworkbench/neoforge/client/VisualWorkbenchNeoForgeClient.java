package fuzs.visualworkbench.neoforge.client;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import fuzs.visualworkbench.VisualWorkbench;
import fuzs.visualworkbench.client.VisualWorkbenchClient;
import fuzs.visualworkbench.data.client.ModLanguageProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = VisualWorkbench.MOD_ID, dist = Dist.CLIENT)
public class VisualWorkbenchNeoForgeClient {

    public VisualWorkbenchNeoForgeClient() {
        ClientModConstructor.construct(VisualWorkbench.MOD_ID, VisualWorkbenchClient::new);
        DataProviderHelper.registerDataProviders(VisualWorkbench.MOD_ID, ModLanguageProvider::new);
    }
}
