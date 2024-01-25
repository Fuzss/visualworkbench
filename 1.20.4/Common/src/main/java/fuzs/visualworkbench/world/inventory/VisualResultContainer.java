package fuzs.visualworkbench.world.inventory;

import fuzs.visualworkbench.world.level.block.entity.CraftingTableBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;

public class VisualResultContainer extends ResultContainer {
    private final Container container;

    public VisualResultContainer(CraftingTableBlockEntity container) {
        this.container = container;
        this.itemStacks = container.getResultItems();
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack result = ContainerHelper.removeItem(this.itemStacks, slot, amount);
        if (!result.isEmpty()) this.container.setChanged();
        return result;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        super.setItem(slot, stack);
        this.container.setChanged();
    }
}
