package fuzs.visualworkbench;

import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.event.v1.RegistryEntryAddedCallback;
import fuzs.puzzleslib.api.event.v1.server.TagsUpdatedCallback;
import fuzs.visualworkbench.config.ClientConfig;
import fuzs.visualworkbench.handler.OpenMenuHandler;
import fuzs.visualworkbench.init.ModRegistry;
import net.minecraft.core.registries.Registries;
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
        ModRegistry.touch();
        registerHandlers();
    }

    private static void registerHandlers() {
        RegistryEntryAddedCallback.registryEntryAdded(Registries.BLOCK).register(OpenMenuHandler::onRegistryEntryAdded);
        TagsUpdatedCallback.EVENT.register(OpenMenuHandler::onTagsUpdated);
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
