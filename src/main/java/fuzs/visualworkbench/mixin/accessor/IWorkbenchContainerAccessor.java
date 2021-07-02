package fuzs.visualworkbench.mixin.accessor;

import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.WorkbenchContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WorkbenchContainer.class)
public interface IWorkbenchContainerAccessor {

    @Accessor
    CraftingInventory getCraftSlots();

    @Accessor
    void setCraftSlots(CraftingInventory craftSlots);

    @Accessor
    CraftResultInventory getResultSlots();

}
