package fuzs.visualworkbench.element;

import fuzs.puzzleslib.PuzzlesLib;
import fuzs.puzzleslib.element.extension.ClientExtensibleElement;
import fuzs.visualworkbench.VisualWorkbench;
import fuzs.visualworkbench.block.IWorkbenchTileEntityProvider;
import fuzs.visualworkbench.client.element.VisualWorkbenchExtension;
import fuzs.visualworkbench.inventory.container.VisualWorkbenchContainer;
import fuzs.visualworkbench.tileentity.WorkbenchTileEntity;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

public class VisualWorkbenchElement extends ClientExtensibleElement<VisualWorkbenchExtension> {

    public static final Tags.IOptionalNamedTag<Block> NON_VISUAL_WORKBENCHES_TAG = BlockTags.createOptional(new ResourceLocation(VisualWorkbench.MODID, "non_visual_workbenches"));

    @ObjectHolder(VisualWorkbench.MODID + ":" + "crafting_table")
    public static final TileEntityType<WorkbenchTileEntity> WORKBENCH_TILE_ENTITY = null;

    @ObjectHolder(VisualWorkbench.MODID + ":" + "crafting")
    public static final ContainerType<VisualWorkbenchContainer> CRAFTING_CONTAINER = null;

    public VisualWorkbenchElement() {

        super(element -> new VisualWorkbenchExtension((VisualWorkbenchElement) element));
    }

    @Override
    public String[] getDescription() {

        return new String[]{"Items stay inside of crafting tables and are also rendered on top. It's really fancy!"};
    }

    @Override
    public void setupCommon() {

        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(TileEntityType.class, this::onRegistryRegister);
        PuzzlesLib.getRegistryManager().register("crafting", new ContainerType<>(VisualWorkbenchContainer::new));
    }

    @SuppressWarnings("ConstantConditions")
    private void onRegistryRegister(final RegistryEvent.Register<TileEntityType<?>> evt) {

        // blocks are registered first, so this works for modded crafting tables
        TileEntityType<WorkbenchTileEntity> tileEntityType = TileEntityType.Builder.of(WorkbenchTileEntity::new, ForgeRegistries.BLOCKS.getValues().stream()
                .filter(block -> block instanceof IWorkbenchTileEntityProvider && ((IWorkbenchTileEntityProvider) block).hasWorkbenchTileEntity())
                .toArray(Block[]::new)).build(null);

        tileEntityType.setRegistryName(new ResourceLocation(VisualWorkbench.MODID, "crafting_table"));
        evt.getRegistry().register(tileEntityType);
    }

}
