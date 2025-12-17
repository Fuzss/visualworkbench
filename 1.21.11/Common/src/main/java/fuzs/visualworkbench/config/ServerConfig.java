package fuzs.visualworkbench.config;

import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;

public class ServerConfig implements ConfigCore {
    @Config(description = "Leftover vanilla crafting tables will transform when trying to use them.")
    public boolean convertVanillaWorkbenchWhenInteracting = true;
}
