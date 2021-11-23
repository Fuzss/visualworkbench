package fuzs.visualworkbench.config;

import com.google.common.collect.Lists;
import fuzs.puzzleslib.config.AbstractConfig;
import fuzs.puzzleslib.config.annotation.Config;
import fuzs.puzzleslib.config.serialization.EntryCollectionBuilder;
import fuzs.visualworkbench.VisualWorkbench;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Set;

public class ServerConfig extends AbstractConfig {
    @Config(name = "Workbench Blacklist", description = {"Workbenches disabled from being able to store crafting ingredients.", "Modders may add their own incompatible workbenches via the \"" + VisualWorkbench.MOD_ID + ":non_visual_workbenches\" block tag.", EntryCollectionBuilder.CONFIG_DESCRIPTION})
    private List<String> workbenchBlacklistRaw = Lists.newArrayList();

    public Set<Block> workbenchBlacklist;

    public ServerConfig() {
        super("");
    }

    @Override
    protected void afterConfigReload() {
        this.workbenchBlacklist = EntryCollectionBuilder.of(ForgeRegistries.BLOCKS).buildSet(this.workbenchBlacklistRaw);
    }
}
