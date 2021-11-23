package fuzs.visualworkbench.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;

public class CraftingContainerWrapper extends CraftingContainer {
    private final Container inventory;

    public CraftingContainerWrapper(Container inventory, AbstractContainerMenu eventHandler, int width, int height) {
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
    public boolean stillValid(Player player) {
        return this.inventory.stillValid(player);
    }

    @Override
    public void clearContent() {
        this.inventory.clearContent();
    }

    @Override
    public void fillStackedContents(StackedContents helper) {
        for (int i = 0; i < this.getContainerSize(); i++) {
            helper.accountSimpleStack(this.getItem(i));
        }
    }
}
