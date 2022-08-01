package fuzs.visualworkbench;

import fuzs.puzzleslib.config.ConfigHolder;
import fuzs.puzzleslib.core.CoreServices;
import fuzs.puzzleslib.core.ModConstructor;
import fuzs.visualworkbench.config.ClientConfig;
import fuzs.visualworkbench.config.JsonConfigBuilder;
import fuzs.visualworkbench.registry.ModRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VisualWorkbench implements ModConstructor {
    public static final String MOD_ID = "visualworkbench";
    public static final String MOD_NAME = "Visual Workbench";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    @SuppressWarnings("Convert2MethodRef")
    public static final ConfigHolder CONFIG = CoreServices.FACTORIES.clientConfig(ClientConfig.class, () -> new ClientConfig());

    @Override
    public void onConstructMod() {
        CONFIG.bakeConfigs(MOD_ID);
        JsonConfigBuilder.INSTANCE.load();
        ModRegistry.touch();
    }
}
