package fuzs.visualworkbench.world.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;

public class VisualResultContainer extends ResultContainer {
    private final Container container;

    public VisualResultContainer(NonNullList<ItemStack> items, Container container) {
        this.container = container;
        this.itemStacks = items;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack result = super.removeItem(slot, amount);
        if (!result.isEmpty()) this.setChanged();
        return result;
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
