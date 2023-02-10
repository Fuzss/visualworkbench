package fuzs.visualworkbench.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;

public class CraftingContainerWrapper extends CraftingContainer {
    private final Container container;
    private final AbstractContainerMenu menu;

    public CraftingContainerWrapper(Container container, AbstractContainerMenu eventHandler, int width, int height) {
        super(eventHandler, width, height);
        this.container = container;
        this.menu = eventHandler;
        if (width * height != this.getContainerSize()) throw new IllegalArgumentException("Wrong crafting inventory dimensions!");
    }

    @Override
    public int getContainerSize() {
        return this.container.getContainerSize();
    }

    @Override
    public boolean isEmpty() {
        return this.container.isEmpty();
    }

    @Override
    public ItemStack getItem(int index) {
        return this.container.getItem(index);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return this.container.removeItemNoUpdate(index);
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        ItemStack itemstack = this.container.removeItem(index, count);
        if (!itemstack.isEmpty()) {
            this.menu.slotsChanged(this);
        }
        return itemstack;
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        this.container.setItem(index, stack);
        this.menu.slotsChanged(this);
    }

    @Override
    public void setChanged() {
        this.container.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public void clearContent() {
        this.container.clearContent();
    }

    @Override
    public void fillStackedContents(StackedContents helper) {
        for (int i = 0; i < this.getContainerSize(); i++) {
            helper.accountSimpleStack(this.getItem(i));
        }
    }
}
