package fuzs.visualworkbench.mixin;

import fuzs.visualworkbench.VisualWorkbench;
import fuzs.visualworkbench.registry.ModRegistry;
import fuzs.visualworkbench.world.level.block.CraftingBlockEntityProvider;
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
public abstract class CraftingTableBlockMixin extends Block implements EntityBlock, CraftingBlockEntityProvider {
    public CraftingTableBlockMixin(Properties p_i48440_1_) {
        super(p_i48440_1_);
    }

    @Override
    public boolean withCraftingBlockEntity() {
        return !VisualWorkbench.CONFIG.server().workbenchBlacklist.contains(this) && !ModRegistry.NON_VISUAL_WORKBENCHES_TAG.contains(this);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        if (this.withCraftingBlockEntity()) {
            VisualWorkbench.LOGGER.info("config {}", VisualWorkbench.CONFIG.server().workbenchBlacklist.contains(this));
            VisualWorkbench.LOGGER.info("tag {}", ModRegistry.NON_VISUAL_WORKBENCHES_TAG.contains(this));
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
        if (!this.withCraftingBlockEntity()) {
            super.setPlacedBy(world, pos, state, placer, stack);
            return;
        }
        if (stack.hasCustomHoverName()) {
            if (world.getBlockEntity(pos) instanceof CraftingTableBlockEntity tileentity) {
                tileentity.setCustomName(stack.getHoverName());
            }
        }
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!this.withCraftingBlockEntity()) {
            super.onRemove(state, world, pos, newState, isMoving);
            return;
        }
        if (!state.is(newState.getBlock())) {
            if (world.getBlockEntity(pos) instanceof CraftingTableBlockEntity tileentity) {
                Containers.dropContents(world, pos, tileentity);
            }
            super.onRemove(state, world, pos, newState, isMoving);
        }
    }

    @Override
    public boolean triggerEvent(BlockState state, Level world, BlockPos pos, int id, int param) {
        if (!this.withCraftingBlockEntity()) {
            return super.triggerEvent(state, world, pos, id, param);
        }
        super.triggerEvent(state, world, pos, id, param);
        return world.getBlockEntity(pos) instanceof CraftingTableBlockEntity tileentity && tileentity.triggerEvent(id, param);
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> callbackInfo) {
        if (this.withCraftingBlockEntity()) {
            if (world.isClientSide) {
                callbackInfo.setReturnValue(InteractionResult.SUCCESS);
            } else {
                if (world.getBlockEntity(pos) instanceof CraftingTableBlockEntity tileentity) {
                    player.openMenu(tileentity);
                    player.awardStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
                }
                callbackInfo.setReturnValue(InteractionResult.CONSUME);
            }
        }
    }

    @Inject(method = "getMenuProvider", at = @At("HEAD"), cancellable = true)
    public void getMenuProvider(BlockState state, Level world, BlockPos pos, CallbackInfoReturnable<MenuProvider> callbackInfo) {
        if (this.withCraftingBlockEntity()) {
            if (world.getBlockEntity(pos) instanceof CraftingTableBlockEntity tileentity) {
                callbackInfo.setReturnValue(tileentity);
            }
        }
    }
}
