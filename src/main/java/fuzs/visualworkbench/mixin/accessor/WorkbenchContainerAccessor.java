package fuzs.visualworkbench.mixin.accessor;

import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.WorkbenchContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WorkbenchContainer.class)
public interface WorkbenchContainerAccessor {
    @Mutable
    @Accessor
    void setCraftSlots(CraftingInventory craftSlots);

    @Accessor
    CraftResultInventory getResultSlots();
}
