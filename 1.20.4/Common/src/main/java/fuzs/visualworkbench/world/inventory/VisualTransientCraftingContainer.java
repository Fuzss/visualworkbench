package fuzs.visualworkbench.world.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;

public class VisualTransientCraftingContainer extends TransientCraftingContainer {
    private final Container container;

    public VisualTransientCraftingContainer(AbstractContainerMenu menu, int width, int height, NonNullList<ItemStack> items, Container container) {
        super(menu, width, height, items);
        this.container = container;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack itemStack = super.removeItem(slot, amount);
        if (!itemStack.isEmpty()) this.setChanged();
        return itemStack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        super.setItem(slot, stack);
        this.setChanged();
    }

    @Override
    public void setChanged() {
        super.setChanged();
        this.container.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }
}
