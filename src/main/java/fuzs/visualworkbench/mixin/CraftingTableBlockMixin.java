package fuzs.visualworkbench.mixin;

import fuzs.visualworkbench.tileentity.WorkbenchTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings({"deprecation", "unused"})
@Mixin(CraftingTableBlock.class)
public abstract class CraftingTableBlockMixin extends Block implements ITileEntityProvider {

    public CraftingTableBlockMixin(Properties p_i48440_1_) {

        super(p_i48440_1_);
    }

    @Override
    public TileEntity newBlockEntity(IBlockReader world) {

        return new WorkbenchTileEntity();
    }

    @Override
    public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {

        if (stack.hasCustomHoverName()) {

            TileEntity tileentity = world.getBlockEntity(pos);
            if (tileentity instanceof WorkbenchTileEntity) {

                ((WorkbenchTileEntity) tileentity).setCustomName(stack.getHoverName());
            }
        }
    }

    @Override
    public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {

        if (!state.is(newState.getBlock())) {

            TileEntity tileentity = world.getBlockEntity(pos);
            if (tileentity instanceof WorkbenchTileEntity) {

                InventoryHelper.dropContents(world, pos, (WorkbenchTileEntity) tileentity);
            }

            super.onRemove(state, world, pos, newState, isMoving);
        }
    }

    @Override
    public boolean triggerEvent(BlockState state, World world, BlockPos pos, int id, int param) {

        super.triggerEvent(state, world, pos, id, param);
        TileEntity tileentity = world.getBlockEntity(pos);
        return tileentity != null && tileentity.triggerEvent(id, param);
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit, CallbackInfoReturnable<ActionResultType> callbackInfo) {

        if (world.isClientSide) {

            callbackInfo.setReturnValue(ActionResultType.SUCCESS);
        } else {

            TileEntity tileentity = world.getBlockEntity(pos);
            if (tileentity instanceof WorkbenchTileEntity) {

                player.openMenu((WorkbenchTileEntity) tileentity);
                player.awardStat(Stats.INTERACT_WITH_BREWINGSTAND);
            }

            callbackInfo.setReturnValue(ActionResultType.CONSUME);
        }
    }

    @Inject(method = "getMenuProvider", at = @At("HEAD"), cancellable = true)
    public void getMenuProvider(BlockState state, World world, BlockPos pos, CallbackInfoReturnable<INamedContainerProvider> callbackInfo) {

        TileEntity tileentity = world.getBlockEntity(pos);
        callbackInfo.setReturnValue(tileentity instanceof INamedContainerProvider ? (INamedContainerProvider) tileentity : null);
    }

}
