package fuzs.visualworkbench.world.inventory;

import fuzs.visualworkbench.init.ModRegistry;
import fuzs.visualworkbench.mixin.accessor.CraftingMenuAccessor;
import fuzs.visualworkbench.world.level.block.entity.VisualCraftingTableBlockEntity;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class VisualCraftingMenu extends CraftingMenu implements ContainerListener {
    private final CraftingContainer craftSlots;
    private final ResultContainer resultSlots;
    @Nullable
    private final VisualCraftingTableBlockEntity blockEntity;
    private final ContainerLevelAccess access;

    public VisualCraftingMenu(int id, Inventory inventory) {
        super(id, inventory);
        this.craftSlots = ((CraftingMenuAccessor) this).visualworkbench$getCraftSlots();
        this.resultSlots = ((CraftingMenuAccessor) this).visualworkbench$getResultSlots();
        this.blockEntity = null;
        this.access = ContainerLevelAccess.NULL;
    }

    public VisualCraftingMenu(int id, Inventory inventory, VisualCraftingTableBlockEntity blockEntity, ContainerLevelAccess access) {
        super(id, inventory, access);
        this.craftSlots = blockEntity.getCraftSlots();
        ((CraftingMenuAccessor) this).visualworkbench$setCraftSlots(this.craftSlots);
        this.resultSlots = blockEntity.getResultSlots();
        ((CraftingMenuAccessor) this).visualworkbench$setResultSlots(this.resultSlots);
        this.blockEntity = blockEntity;
        this.access = access;
        this.addSlots(inventory);
        this.addSlotListener(this);
    }

    private void addSlots(Inventory inventory) {
        this.slots.set(0, Util.make(new ResultSlot(inventory.player, this.craftSlots, this.resultSlots, 0, 124, 35) {

            @Override
            public void set(ItemStack stack) {
                // fast workbench makes this do nothing (via mixin), but in our case it is needed to avoid client desync
                this.container.setItem(this.getContainerSlot(), stack);
                this.setChanged();
            }
        }, slot -> slot.index = 0));
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                int slotIndex = j + i * 3;
                this.slots.set(slotIndex + 1, Util.make(new Slot(this.craftSlots, slotIndex, 30 + j * 18, 17 + i * 18), slot -> slot.index = slotIndex + 1));
            }
        }
    }

    @Override
    public MenuType<?> getType() {
        return ModRegistry.CRAFTING_MENU_TYPE.get();
    }

    @Override
    public void slotChanged(AbstractContainerMenu containerMenu, int dataSlotIndex, ItemStack itemStack) {
        if (containerMenu == this) {
            this.access.execute((Level level, BlockPos blockPos) -> {
                if (dataSlotIndex >= 1 && dataSlotIndex < 10) {
                    this.slotsChanged(this.craftSlots);
                }
            });
        }
    }

    @Override
    public void dataChanged(AbstractContainerMenu containerMenu, int dataSlotIndex, int value) {
        
    }

    @Override
    public boolean stillValid(Player player) {
        Objects.requireNonNull(this.blockEntity, "block entity is null");
        return Container.stillValidBlockEntity(this.blockEntity, player);
    }

    @Override
    public void removed(Player player) {
        // copied from container base class
        if (player instanceof ServerPlayer) {
            ItemStack itemstack = this.getCarried();
            if (!itemstack.isEmpty()) {
                if (player.isAlive() && !((ServerPlayer) player).hasDisconnected()) {
                    player.getInventory().placeItemBackInInventory(itemstack);
                } else {
                    player.drop(itemstack, false);
                }
                this.setCarried(ItemStack.EMPTY);
            }
        }
    }
}
