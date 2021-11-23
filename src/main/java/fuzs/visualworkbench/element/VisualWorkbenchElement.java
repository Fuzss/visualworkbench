package fuzs.visualworkbench.element;

import com.google.common.collect.Lists;
import fuzs.puzzleslib.PuzzlesLib;
import fuzs.puzzleslib.config.ConfigManager;
import fuzs.puzzleslib.config.option.OptionsBuilder;
import fuzs.puzzleslib.config.serialization.EntryCollectionBuilder;
import fuzs.puzzleslib.element.extension.ClientExtensibleElement;
import fuzs.visualworkbench.VisualWorkbench;
import fuzs.visualworkbench.block.CraftingBlockEntityProvider;
import fuzs.visualworkbench.client.element.VisualWorkbenchExtension;
import fuzs.visualworkbench.inventory.container.VisualWorkbenchContainer;
import fuzs.visualworkbench.tileentity.WorkbenchTileEntity;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

import java.util.Set;

public class VisualWorkbenchElement extends ClientExtensibleElement<VisualWorkbenchExtension> {
    @ObjectHolder(VisualWorkbench.MODID + ":" + "crafting_table")
    public static final TileEntityType<WorkbenchTileEntity> WORKBENCH_TILE_ENTITY = null;
    @ObjectHolder(VisualWorkbench.MODID + ":" + "crafting")
    public static final ContainerType<VisualWorkbenchContainer> CRAFTING_CONTAINER = null;

    public static final Tags.IOptionalNamedTag<Block> NON_VISUAL_WORKBENCHES_TAG = BlockTags.createOptional(new ResourceLocation(VisualWorkbench.MODID, "non_visual_workbenches"));

    public Set<Block> workbenchBlacklist;

    public VisualWorkbenchElement() {
        super(element -> new VisualWorkbenchExtension((VisualWorkbenchElement) element));
    }

    @Override
    public String[] getDescription() {
        return new String[]{"Items stay inside of crafting tables and are also rendered on top. It's really fancy!"};
    }

    @Override
    protected boolean isPersistent() {
        return true;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void constructCommon() {
        // blocks are registered first, so this works for modded crafting tables
        PuzzlesLib.getRegistryManagerV2().registerTileEntityType("crafting_table", () -> TileEntityType.Builder.of(WorkbenchTileEntity::new, ForgeRegistries.BLOCKS.getValues().stream()
                .filter(block -> block instanceof CraftingBlockEntityProvider && ((CraftingBlockEntityProvider) block).withCraftingBlockEntity(false))
                .toArray(Block[]::new)).build(null));
        PuzzlesLib.getRegistryManagerV2().registerContainerType("crafting", () -> new ContainerType<>(VisualWorkbenchContainer::new));
    }

    @Override
    public void setupCommonConfig(OptionsBuilder builder) {
        builder.define("Trader Blacklist", Lists.<String>newArrayList()).comment("Workbenches disabled from being able to store crafting ingredients.", "Modders may add their own incompatible workbenches via the \"" + VisualWorkbench.MODID + ":non_visual_workbenches\" block tag.", EntryCollectionBuilder.CONFIG_STRING).sync(v -> this.workbenchBlacklist = ConfigManager.deserializeToSet(v, ForgeRegistries.BLOCKS));
    }
}
