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
    public int combinedLight;
    public int ticks;
    public float currentAngle;
    public float nextAngle;
    private byte sector;
    private boolean animating;
    private float animationAngleStart;
    private float animationAngleEnd;
    private double startTicks;
    private double playerAngle;

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
    public void onDataPacket(NetworkManager networkManager, SUpdateTileEntityPacket updatePacket) {
        CompoundNBT compound = updatePacket.getTag();
        this.clearContent();
        ItemStackHelper.loadAllItems(compound, this.items);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (this.level != null) {
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
        ItemStack itemStack = ItemStackHelper.removeItem(this.items, index, count);
        if (!itemStack.isEmpty()) {
            // vanilla is fine, but crafting tweaks mod doesn't update the client properly without this
            this.setChanged();
        }
        return itemStack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        ItemStack itemStack = ItemStackHelper.takeItem(this.items, index);
        if (!itemStack.isEmpty()) {
            // vanilla is fine, but crafting tweaks mod doesn't update the client properly without this
            this.setChanged();
        }
        return itemStack;
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        if (index >= 0 && index < this.items.size()) {
            this.items.set(index, stack);
            // vanilla is fine, but crafting tweaks mod doesn't update the client properly without this
            this.setChanged();
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
        if (this.level == null || !this.level.isClientSide || this.isEmpty()) {
            return;
        }
        // renderer checks inside this block where lighting will be 0, so we instead check above
        this.combinedLight = this.getLevel() != null ? getLightColor(this.getLevel(), this.getBlockPos().above()) : 15728880;
        if (this.combinedLight == 0) {
            // don't render when a block is above the workbench
            return;
        }
        ++this.ticks;
        PlayerEntity playerentity = this.level.getNearestPlayer((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D, 3.0D, false);
        if (playerentity != null) {
            double d0 = playerentity.getX() - ((double)this.worldPosition.getX() + 0.5D);
            double d1 = playerentity.getZ() - ((double)this.worldPosition.getZ() + 0.5D);
            this.playerAngle = (Math.atan2(-d0, -d1) + 3.9269908169872414D) % 6.283185307179586D;
        }
        // most of this code is taken from the old RealBench mod (https://www.curseforge.com/minecraft/mc-mods/realbench)
        byte sector = (byte) ((int) (this.playerAngle * 2.0 / Math.PI));
        if (this.sector != sector) {
            this.animating = true;
            this.animationAngleStart = this.currentAngle;
            float delta1 = (float) sector * 90.0F - this.currentAngle;
            float abs1 = Math.abs(delta1);
            float delta2 = delta1 + 360.0F;
            float shift = Math.abs(delta2);
            float delta3 = delta1 - 360.0F;
            float abs3 = Math.abs(delta3);
            if (abs3 < abs1 && abs3 < shift) {
                this.animationAngleEnd = delta3 + this.currentAngle;
            } else if (shift < abs1 && shift < abs3) {
                this.animationAngleEnd = delta2 + this.currentAngle;
            } else {
                this.animationAngleEnd = delta1 + this.currentAngle;
            }
            this.startTicks = this.ticks;
            this.sector = sector;
        }
        if (this.animating) {
            if (this.ticks >= this.startTicks + 20) {
                this.animating = false;
                this.currentAngle = this.nextAngle = (this.animationAngleEnd + 360.0F) % 360.0F;
            } else {
                this.currentAngle = (easeOutQuad(this.ticks - this.startTicks, this.animationAngleStart, this.animationAngleEnd - this.animationAngleStart, 20.0) + 360.0F) % 360.0F;
                this.nextAngle = (easeOutQuad(Math.min(this.ticks + 1 - this.startTicks, 20), this.animationAngleStart, this.animationAngleEnd - this.animationAngleStart, 20.0) + 360.0F) % 360.0F;
                if (this.currentAngle != 0.0F || this.nextAngle != 0.0F) {
                    if (this.currentAngle == 0.0F && this.nextAngle >= 180.0F) {
                        this.currentAngle = 360.0F;
                    }
                    if (this.nextAngle == 0.0F && this.currentAngle >= 180.0F) {
                        this.nextAngle = 360.0F;
                    }
                }
            }
        }
    }

    private static float easeOutQuad(double t, float b, float c, double d) {
        float z = (float) t / (float) d;
        return -c * z * (z - 2.0F) + b;
    }

    /**
     * from {@link net.minecraft.client.renderer.WorldRenderer#getLightColor}
     */
    public static int getLightColor(IBlockDisplayReader displayReader, BlockPos pos) {
        return getLightColor(displayReader, displayReader.getBlockState(pos), pos);
    }

    /**
     * from {@link net.minecraft.client.renderer.WorldRenderer#getLightColor}
     */
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
