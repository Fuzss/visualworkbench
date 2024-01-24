package fuzs.visualworkbench.handler;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import fuzs.puzzleslib.api.block.v1.BlockConversionHelper;
import fuzs.puzzleslib.api.init.v3.RegistryHelper;
import fuzs.visualworkbench.VisualWorkbench;
import fuzs.visualworkbench.init.ModRegistry;
import fuzs.visualworkbench.world.level.block.VisualCraftingTableBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CraftingTableBlock;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class OpenMenuHandler {
    public static final BiMap<Block, Block> BLOCK_CONVERSIONS = HashBiMap.create();

    public static void onRegistryEntryAdded(Registry<Block> registry, ResourceLocation id, Block entry, BiConsumer<ResourceLocation, Supplier<Block>> registrar) {
        if (entry instanceof CraftingTableBlock) {
            ResourceLocation resourceLocation = VisualWorkbench.id(id.getNamespace() + "/" + id.getPath());
            registrar.accept(resourceLocation, () -> {
                BLOCK_CONVERSIONS.put(entry, new VisualCraftingTableBlock(entry));
                return BLOCK_CONVERSIONS.get(entry);
            });
        }
    }

    public static void onTagsUpdated(RegistryAccess registryAccess, boolean client) {
        for (Map.Entry<ResourceKey<Item>, Item> entry : BuiltInRegistries.ITEM.entrySet()) {
            if (entry.getValue() instanceof BlockItem blockItem) {
                Block block = blockItem.getBlock();
                setItemForBlock(entry.getKey().location(), blockItem, block);
                setBlockForItem(blockItem, block);
            }
        }
        copyBoundTags();
    }

    private static void setItemForBlock(ResourceLocation resourceLocation, BlockItem blockItem, Block block) {
        if (BLOCK_CONVERSIONS.containsKey(block)) {
            BlockConversionHelper.setItemForBlock(BLOCK_CONVERSIONS.get(block), blockItem);
        }
    }

    private static void setBlockForItem(BlockItem blockItem, Block block) {
        BiMap<Block, Block> conversions = BLOCK_CONVERSIONS;
        Block baseBlock;
        Block diagonalBlock = conversions.get(block);
        if (diagonalBlock != null) {
            baseBlock = block;
        } else {
            baseBlock = conversions.inverse().get(block);
            if (baseBlock != null) {
                diagonalBlock = block;
            } else {
                return;
            }
        }
        if (RegistryHelper.is(ModRegistry.NON_VISUAL_WORKBENCHES_BLOCK_TAG, baseBlock)) {
            BlockConversionHelper.setBlockForItem(blockItem, baseBlock);
        } else {
            BlockConversionHelper.setBlockForItem(blockItem, diagonalBlock);
        }
    }

    private static void copyBoundTags() {
        BLOCK_CONVERSIONS.forEach(BlockConversionHelper::copyBoundTags);
    }
}
