package fuzs.visualworkbench.config;

import fuzs.puzzleslib.config.AbstractConfig;
import fuzs.puzzleslib.config.annotation.Config;

public class ClientConfig extends AbstractConfig {
    @Config(description = "Makes crafting table contents lay flat on the table instead of floating above.")
    public boolean flatRendering = false;

    public ClientConfig() {
        super("");
    }
}
