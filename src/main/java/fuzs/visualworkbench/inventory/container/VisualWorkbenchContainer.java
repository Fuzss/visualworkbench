package fuzs.visualworkbench.inventory.container;

import fuzs.visualworkbench.element.VisualWorkbenchElement;
import fuzs.visualworkbench.inventory.CraftingInventoryWrapper;
import fuzs.visualworkbench.mixin.accessor.IWorkbenchContainerAccessor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.inventory.container.Slot;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;

public class VisualWorkbenchContainer extends WorkbenchContainer {

    public VisualWorkbenchContainer(int id, PlayerInventory playerInventory) {

        super(id, playerInventory);
    }

    public VisualWorkbenchContainer(int id, PlayerInventory playerInventory, IInventory tileEntity, IWorldPosCallable access) {

        super(id, playerInventory, access);

        // clearing lastSlots as well is not necessary, the slots that are added now will simply remain unused
        this.slots.clear();
        CraftingInventory craftingInventory = new CraftingInventoryWrapper(tileEntity, this, 3, 3);
        this.addSlot(new CraftingResultSlot(playerInventory.player, craftingInventory, ((IWorkbenchContainerAccessor) this).getResultSlots(), 0, 124, 35));

        for (int i = 0; i < 3; ++i) {

            for (int j = 0; j < 3; ++j) {

                this.addSlot(new Slot(craftingInventory, j + i * 3, 30 + j * 18, 17 + i * 18));
            }
        }

        for (int k = 0; k < 3; ++k) {

            for (int i1 = 0; i1 < 9; ++i1) {

                this.addSlot(new Slot(playerInventory, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
            }
        }

        for (int l = 0; l < 9; ++l) {

            this.addSlot(new Slot(playerInventory, l, 8 + l * 18, 142));
        }

        ((IWorkbenchContainerAccessor) this).setCraftSlots(craftingInventory);
        // update result slot if a valid recipe is already present
        this.slotsChanged(craftingInventory);
    }

    @Override
    public ContainerType<?> getType() {

        return VisualWorkbenchElement.CRAFTING_CONTAINER;
    }

    @Override
    public boolean stillValid(PlayerEntity player) {

        return ((IWorkbenchContainerAccessor) this).getCraftSlots().stillValid(player);
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
