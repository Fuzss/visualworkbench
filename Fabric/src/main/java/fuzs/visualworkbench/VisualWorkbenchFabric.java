package fuzs.visualworkbench;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.visualworkbench.config.JsonConfigBuilder;
import fuzs.visualworkbench.init.ModRegistry;
import fuzs.visualworkbench.mixin.accessor.BlockEntityTypeFabricAccessor;
import fuzs.visualworkbench.world.level.block.entity.CraftingTableBlockEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Set;

public class VisualWorkbenchFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ModConstructor.construct(VisualWorkbench.MOD_ID, VisualWorkbench::new);
        registerHandlers();
    }

    private static void registerHandlers() {
        // workaround for mod compat on Fabric: mod loading is done sequentially, everything registered before us is already added to the crafting table block entity as normal
        // everything registered afterwards runs through this callback and is hacked into the valid blocks list of our crafting table block entity
        RegistryEntryAddedCallback.event(BuiltInRegistries.BLOCK).register((int rawId, ResourceLocation id, Block object) -> {
            if (JsonConfigBuilder.INSTANCE.isValidCraftingTable(id, object)) {
                addValidCraftingTableBlock(object);
            }
        });
    }

    private static void addValidCraftingTableBlock(Block object) {
        BlockEntityType<CraftingTableBlockEntity> craftingTable = ModRegistry.CRAFTING_TABLE_BLOCK_ENTITY.get();
        Set<Block> validBlocks = ((BlockEntityTypeFabricAccessor) craftingTable).visualworkbench$getValidBlocks();
        // vanilla makes an immutable set out of the valid blocks list, so we just replace it with a mutable one for future additions
        if (validBlocks instanceof ImmutableSet<Block>) {
            validBlocks = Sets.newHashSet(validBlocks);
            ((BlockEntityTypeFabricAccessor) craftingTable).visualworkbench$setValidBlocks(validBlocks);
        }
        validBlocks.add(object);
        JsonConfigBuilder.INSTANCE.clearCache();
    }
}
