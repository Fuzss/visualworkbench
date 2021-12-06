package fuzs.visualworkbench.registry;

import fuzs.puzzleslib.registry.RegistryManager;
import fuzs.visualworkbench.VisualWorkbench;
import fuzs.visualworkbench.config.JsonConfigBuilder;
import fuzs.visualworkbench.world.inventory.ModCraftingMenu;
import fuzs.visualworkbench.world.level.block.entity.CraftingTableBlockEntity;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fmllegacy.RegistryObject;

public class ModRegistry {
    private static final RegistryManager REGISTRY = RegistryManager.of(VisualWorkbench.MOD_ID);
    public static final RegistryObject<BlockEntityType<CraftingTableBlockEntity>> CRAFTING_TABLE_BLOCK_ENTITY = REGISTRY.registerRawBlockEntityType("crafting_table", () -> BlockEntityType.Builder.of(CraftingTableBlockEntity::new, JsonConfigBuilder.INSTANCE.getBlockStream().toArray(Block[]::new)));
    public static final RegistryObject<MenuType<ModCraftingMenu>> CRAFTING_MENU_TYPE = REGISTRY.registerRawMenuType("crafting", () -> ModCraftingMenu::new);

    public static void touch() {

    }
}
