package fuzs.visualworkbench.element;

import fuzs.puzzleslib.PuzzlesLib;
import fuzs.puzzleslib.element.extension.ClientExtensibleElement;
import fuzs.visualworkbench.VisualWorkbench;
import fuzs.visualworkbench.client.element.VisualWorkbenchExtension;
import fuzs.visualworkbench.inventory.container.VisualWorkbenchContainer;
import fuzs.visualworkbench.tileentity.WorkbenchTileEntity;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class VisualWorkbenchElement extends ClientExtensibleElement<VisualWorkbenchExtension> {

    @ObjectHolder(VisualWorkbench.MODID + ":" + "crafting_table")
    public static final TileEntityType<WorkbenchTileEntity> WORKBENCH_TILE_ENTITY = null;

    @ObjectHolder(VisualWorkbench.MODID + ":" + "crafting")
    public static final ContainerType<VisualWorkbenchContainer> CRAFTING_CONTAINER = null;

    public VisualWorkbenchElement() {

        super(element -> new VisualWorkbenchExtension((VisualWorkbenchElement) element));
    }

    @Override
    public String[] getDescription() {

        return new String[]{"Changing the in-game models to allow for better animations and subtle effects."};
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void setupCommon() {

        PuzzlesLib.getRegistryManager().register("crafting_table", TileEntityType.Builder.of(WorkbenchTileEntity::new, Blocks.CRAFTING_TABLE).build(null));
        PuzzlesLib.getRegistryManager().register("crafting", new ContainerType<>(VisualWorkbenchContainer::new));
    }

}
