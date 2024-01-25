package fuzs.visualworkbench.world.inventory;

import fuzs.visualworkbench.init.ModRegistry;
import fuzs.visualworkbench.world.level.block.entity.CraftingTableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class VisualCraftingMenu extends CraftingMenu implements ContainerListener {
    private final Container container;

    public VisualCraftingMenu(int id, Inventory inventory) {
        super(id, inventory);
        this.container = new SimpleContainer();
    }

    public VisualCraftingMenu(int id, Inventory inventory, CraftingTableBlockEntity blockEntity, ContainerLevelAccess access) {
        super(id, inventory, access);
        this.container = blockEntity;
        this.craftSlots = new TransientCraftingContainer(this, 3, 3, blockEntity.getItems());
        this.resultSlots = new ResultContainer();
//        this.resultSlots = new VisualResultContainer(blockEntity);
        this.setCraftingSlots(inventory.player);
        this.addSlotListener(this);
    }

    private void setCraftingSlots(Player player) {
//        for (int i = 0; i < 10; i++) {
//            Slot slot = this.getSlot(i);
//            if (slot instanceof ResultSlot resultSlot) {
//                resultSlot.craftSlots = this.craftSlots;
//                slot.container = this.resultSlots;
//            } else if (slot.container instanceof TransientCraftingContainer) {
//                slot.container = this.craftSlots;
//            }
//        }


        // TODO don't replace slots, instead change containers
        this.setSlot(0, new ResultSlot(player, this.craftSlots, this.resultSlots, 0, 124, 35) {

            @Override
            public void setChanged() {
                // Update block entity.
                this.container.setChanged();
            }
        });
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                int slotIndex = j + i * 3;
                this.setSlot(slotIndex + 1, new Slot(this.craftSlots, slotIndex, 30 + j * 18, 17 + i * 18));
            }
        }
    }

    public Slot setSlot(int index, Slot slot) {
        slot.index = index;
        this.slots.set(index, slot);
        return slot;
    }

    @Override
    public MenuType<?> getType() {
        return ModRegistry.CRAFTING_MENU_TYPE.value();
    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);
        this.access.execute((Level level, BlockPos blockPos) -> {
            // Update block entity.
            this.container.setChanged();
        });
    }

    @Override
    public void slotChanged(AbstractContainerMenu containerMenu, int dataSlotIndex, ItemStack itemStack) {
        if (dataSlotIndex >= 1 && dataSlotIndex < 10) {
            this.slotsChanged(this.craftSlots);
        }
    }

    @Override
    public void dataChanged(AbstractContainerMenu containerMenu, int dataSlotIndex, int value) {
        // NO-OP
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        // Prevent items from being cleared out of the container.
        ContainerLevelAccess containerLevelAccess = this.access;
        this.access = ContainerLevelAccess.NULL;
        super.removed(player);
        this.access = containerLevelAccess;
    }
}
