package fuzs.visualworkbench.world.inventory;

import fuzs.puzzleslib.util.PuzzlesUtil;
import fuzs.visualworkbench.mixin.accessor.CraftingMenuAccessor;
import fuzs.visualworkbench.registry.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ModCraftingMenu extends CraftingMenu implements ContainerListener {
    private final CraftingContainer craftSlots;
    private final ResultContainer resultSlots;
    private final ContainerLevelAccess access;
    private final Player player;
    private final ContainerData containerData;

    public ModCraftingMenu(int id, Inventory playerInventory) {
        this(id, playerInventory, new SimpleContainer(9), ContainerLevelAccess.NULL, new SimpleContainerData(1));
    }

    public ModCraftingMenu(int id, Inventory playerInventory, Container tileInventory, ContainerLevelAccess access, ContainerData containerData) {
        super(id, playerInventory, access);
        this.craftSlots = new CraftingContainerWrapper(tileInventory, this, 3, 3);
        this.resultSlots = ((CraftingMenuAccessor) this).getResultSlots();
        this.access = access;
        this.player = playerInventory.player;
        this.containerData = containerData;
        ((CraftingMenuAccessor) this).setCraftSlots(this.craftSlots);
        this.slots.set(0, PuzzlesUtil.make(new ResultSlot(playerInventory.player, this.craftSlots, this.resultSlots, 0, 124, 35) {
            @Override
            public void set(ItemStack p_75215_1_) {
                // fast workbench makes this do nothing (via mixin), but in our case it is needed to avoid client desync
                this.container.setItem(this.getSlotIndex(), p_75215_1_);
                this.setChanged();
            }
        }, slot -> slot.index = 0));
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                final int slotIndex = j + i * 3;
                this.slots.set(slotIndex + 1, PuzzlesUtil.make(new Slot(this.craftSlots, slotIndex, 30 + j * 18, 17 + i * 18), slot -> slot.index = slotIndex + 1));
            }
        }
        this.addSlotListener(this);
    }

    @Override
    public MenuType<?> getType() {
        return ModRegistry.CRAFTING_MENU_TYPE.get();
    }

    /**
     * copied from vanilla super to avoid FastWorkbench mixin which would lead to a ClassCastException for craftSlots
     */
    @Override
    public void slotsChanged(Container pInventory) {
        this.access.execute((p_39386_, p_39387_) -> {
            slotChangedCraftingGrid(this, p_39386_, this.player, this.craftSlots, this.resultSlots);
            this.containerData.set(0, Registry.ITEM.getId(this.resultSlots.getItem(0).getItem()));
        });
    }

    @Override
    public void slotChanged(AbstractContainerMenu p_71111_1_, int p_71111_2_, ItemStack p_71111_3_) {
        if (p_71111_1_ == this) {
            this.access.execute((Level level, BlockPos blockPos) -> {
                if (p_71111_2_ >= 1 && p_71111_2_ < 10) {
                    this.slotsChanged(this.craftSlots);
                }
            });
        }
    }

    @Override
    public void dataChanged(AbstractContainerMenu pContainerMenu, int pDataSlotIndex, int pValue) {
        
    }

    @Override
    public boolean stillValid(Player player) {
        return this.craftSlots.stillValid(player);
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

    /**
     * copied from vanilla super to avoid FastWorkbench mixin which would lead to a ClassCastException for craftSlots
     */
    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (pIndex == 0) {
                this.access.execute((p_39378_, p_39379_) -> {
                    itemstack1.getItem().onCraftedBy(itemstack1, p_39378_, pPlayer);
                });
                if (!this.moveItemStackTo(itemstack1, 10, 46, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
            } else if (pIndex >= 10 && pIndex < 46) {
                if (!this.moveItemStackTo(itemstack1, 1, 10, false)) {
                    if (pIndex < 37) {
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

            slot.onTake(pPlayer, itemstack1);
            if (pIndex == 0) {
                pPlayer.drop(itemstack1, false);
            }
        }

        return itemstack;
    }
}
