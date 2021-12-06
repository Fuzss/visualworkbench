package fuzs.visualworkbench.mixin;

import fuzs.visualworkbench.VisualWorkbench;
import fuzs.visualworkbench.element.VisualWorkbenchElement;
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

import javax.annotation.Nullable;
import java.util.Set;

@SuppressWarnings("deprecation")
@Mixin(CraftingTableBlock.class)
public abstract class CraftingTableBlockMixin extends Block implements ITileEntityProvider {
    public CraftingTableBlockMixin(Properties p_i48440_1_) {
        super(p_i48440_1_);
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader p_196283_1_) {
        final Set<Block> blockValues = ((VisualWorkbenchElement) VisualWorkbench.VISUAL_WORKBENCH).blockValues;
        if (blockValues != null && blockValues.contains(this)) {
            return new WorkbenchTileEntity();
        }
        return null;
    }

    @Override
    public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        TileEntity tileentity = world.getBlockEntity(pos);
        if (tileentity instanceof WorkbenchTileEntity) {
            if (stack.hasCustomHoverName()) {
                ((WorkbenchTileEntity) tileentity).setCustomName(stack.getHoverName());
            }
        } else {
            super.setPlacedBy(world, pos, state, placer, stack);
        }
    }

    @Override
    public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            TileEntity tileentity = world.getBlockEntity(pos);
            if (tileentity instanceof WorkbenchTileEntity) {
                InventoryHelper.dropContents(world, pos, (WorkbenchTileEntity) tileentity);
            }
        }
        super.onRemove(state, world, pos, newState, isMoving);
    }

    @Override
    public boolean triggerEvent(BlockState state, World world, BlockPos pos, int id, int param) {
        final boolean result = super.triggerEvent(state, world, pos, id, param);
        TileEntity tileentity = world.getBlockEntity(pos);
        if (tileentity instanceof WorkbenchTileEntity) {
            return tileentity.triggerEvent(id, param);
        }
        return result;
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit, CallbackInfoReturnable<ActionResultType> callbackInfo) {
        TileEntity tileentity = world.getBlockEntity(pos);
        if (tileentity instanceof WorkbenchTileEntity) {
            if (world.isClientSide) {
                callbackInfo.setReturnValue(ActionResultType.SUCCESS);
            } else {
                player.openMenu((INamedContainerProvider) tileentity);
                player.awardStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
                callbackInfo.setReturnValue(ActionResultType.CONSUME);
            }
        }
    }

    @Inject(method = "getMenuProvider", at = @At("HEAD"), cancellable = true)
    public void getMenuProvider(BlockState state, World world, BlockPos pos, CallbackInfoReturnable<INamedContainerProvider> callbackInfo) {
        TileEntity tileentity = world.getBlockEntity(pos);
        if (tileentity instanceof WorkbenchTileEntity) {
            callbackInfo.setReturnValue((INamedContainerProvider) tileentity);
        }
    }
}
