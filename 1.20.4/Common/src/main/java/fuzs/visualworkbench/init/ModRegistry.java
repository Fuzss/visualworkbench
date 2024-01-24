package fuzs.visualworkbench.init;

import fuzs.puzzleslib.api.init.v2.RegistryManager;
import fuzs.puzzleslib.api.init.v2.RegistryReference;
import fuzs.puzzleslib.api.init.v3.tags.BoundTagFactory;
import fuzs.visualworkbench.VisualWorkbench;
import fuzs.visualworkbench.handler.OpenMenuHandler;
import fuzs.visualworkbench.mixin.accessor.BlockEntityTypeBuilderAccessor;
import fuzs.visualworkbench.world.inventory.VisualCraftingMenu;
import fuzs.visualworkbench.world.level.block.entity.VisualCraftingTableBlockEntity;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModRegistry {
    static final RegistryManager REGISTRY = RegistryManager.instant(VisualWorkbench.MOD_ID);
    public static final RegistryReference<BlockEntityType<VisualCraftingTableBlockEntity>> CRAFTING_TABLE_BLOCK_ENTITY = REGISTRY.registerBlockEntityType("crafting_table", () -> {
        BlockEntityType.Builder<VisualCraftingTableBlockEntity> builder = BlockEntityType.Builder.of(VisualCraftingTableBlockEntity::new);
        BlockEntityTypeBuilderAccessor.class.cast(builder).visualworkbench$setValidBlocks(OpenMenuHandler.BLOCK_CONVERSIONS.values());
        return builder;
    });
    public static final RegistryReference<MenuType<VisualCraftingMenu>> CRAFTING_MENU_TYPE = REGISTRY.registerMenuType("crafting", () -> VisualCraftingMenu::new);

    static final BoundTagFactory TAGS = BoundTagFactory.make(VisualWorkbench.MOD_ID);
    public static final TagKey<Block> NON_VISUAL_WORKBENCHES_BLOCK_TAG = TAGS.registerBlockTag("non_visual_workbenches");

    public static void touch() {

    }
}
