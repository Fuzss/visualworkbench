package fuzs.visualworkbench;

import fuzs.puzzleslib.config.ConfigHolder;
import fuzs.puzzleslib.config.ConfigHolderImpl;
import fuzs.visualworkbench.config.ClientConfig;
import fuzs.visualworkbench.config.ServerConfig;
import fuzs.visualworkbench.registry.ModRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(VisualWorkbench.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class VisualWorkbench {
    public static final String MOD_ID = "visualworkbench";
    public static final String MOD_NAME = "Visual Workbench";
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    @SuppressWarnings("Convert2MethodRef")
    public static final ConfigHolder<ClientConfig, ServerConfig> CONFIG = ConfigHolder.of(() -> new ClientConfig(), () -> new ServerConfig());

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ((ConfigHolderImpl<?, ?>) CONFIG).addConfigs(MOD_ID);
        ModRegistry.touch();
    }
}
