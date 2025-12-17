package fuzs.visualworkbench;

import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.event.v1.AddBlockEntityTypeBlocksCallback;
import fuzs.puzzleslib.api.event.v1.RegistryEntryAddedCallback;
import fuzs.puzzleslib.api.event.v1.core.EventPhase;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerInteractEvents;
import fuzs.puzzleslib.api.event.v1.server.TagsUpdatedCallback;
import fuzs.visualworkbench.config.ClientConfig;
import fuzs.visualworkbench.config.ServerConfig;
import fuzs.visualworkbench.handler.BlockConversionHandler;
import fuzs.visualworkbench.init.ModRegistry;
import fuzs.visualworkbench.world.level.block.CraftingTableWithInventoryBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CraftingTableBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Predicate;

public class VisualWorkbench implements ModConstructor {
    public static final String MOD_ID = "visualworkbench";
    public static final String MOD_NAME = "Visual Workbench";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static final ConfigHolder CONFIG = ConfigHolder.builder(MOD_ID)
            .client(ClientConfig.class)
            .server(ServerConfig.class);
    public static final Predicate<Block> BLOCK_PREDICATE = (Block block) -> {
        return block instanceof CraftingTableBlock && !(block instanceof CraftingTableWithInventoryBlock);
    };

    @Override
    public void onConstructMod() {
        ModRegistry.bootstrap();
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        RegistryEntryAddedCallback.registryEntryAdded(Registries.BLOCK)
                .register(BlockConversionHandler.onRegistryEntryAdded(BLOCK_PREDICATE,
                        CraftingTableWithInventoryBlock::new,
                        MOD_ID));
        AddBlockEntityTypeBlocksCallback.EVENT.register(BlockConversionHandler.onAddBlockEntityTypeBlocks(ModRegistry.CRAFTING_TABLE_BLOCK_ENTITY_TYPE));
        PlayerInteractEvents.USE_BLOCK.register(BlockConversionHandler.onUseBlock(ModRegistry.UNALTERED_WORKBENCHES_BLOCK_TAG,
                null,
                () -> CONFIG.get(ServerConfig.class).convertVanillaWorkbenchWhenInteracting));
        TagsUpdatedCallback.EVENT.register(EventPhase.FIRST,
                BlockConversionHandler.onTagsUpdated(ModRegistry.UNALTERED_WORKBENCHES_BLOCK_TAG, BLOCK_PREDICATE));
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
