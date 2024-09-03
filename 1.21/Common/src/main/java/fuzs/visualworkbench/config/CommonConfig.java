package fuzs.visualworkbench.config;

import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;

public class CommonConfig implements ConfigCore {
    @Config(description = "Leftover vanilla crafting tables in a world become unusable until they are broken and replaced.")
    public boolean disableVanillaWorkbench = true;
    @Config(description = "Replace vanilla crafting tables created in structures during world generation. Does not affect already generated blocks.")
    public boolean convertVanillaWorkbenchDuringWorldGen = true;
}
