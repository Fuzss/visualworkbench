package fuzs.visualworkbench.tileentity;

import fuzs.visualworkbench.element.VisualWorkbenchElement;
import fuzs.visualworkbench.inventory.container.VisualWorkbenchContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.LightType;

import javax.annotation.Nullable;

public class WorkbenchTileEntity extends LockableTileEntity implements ITickableTileEntity {

    private final NonNullList<ItemStack> items = NonNullList.withSize(9, ItemStack.EMPTY);

    public int ticks;
    public double playerAngle;
    public int combinedLight;

    @SuppressWarnings("ConstantConditions")
    public WorkbenchTileEntity() {

        super(VisualWorkbenchElement.WORKBENCH_TILE_ENTITY);
    }

    @Override
    protected ITextComponent getDefaultName() {

        return new TranslationTextComponent("container.crafting");
    }

    @Override
    public void load(BlockState state, CompoundNBT compound) {

        super.load(state, compound);
        this.clearContent();
        ItemStackHelper.loadAllItems(compound, this.items);
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {

        return this.saveMetadataAndItems(compound);
    }

    private CompoundNBT saveMetadataAndItems(CompoundNBT compound) {

        super.save(compound);
        ItemStackHelper.saveAllItems(compound, this.items, true);
        return compound;
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {

        return new SUpdateTileEntityPacket(this.worldPosition, -1, this.getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {

        return this.saveMetadataAndItems(new CompoundNBT());
    }

    @Override
    public void onDataPacket(NetworkManager networkManager, SUpdateTileEntityPacket updatePacket){

        CompoundNBT compound = updatePacket.getTag();
        this.clearContent();
        ItemStackHelper.loadAllItems(compound, this.items);
    }

    @Override
    public void setChanged() {

        super.setChanged();
        if (this.hasLevel()) {

            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    @Override
    public int getContainerSize() {

        return this.items.size();
    }

    @Override
    public boolean isEmpty() {

        for (ItemStack itemstack : this.items) {

            if (!itemstack.isEmpty()) {

                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack getItem(int index) {

        return index >= 0 && index < this.items.size() ? this.items.get(index) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int index, int count) {

        return ItemStackHelper.removeItem(this.items, index, count);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {

        return ItemStackHelper.takeItem(this.items, index);
    }

    @Override
    public void setItem(int index, ItemStack stack) {

        if (index >= 0 && index < this.items.size()) {

            this.items.set(index, stack);
        }
    }

    @Override
    public boolean stillValid(PlayerEntity player) {

        if (this.level.getBlockEntity(this.worldPosition) != this) {

            return false;
        } else {

            return !(player.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) > 64.0D);
        }
    }

    @Override
    protected Container createMenu(int id, PlayerInventory playerInventory) {

        return new VisualWorkbenchContainer(id, playerInventory, this, IWorldPosCallable.create(this.level, this.worldPosition));
    }

    @Override
    public void clearContent() {

        this.items.clear();
    }

    @Override
    public void tick() {

        if (!this.hasLevel() || !this.level.isClientSide) {

            return;
        }

        PlayerEntity playerentity = this.level.getNearestPlayer((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D, 3.0D, false);
        if (playerentity != null) {

            double d0 = playerentity.getX() - ((double)this.worldPosition.getX() + 0.5D);
            double d1 = playerentity.getZ() - ((double)this.worldPosition.getZ() + 0.5D);
            this.playerAngle = (Math.atan2(-d0, -d1) + 3.9269908169872414D) % 6.283185307179586D;
        }

        // renderer checks inside of this block where lighting will be 0, so we instead check above
        this.combinedLight = this.getLevel() != null ? getLightColor(this.getLevel(), this.getBlockPos().above()) : 15728880;
        ++this.ticks;
    }

    public static int getLightColor(IBlockDisplayReader displayReader, BlockPos pos) {

        return getLightColor(displayReader, displayReader.getBlockState(pos), pos);
    }

    public static int getLightColor(IBlockDisplayReader displayReader, BlockState state, BlockPos pos) {

        if (state.emissiveRendering(displayReader, pos)) {

            return 15728880;
        } else {

            int i = displayReader.getBrightness(LightType.SKY, pos);
            int j = displayReader.getBrightness(LightType.BLOCK, pos);
            int k = state.getLightValue(displayReader, pos);
            if (j < k) {
                j = k;
            }

            return i << 20 | j << 4;
        }
    }

}
