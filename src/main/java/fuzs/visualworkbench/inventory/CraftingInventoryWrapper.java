package fuzs.visualworkbench.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeItemHelper;

public class CraftingInventoryWrapper extends CraftingInventory {

    private final IInventory inventory;
    private final Container menu;

    public CraftingInventoryWrapper(IInventory inventory, Container eventHandler, int width, int height) {

        super(eventHandler, width, height);
        this.inventory = inventory;
        this.menu = eventHandler;
        if (width * height != this.getContainerSize()) {

            throw new RuntimeException("Wrong crafting inventory dimensions!");
        }
    }

    @Override
    public int getContainerSize() {

        return this.inventory.getContainerSize();
    }

    @Override
    public boolean isEmpty() {

        return this.inventory.isEmpty();
    }

    @Override
    public ItemStack getItem(int index) {

        return this.inventory.getItem(index);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {

        return this.inventory.removeItemNoUpdate(index);
    }

    @Override
    public ItemStack removeItem(int index, int count) {

        ItemStack itemstack = this.inventory.removeItem(index, count);
        if (!itemstack.isEmpty()) {

            this.menu.slotsChanged(this);
        }

        return itemstack;
    }

    @Override
    public void setItem(int index, ItemStack stack) {

        this.inventory.setItem(index, stack);
        this.menu.slotsChanged(this);
    }

    @Override
    public void setChanged() {

        this.inventory.setChanged();
    }

    @Override
    public boolean stillValid(PlayerEntity player) {

        return this.inventory.stillValid(player);
    }

    @Override
    public void clearContent() {

        this.inventory.clearContent();
    }

    @Override
    public void fillStackedContents(RecipeItemHelper helper) {

        for (int i = 0; i < this.getContainerSize(); i++) {

            helper.accountSimpleStack(this.getItem(i));
        }
    }

}
