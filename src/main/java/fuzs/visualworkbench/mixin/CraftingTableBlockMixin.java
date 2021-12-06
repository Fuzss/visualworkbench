package fuzs.visualworkbench.mixin;

import fuzs.visualworkbench.config.JsonConfigBuilder;
import fuzs.visualworkbench.registry.ModRegistry;
import fuzs.visualworkbench.world.level.block.entity.CraftingTableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CraftingTableBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
@Mixin(CraftingTableBlock.class)
public abstract class CraftingTableBlockMixin extends Block implements EntityBlock {
    public CraftingTableBlockMixin(Properties p_i48440_1_) {
        super(p_i48440_1_);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        if (JsonConfigBuilder.INSTANCE.contains(this)) {
            return new CraftingTableBlockEntity(pPos, pState);
        }
        return null;
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModRegistry.CRAFTING_TABLE_BLOCK_ENTITY.get(), CraftingTableBlockEntity::tick);
    }

    @Nullable
    private static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> checkType(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
        return expectedType == givenType ? (BlockEntityTicker<A>) ticker : null;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (world.getBlockEntity(pos) instanceof CraftingTableBlockEntity blockEntity) {
            if (stack.hasCustomHoverName()) {
                blockEntity.setCustomName(stack.getHoverName());
            }
        } else {
            super.setPlacedBy(world, pos, state, placer, stack);
        }
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            if (world.getBlockEntity(pos) instanceof CraftingTableBlockEntity blockEntity) {
                Containers.dropContents(world, pos, blockEntity);
            }
        }
        super.onRemove(state, world, pos, newState, isMoving);
    }

    @Override
    public boolean triggerEvent(BlockState state, Level world, BlockPos pos, int id, int param) {
        final boolean result = super.triggerEvent(state, world, pos, id, param);
        if (world.getBlockEntity(pos) instanceof CraftingTableBlockEntity blockEntity) {
            return blockEntity.triggerEvent(id, param);
        }
        return result;
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> callbackInfo) {
        if (world.getBlockEntity(pos) instanceof CraftingTableBlockEntity blockEntity) {
            if (world.isClientSide) {
                callbackInfo.setReturnValue(InteractionResult.SUCCESS);
            } else {
                player.openMenu(blockEntity);
                player.awardStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
                callbackInfo.setReturnValue(InteractionResult.CONSUME);
            }
        }
    }

    @Inject(method = "getMenuProvider", at = @At("HEAD"), cancellable = true)
    public void getMenuProvider(BlockState state, Level world, BlockPos pos, CallbackInfoReturnable<MenuProvider> callbackInfo) {
        if (world.getBlockEntity(pos) instanceof CraftingTableBlockEntity blockEntity) {
            callbackInfo.setReturnValue(blockEntity);
        }
    }
}
