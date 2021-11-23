package fuzs.visualworkbench.inventory.container;

import fuzs.puzzleslib.util.PuzzlesUtil;
import fuzs.visualworkbench.element.VisualWorkbenchElement;
import fuzs.visualworkbench.inventory.CraftingInventoryWrapper;
import fuzs.visualworkbench.mixin.accessor.WorkbenchContainerAccessor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class VisualWorkbenchContainer extends WorkbenchContainer implements IContainerListener {
    private final CraftingInventory craftSlots;
    private final CraftResultInventory resultSlots;
    private final IWorldPosCallable access;
    private final PlayerEntity player;

    public VisualWorkbenchContainer(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, new Inventory(9), IWorldPosCallable.NULL);
    }

    public VisualWorkbenchContainer(int id, PlayerInventory playerInventory, IInventory tileInventory, IWorldPosCallable access) {
        super(id, playerInventory, access);
        this.craftSlots = new CraftingInventoryWrapper(tileInventory, this, 3, 3);
        this.resultSlots = ((WorkbenchContainerAccessor) this).getResultSlots();
        this.access = access;
        this.player = playerInventory.player;
        ((WorkbenchContainerAccessor) this).setCraftSlots(this.craftSlots);
        this.slots.set(0, PuzzlesUtil.make(new CraftingResultSlot(playerInventory.player, this.craftSlots, this.resultSlots, 0, 124, 35), slot -> slot.index = 0));
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                final int slotIndex = j + i * 3;
                this.slots.set(slotIndex + 1, PuzzlesUtil.make(new Slot(this.craftSlots, slotIndex, 30 + j * 18, 17 + i * 18), slot -> slot.index = slotIndex + 1));
            }
        }
        this.addSlotListener(this);
    }

    @Override
    public ContainerType<?> getType() {
        return VisualWorkbenchElement.CRAFTING_CONTAINER;
    }

    /**
     * copied from vanilla super to avoid FastWorkbench mixin which would lead to a ClassCastException for craftSlots
     */
    @Override
    public void slotsChanged(IInventory p_75130_1_) {
        this.access.execute((p_217069_1_, p_217069_2_) -> {
            slotChangedCraftingGrid(this.containerId, p_217069_1_, this.player, this.craftSlots, this.resultSlots);
        });
    }

    @Override
    public void refreshContainer(Container p_71110_1_, NonNullList<ItemStack> p_71110_2_) {

    }

    @Override
    public void slotChanged(Container p_71111_1_, int p_71111_2_, ItemStack p_71111_3_) {
        if (p_71111_1_ == this) {
            this.access.execute((World level, BlockPos blockPos) -> {
                if (p_71111_2_ >= 1 && p_71111_2_ < 10) {
                    this.slotsChanged(this.craftSlots);
                }
            });
        }
    }

    @Override
    public void setContainerData(Container p_71112_1_, int p_71112_2_, int p_71112_3_) {

    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return this.craftSlots.stillValid(player);
    }

    @Override
    public void removed(PlayerEntity player) {
        // copied from container base class
        PlayerInventory playerinventory = player.inventory;
        if (!playerinventory.getCarried().isEmpty()) {
            player.drop(playerinventory.getCarried(), false);
            playerinventory.setCarried(ItemStack.EMPTY);
        }
    }

    /**
     * copied from vanilla super to avoid FastWorkbench mixin which would lead to a ClassCastException for craftSlots
     */
    @Override
    public ItemStack quickMoveStack(PlayerEntity p_82846_1_, int p_82846_2_) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(p_82846_2_);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (p_82846_2_ == 0) {
                this.access.execute((p_217067_2_, p_217067_3_) -> {
                    itemstack1.getItem().onCraftedBy(itemstack1, p_217067_2_, p_82846_1_);
                });
                if (!this.moveItemStackTo(itemstack1, 10, 46, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
            } else if (p_82846_2_ >= 10 && p_82846_2_ < 46) {
                if (!this.moveItemStackTo(itemstack1, 1, 10, false)) {
                    if (p_82846_2_ < 37) {
                        if (!this.moveItemStackTo(itemstack1, 37, 46, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (!this.moveItemStackTo(itemstack1, 10, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.moveItemStackTo(itemstack1, 10, 46, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            ItemStack itemstack2 = slot.onTake(p_82846_1_, itemstack1);
            if (p_82846_2_ == 0) {
                p_82846_1_.drop(itemstack2, false);
            }
        }

        return itemstack;
    }
}
