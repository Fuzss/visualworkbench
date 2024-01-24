package fuzs.visualworkbench.config;

import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;

public class ClientConfig implements ConfigCore {
    @Config(description = "Makes crafting table contents lay flat on the table instead of floating above.")
    public boolean flatRendering = false;
    @Config(description = "Render the result of the crafting operation in addition to crafting table contents.")
    public boolean renderResult = true;
    @Config(description = "Rotate crafting table contents so they always face the closest player.")
    public boolean rotateIngredients = true;
}
