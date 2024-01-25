package fuzs.visualworkbench.data;

import fuzs.puzzleslib.api.data.v2.AbstractTagProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.visualworkbench.init.ModRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.Blocks;

public class ModBlockTagsProvider extends AbstractTagProvider.Blocks {

    public ModBlockTagsProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addTags(HolderLookup.Provider provider) {
        this.tag(ModRegistry.UNALTERED_WORKBENCHES_BLOCK_TAG).add(Blocks.SMITHING_TABLE, Blocks.FLETCHING_TABLE);
    }
}
