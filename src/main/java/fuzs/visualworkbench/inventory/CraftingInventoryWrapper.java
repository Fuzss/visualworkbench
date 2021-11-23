package fuzs.visualworkbench.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeItemHelper;

public class CraftingInventoryWrapper extends CraftingInventory {
    private final IInventory inventory;

    public CraftingInventoryWrapper(IInventory inventory, Container eventHandler, int width, int height) {
        super(eventHandler, width, height);
        this.inventory = inventory;
        if (width * height != this.getContainerSize()) throw new IllegalArgumentException("Wrong crafting inventory dimensions!");
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
        // usually crafting inventory calls slotsChanged on the menu here, we don't need that as we use a container listener instead of the method
        // this is needed to be able to respond to changes made by other players
        return this.inventory.removeItem(index, count);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        // usually crafting inventory calls slotsChanged on the menu here, we don't need that as we use a container listener instead of the method
        // this is needed to be able to respond to changes made by other players
        this.inventory.setItem(index, stack);
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
