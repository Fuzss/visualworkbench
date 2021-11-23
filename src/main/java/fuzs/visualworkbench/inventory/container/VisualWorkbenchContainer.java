package fuzs.visualworkbench.inventory.container;

import fuzs.puzzleslib.util.PuzzlesUtil;
import fuzs.visualworkbench.element.VisualWorkbenchElement;
import fuzs.visualworkbench.inventory.CraftingInventoryWrapper;
import fuzs.visualworkbench.mixin.accessor.WorkbenchContainerAccessor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
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
    private final IWorldPosCallable access;

    public VisualWorkbenchContainer(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, new Inventory(9), IWorldPosCallable.NULL);
    }

    public VisualWorkbenchContainer(int id, PlayerInventory playerInventory, IInventory tileInventory, IWorldPosCallable access) {
        super(id, playerInventory, access);
        this.craftSlots = new CraftingInventoryWrapper(tileInventory, this, 3, 3);
        this.access = access;
        ((WorkbenchContainerAccessor) this).setCraftSlots(this.craftSlots);
        this.slots.set(0, PuzzlesUtil.make(new CraftingResultSlot(playerInventory.player, this.craftSlots, ((WorkbenchContainerAccessor) this).getResultSlots(), 0, 124, 35), slot -> slot.index = 0));
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
}
