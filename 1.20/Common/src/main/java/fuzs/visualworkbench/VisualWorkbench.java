package fuzs.visualworkbench;

import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.event.v1.core.EventPhase;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerInteractEvents;
import fuzs.visualworkbench.config.ClientConfig;
import fuzs.visualworkbench.config.JsonConfigBuilder;
import fuzs.visualworkbench.handler.OpenMenuHandler;
import fuzs.visualworkbench.init.ModRegistry;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VisualWorkbench implements ModConstructor {
    public static final String MOD_ID = "visualworkbench";
    public static final String MOD_NAME = "Visual Workbench";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static final ConfigHolder CONFIG = ConfigHolder.builder(MOD_ID).client(ClientConfig.class);

    @Override
    public void onConstructMod() {
        JsonConfigBuilder.INSTANCE.load();
        ModRegistry.touch();
        registerHandlers();
    }

    private static void registerHandlers() {
        // let other run first, we just reproduce the vanilla screen opening basically
        PlayerInteractEvents.USE_BLOCK.register(EventPhase.AFTER, OpenMenuHandler::onUseBlock);
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
