package fuzs.visualworkbench.config;

import fuzs.puzzleslib.config.AbstractConfig;
import fuzs.puzzleslib.config.annotation.Config;

public class ClientConfig extends AbstractConfig {
    @Config(description = "Makes crafting table contents lay flat on the table instead of floating above.")
    public boolean flatRendering = false;
    @Config(description = "Render the result of the crafting operation in addition to crafting table contents.")
    public boolean renderResult = true;

    public ClientConfig() {
        super("");
    }
}
