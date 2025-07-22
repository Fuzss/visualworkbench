package fuzs.visualworkbench.world.level.block.entity;

import fuzs.visualworkbench.VisualWorkbench;
import fuzs.visualworkbench.config.ClientConfig;
import fuzs.visualworkbench.init.ModRegistry;
import fuzs.visualworkbench.util.MathHelper;
import fuzs.visualworkbench.world.inventory.ModCraftingMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CraftingTableBlockEntity extends BaseContainerBlockEntity {
    private static final String TAG_LAST_RECIPE = "LastRecipe";

    private final NonNullList<ItemStack> inventory = NonNullList.withSize(9, ItemStack.EMPTY);
    private ItemStack lastResult = ItemStack.EMPTY;
    public int ticks;
    public float currentAngle;
    public float nextAngle;
    private int sector;
    private boolean animating;
    private float animationAngleStart;
    private float animationAngleEnd;
    private double startTicks;
    private double playerAngle;

    public CraftingTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModRegistry.CRAFTING_TABLE_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.crafting");
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.inventory.clear();
        ContainerHelper.loadAllItems(tag, this.inventory);
        if (tag.contains(TAG_LAST_RECIPE)) {
            this.lastResult = ItemStack.of(tag.getCompound(TAG_LAST_RECIPE));
        } else {
            this.lastResult = ItemStack.EMPTY;
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, this.inventory, true);
        if (!this.lastResult.isEmpty()) {
            CompoundTag compoundTag = new CompoundTag();
            this.lastResult.save(compoundTag);
            tag.put(TAG_LAST_RECIPE, compoundTag);
        }
    }

    @Override
    @Nullable
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        ItemStack itemStackInSlot = this.inventory.get(slot);
        if (itemStackInSlot.isEmpty()) {
            return !this.smallerStackExist(stack.getMaxStackSize(), stack, -1);
        } else {
            return !this.smallerStackExist(itemStackInSlot.getCount(), itemStackInSlot, slot);
        }
    }

    /**
     * @see net.minecraft.world.level.block.entity.CrafterBlockEntity#smallerStackExist(int, ItemStack, int)
     */
    private boolean smallerStackExist(int currentSize, ItemStack itemStackInSlot, int slot) {
        for (int i = slot + 1; i < this.getContainerSize(); i++) {
            ItemStack itemStack = this.getItem(i);
            if (!itemStack.isEmpty() && itemStack.getCount() < currentSize && ItemStack.isSameItemSameTags(itemStack,
                    itemStackInSlot)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean canTakeItem(Container target, int slot, ItemStack stack) {
        return false;
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
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : this.inventory) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int index) {
        return index >= 0 && index < this.inventory.size() ? this.inventory.get(index) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        ItemStack itemStack = ContainerHelper.removeItem(this.inventory, index, count);
        if (!itemStack.isEmpty()) {
            // vanilla is fine, but crafting tweaks mod doesn't update the client properly without this
            this.setChanged();
        }
        return itemStack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        ItemStack itemStack = ContainerHelper.takeItem(this.inventory, index);
        if (!itemStack.isEmpty()) {
            // vanilla is fine, but crafting tweaks mod doesn't update the client properly without this
            this.setChanged();
        }
        return itemStack;
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        if (index >= 0 && index < this.inventory.size()) {
            this.inventory.set(index, stack);
            // vanilla is fine, but crafting tweaks mod doesn't update the client properly without this
            this.setChanged();
        }
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.getLevel().getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return !(player.distanceToSqr((double) this.worldPosition.getX() + 0.5D,
                    (double) this.worldPosition.getY() + 0.5D,
                    (double) this.worldPosition.getZ() + 0.5D) > 64.0D);
        }
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory playerInventory) {
        return new ModCraftingMenu(id,
                playerInventory,
                this,
                ContainerLevelAccess.create(this.getLevel(), this.getBlockPos()),
                this::setLastResult);
    }

    @Override
    public void clearContent() {
        this.inventory.clear();
    }

    public ItemStack getLastResult() {
        return this.lastResult;
    }

    private void setLastResult(ItemStack lastResult) {
        this.lastResult = lastResult;
        this.setChanged();
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, CraftingTableBlockEntity blockEntity) {
        ++blockEntity.ticks;
        if (blockEntity.isEmpty() || !VisualWorkbench.CONFIG.get(ClientConfig.class).rotateIngredients) return;
        Player player = level.getNearestPlayer((double) blockEntity.worldPosition.getX() + 0.5D,
                (double) blockEntity.worldPosition.getY() + 0.5D,
                (double) blockEntity.worldPosition.getZ() + 0.5D,
                3.0D,
                false);
        if (player != null) {
            double d0 = player.getX() - ((double) blockEntity.worldPosition.getX() + 0.5D);
            double d1 = player.getZ() - ((double) blockEntity.worldPosition.getZ() + 0.5D);
            blockEntity.playerAngle = (Math.atan2(-d0, -d1) + 3.9269908169872414D) % 6.283185307179586D;
        }
        // most animation code is taken from the old RealBench mod (https://www.curseforge.com/minecraft/mc-mods/realbench)
        int sector = (int) (blockEntity.playerAngle * 2.0 / Math.PI);
        if (blockEntity.sector != sector) {
            blockEntity.animating = true;
            blockEntity.animationAngleStart = blockEntity.currentAngle;
            float delta1 = sector * 90.0F - blockEntity.currentAngle;
            float abs1 = Math.abs(delta1);
            float delta2 = delta1 + 360.0F;
            float shift = Math.abs(delta2);
            float delta3 = delta1 - 360.0F;
            float abs3 = Math.abs(delta3);
            if (abs3 < abs1 && abs3 < shift) {
                blockEntity.animationAngleEnd = delta3 + blockEntity.currentAngle;
            } else if (shift < abs1 && shift < abs3) {
                blockEntity.animationAngleEnd = delta2 + blockEntity.currentAngle;
            } else {
                blockEntity.animationAngleEnd = delta1 + blockEntity.currentAngle;
            }
            blockEntity.startTicks = blockEntity.ticks;
            blockEntity.sector = sector;
        }
        if (blockEntity.animating) {
            if (blockEntity.ticks >= blockEntity.startTicks + 20) {
                blockEntity.animating = false;
                blockEntity.currentAngle = blockEntity.nextAngle = (blockEntity.animationAngleEnd + 360.0F) % 360.0F;
            } else {
                blockEntity.currentAngle = (MathHelper.easeOutQuad(blockEntity.ticks - blockEntity.startTicks,
                        blockEntity.animationAngleStart,
                        blockEntity.animationAngleEnd - blockEntity.animationAngleStart,
                        20.0) + 360.0F) % 360.0F;
                blockEntity.nextAngle =
                        (MathHelper.easeOutQuad(Math.min(blockEntity.ticks + 1 - blockEntity.startTicks, 20),
                                blockEntity.animationAngleStart,
                                blockEntity.animationAngleEnd - blockEntity.animationAngleStart,
                                20.0) + 360.0F) % 360.0F;
                if (blockEntity.currentAngle != 0.0F || blockEntity.nextAngle != 0.0F) {
                    if (blockEntity.currentAngle == 0.0F && blockEntity.nextAngle >= 180.0F) {
                        blockEntity.currentAngle = 360.0F;
                    }
                    if (blockEntity.nextAngle == 0.0F && blockEntity.currentAngle >= 180.0F) {
                        blockEntity.nextAngle = 360.0F;
                    }
                }
            }
        }
    }
}
