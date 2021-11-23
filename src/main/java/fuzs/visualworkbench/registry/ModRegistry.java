package fuzs.visualworkbench.registry;

import fuzs.puzzleslib.registry.RegistryManager;
import fuzs.visualworkbench.VisualWorkbench;
import fuzs.visualworkbench.world.inventory.ModCraftingMenu;
import fuzs.visualworkbench.world.level.block.CraftingBlockEntityProvider;
import fuzs.visualworkbench.world.level.block.entity.CraftingTableBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

public class ModRegistry {
    private static final RegistryManager REGISTRY = RegistryManager.of(VisualWorkbench.MOD_ID);
    // blocks are registered first, so this works to include modded crafting tables
    public static final RegistryObject<BlockEntityType<CraftingTableBlockEntity>> CRAFTING_TABLE_BLOCK_ENTITY = REGISTRY.registerRawBlockEntityType("crafting_table", () -> BlockEntityType.Builder.of(CraftingTableBlockEntity::new, ForgeRegistries.BLOCKS.getValues().stream()
            .filter(block -> block instanceof CraftingBlockEntityProvider)
            .toArray(Block[]::new)));
    public static final RegistryObject<MenuType<ModCraftingMenu>> CRAFTING_MENU_TYPE = REGISTRY.registerRawMenuType("crafting", () -> ModCraftingMenu::new);

    public static final Tags.IOptionalNamedTag<Block> NON_VISUAL_WORKBENCHES_TAG = BlockTags.createOptional(new ResourceLocation(VisualWorkbench.MOD_ID, "non_visual_workbenches"));

    public static void touch() {

    }
}
