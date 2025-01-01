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
    private final Container blockEntity;

    public VisualCraftingMenu(int id, Inventory inventory) {
        super(id, inventory);
        this.blockEntity = new SimpleContainer();
    }

    public VisualCraftingMenu(int id, Inventory inventory, CraftingTableBlockEntity blockEntity, ContainerLevelAccess access) {
        super(id, inventory, access);
        ((TransientCraftingContainer) this.craftSlots).items = blockEntity.getItems();
        this.resultSlots.itemStacks = blockEntity.getResultItems();
        this.blockEntity = blockEntity;
        this.addSlotListener(this);
        // always update recipe output when opening menu, otherwise could be missing or outdated if the block entity didn't save it correctly
        this.slotsChanged(this.craftSlots);
    }

    @Override
    public MenuType<?> getType() {
        return ModRegistry.CRAFTING_MENU_TYPE.value();
    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);
        if (container == this.craftSlots || container == this.resultSlots) {
            this.access.execute((Level level, BlockPos blockPos) -> {
                this.blockEntity.setChanged();
            });
        }
    }

    @Override
    public void removed(Player player) {
        // prevent items from being cleared out of the container
        ContainerLevelAccess containerLevelAccess = this.access;
        this.access = ContainerLevelAccess.NULL;
        super.removed(player);
        this.access = containerLevelAccess;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.access.evaluate((Level level, BlockPos blockPos) -> {
            // craft slots are extended to forward this to the block entity, normally in vanilla this would always return true
            return this.blockEntity.stillValid(player);
        }, true);
    }

    @Override
    public void slotChanged(AbstractContainerMenu containerToSend, int dataSlotIndex, ItemStack itemStack) {
        this.access.execute((Level level, BlockPos blockPos) -> {
            if (dataSlotIndex >= 0 && dataSlotIndex < 10) {
                this.blockEntity.setChanged();
            }
        });
    }

    @Override
    public void dataChanged(AbstractContainerMenu containerMenu, int dataSlotIndex, int value) {
        // NO-OP
    }
}
