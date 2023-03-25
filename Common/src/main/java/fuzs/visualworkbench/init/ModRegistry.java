package fuzs.visualworkbench.init;

import fuzs.puzzleslib.api.init.v2.RegistryManager;
import fuzs.puzzleslib.api.init.v2.RegistryReference;
import fuzs.visualworkbench.VisualWorkbench;
import fuzs.visualworkbench.config.JsonConfigBuilder;
import fuzs.visualworkbench.world.inventory.ModCraftingMenu;
import fuzs.visualworkbench.world.level.block.entity.CraftingTableBlockEntity;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModRegistry {
    static final RegistryManager REGISTRY = RegistryManager.instant(VisualWorkbench.MOD_ID);
    public static final RegistryReference<BlockEntityType<CraftingTableBlockEntity>> CRAFTING_TABLE_BLOCK_ENTITY = REGISTRY.registerBlockEntityType("crafting_table", () -> BlockEntityType.Builder.of(CraftingTableBlockEntity::new, JsonConfigBuilder.INSTANCE.getBlockStream().toArray(Block[]::new)));
    public static final RegistryReference<MenuType<ModCraftingMenu>> CRAFTING_MENU_TYPE = REGISTRY.registerMenuType("crafting", () -> ModCraftingMenu::new);

    public static void touch() {

    }
}
