package fuzs.visualworkbench.world.inventory;

import fuzs.visualworkbench.init.ModRegistry;
import fuzs.visualworkbench.world.level.block.entity.CraftingTableBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;

public class VisualCraftingMenu extends CraftingMenu {

    public VisualCraftingMenu(int id, Inventory inventory) {
        super(id, inventory);
    }

    public VisualCraftingMenu(int id, Inventory inventory, CraftingTableBlockEntity blockEntity, ContainerLevelAccess access) {
        super(id, inventory, access);
        this.craftSlots = new VisualTransientCraftingContainer(this, 3, 3, blockEntity.getItems(), blockEntity);
        this.resultSlots = new VisualResultContainer(blockEntity.getResultItems(), blockEntity);
        this.setCraftingSlotContainer();
    }

    private void setCraftingSlotContainer() {
        for (int i = 0; i < 10; i++) {
            Slot slot = this.getSlot(i);
            if (slot instanceof ResultSlot resultSlot) {
                resultSlot.craftSlots = this.craftSlots;
                slot.container = this.resultSlots;
            } else if (slot.container instanceof TransientCraftingContainer) {
                slot.container = this.craftSlots;
            }
        }
    }

    @Override
    public MenuType<?> getType() {
        return ModRegistry.CRAFTING_MENU_TYPE.value();
    }

    @Override
    public boolean stillValid(Player player) {
        // craft slots are extended to forward this to the block entity, normally in vanilla this would always return true
        return this.craftSlots.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        // prevent items from being cleared out of the container
        ContainerLevelAccess containerLevelAccess = this.access;
        this.access = ContainerLevelAccess.NULL;
        super.removed(player);
        this.access = containerLevelAccess;
    }
}
