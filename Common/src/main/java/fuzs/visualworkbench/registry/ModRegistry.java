package fuzs.visualworkbench.registry;

import fuzs.puzzleslib.core.CoreServices;
import fuzs.puzzleslib.registry.RegistryManager;
import fuzs.puzzleslib.registry.RegistryReference;
import fuzs.puzzleslib.registry.builder.ModBlockEntityTypeBuilder;
import fuzs.visualworkbench.VisualWorkbench;
import fuzs.visualworkbench.config.JsonConfigBuilder;
import fuzs.visualworkbench.world.inventory.ModCraftingMenu;
import fuzs.visualworkbench.world.level.block.entity.CraftingTableBlockEntity;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModRegistry {
    private static final RegistryManager REGISTRY = CoreServices.FACTORIES.registration(VisualWorkbench.MOD_ID);
    public static final RegistryReference<BlockEntityType<CraftingTableBlockEntity>> CRAFTING_TABLE_BLOCK_ENTITY = REGISTRY.registerBlockEntityTypeBuilder("crafting_table", () -> ModBlockEntityTypeBuilder.of(CraftingTableBlockEntity::new, JsonConfigBuilder.INSTANCE.getBlockStream().toArray(Block[]::new)));
    public static final RegistryReference<MenuType<ModCraftingMenu>> CRAFTING_MENU_TYPE = REGISTRY.registerMenuTypeSupplier("crafting", () -> ModCraftingMenu::new);

    public static void touch() {

    }
}
