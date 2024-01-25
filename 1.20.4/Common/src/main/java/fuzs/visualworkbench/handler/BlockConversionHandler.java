package fuzs.visualworkbench.handler;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import fuzs.puzzleslib.api.block.v1.BlockConversionHelper;
import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import fuzs.puzzleslib.api.init.v3.registry.RegistryHelper;
import fuzs.visualworkbench.VisualWorkbench;
import fuzs.visualworkbench.config.ServerConfig;
import fuzs.visualworkbench.init.ModRegistry;
import fuzs.visualworkbench.world.level.block.CraftingTableWithInventoryBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CraftingTableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class BlockConversionHandler {
    public static final Component INVALID_BLOCK_COMPONENT = Component.translatable("container.invalidBlock");
    public static final BiMap<Block, Block> BLOCK_CONVERSIONS = HashBiMap.create();

    public static void onRegistryEntryAdded(Registry<Block> registry, ResourceLocation id, Block block, BiConsumer<ResourceLocation, Supplier<Block>> registrar) {
        if (block instanceof CraftingTableBlock) {
            ResourceLocation resourceLocation = VisualWorkbench.id(id.getNamespace() + "/" + id.getPath());
            registrar.accept(resourceLocation, () -> {
                Block newBlock = new CraftingTableWithInventoryBlock(block);
                BLOCK_CONVERSIONS.put(block, newBlock);
                return newBlock;
            });
        }
    }

    public static EventResultHolder<InteractionResult> onUseBlock(Player player, Level level, InteractionHand interactionHand, BlockHitResult hitResult) {
        if (!VisualWorkbench.CONFIG.get(ServerConfig.class).disableVanillaWorkbench) return EventResultHolder.pass();
        BlockState blockState = level.getBlockState(hitResult.getBlockPos());
        if (BLOCK_CONVERSIONS.containsKey(blockState.getBlock()) && !blockState.is(ModRegistry.UNALTERED_WORKBENCHES_BLOCK_TAG)) {
            player.displayClientMessage(Component.empty().append(INVALID_BLOCK_COMPONENT).withStyle(ChatFormatting.RED), true);
            return EventResultHolder.interrupt(InteractionResult.sidedSuccess(level.isClientSide));
        } else {
            return EventResultHolder.pass();
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
        if (block instanceof CraftingTableBlock) {
            BlockConversionHelper.setItemForBlock(BLOCK_CONVERSIONS.get(block), blockItem);
        }
    }

    private static void setBlockForItem(BlockItem blockItem, Block block) {
        Block baseBlock;
        Block diagonalBlock = BLOCK_CONVERSIONS.get(block);
        if (diagonalBlock != null) {
            baseBlock = block;
        } else {
            baseBlock = BLOCK_CONVERSIONS.inverse().get(block);
            if (baseBlock != null) {
                diagonalBlock = block;
            } else {
                return;
            }
        }
        if (RegistryHelper.is(ModRegistry.UNALTERED_WORKBENCHES_BLOCK_TAG, baseBlock)) {
            BlockConversionHelper.setBlockForItem(blockItem, baseBlock);
        } else {
            BlockConversionHelper.setBlockForItem(blockItem, diagonalBlock);
        }
    }

    private static void copyBoundTags() {
        BLOCK_CONVERSIONS.forEach(BlockConversionHelper::copyBoundTags);
    }
}
