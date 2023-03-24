package fuzs.visualworkbench.mixin;

import fuzs.visualworkbench.config.JsonConfigBuilder;
import fuzs.visualworkbench.init.ModRegistry;
import fuzs.visualworkbench.world.level.block.VisualCraftingTableBlock;
import fuzs.visualworkbench.world.level.block.entity.CraftingTableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CraftingTableBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("deprecation")
@Mixin(CraftingTableBlock.class)
abstract class CraftingTableBlockMixin extends Block implements EntityBlock, VisualCraftingTableBlock {

    public CraftingTableBlockMixin(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return this.hasBlockEntity() ? new CraftingTableBlockEntity(pos, state) : null;
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? checkType(type, ModRegistry.CRAFTING_TABLE_BLOCK_ENTITY.get(), CraftingTableBlockEntity::clientTick) : null;
    }

    @Nullable
    private static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> checkType(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
        return expectedType == givenType ? (BlockEntityTicker<A>) ticker : null;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (this.hasBlockEntity() && world.getBlockEntity(pos) instanceof CraftingTableBlockEntity blockEntity) {
            if (stack.hasCustomHoverName()) {
                blockEntity.setCustomName(stack.getHoverName());
            }
        } else {
            super.setPlacedBy(world, pos, state, placer, stack);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            if (this.hasBlockEntity() && level.getBlockEntity(pos) instanceof CraftingTableBlockEntity blockEntity) {
                Containers.dropContents(level, pos, blockEntity);
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
        final boolean result = super.triggerEvent(state, level, pos, id, param);
        if (this.hasBlockEntity() && level.getBlockEntity(pos) instanceof CraftingTableBlockEntity blockEntity) {
            return blockEntity.triggerEvent(id, param);
        }
        return result;
    }

    @Override
    public boolean hasBlockEntity() {
        return JsonConfigBuilder.INSTANCE.contains(this);
    }

    @Inject(method = "getMenuProvider", at = @At("HEAD"), cancellable = true)
    public void getMenuProvider(BlockState state, Level level, BlockPos pos, CallbackInfoReturnable<MenuProvider> callbackInfo) {
        if (this.hasBlockEntity() && level.getBlockEntity(pos) instanceof CraftingTableBlockEntity blockEntity) {
            callbackInfo.setReturnValue(blockEntity);
        }
    }
}
