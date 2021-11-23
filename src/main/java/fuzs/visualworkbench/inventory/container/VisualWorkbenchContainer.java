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

public class VisualWorkbenchContainer extends WorkbenchContainer implements GlobalContainer {
    private final CraftingInventory craftSlots;
    private final IInventory tileInventory;
    private final IWorldPosCallable access;
    private final PlayerEntity player;

    public VisualWorkbenchContainer(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, new Inventory(9), IWorldPosCallable.NULL);
    }

    public VisualWorkbenchContainer(int id, PlayerInventory playerInventory, IInventory tileInventory, IWorldPosCallable access) {
        super(id, playerInventory, access);
        this.craftSlots = new CraftingInventoryWrapper(tileInventory, this, 3, 3);
        this.tileInventory = tileInventory;
        this.access = access;
        this.player = playerInventory.player;
        ((WorkbenchContainerAccessor) this).setCraftSlots(this.craftSlots);
        this.slots.set(0, PuzzlesUtil.make(new CraftingResultSlot(playerInventory.player, this.craftSlots, ((WorkbenchContainerAccessor) this).getResultSlots(), 0, 124, 35) {
            @Override
            public ItemStack onTake(PlayerEntity p_190901_1_, ItemStack p_190901_2_) {
                final ItemStack itemStack = super.onTake(p_190901_1_, p_190901_2_);
                VisualWorkbenchContainer.this.slotsChangedGlobally(VisualWorkbenchContainer.this.craftSlots, access, playerInventory.player);
                return itemStack;
            }
        }, slot -> slot.index = 0));
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                final int slotIndex = j + i * 3;
                this.slots.set(slotIndex + 1, PuzzlesUtil.make(new Slot(this.craftSlots, slotIndex, 30 + j * 18, 17 + i * 18), slot -> slot.index = slotIndex + 1));
            }
        }
        // update result slot if a valid recipe is already present
        this.slotsChangedLocally(this.craftSlots);
    }

    @Override
    public ContainerType<?> getType() {
        return VisualWorkbenchElement.CRAFTING_CONTAINER;
    }

    @Override
    public void slotsChanged(IInventory inventory) {
        this.slotsChangedLocally(inventory);
        this.slotsChangedGlobally(inventory, this.access, this.player);
    }

    @Override
    public boolean isSameMenu(Container other) {
        return other instanceof VisualWorkbenchContainer && ((VisualWorkbenchContainer) other).tileInventory == this.tileInventory;
    }

    @Override
    public void slotsChangedLocally(IInventory inventory) {
        super.slotsChanged(inventory);
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
