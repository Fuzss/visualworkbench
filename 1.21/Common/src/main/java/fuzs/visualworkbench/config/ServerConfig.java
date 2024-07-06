package fuzs.visualworkbench.config;

import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;

public class ServerConfig implements ConfigCore {
    @Config(description = "Leftover vanilla crafting tables in a world become unusable until they are broken and replaced.")
    public boolean disableVanillaWorkbench = true;
}
