package fuzs.visualworkbench.world.level.block.entity;

import net.minecraft.world.item.ItemStack;

/**
 * An extension for a {@link net.minecraft.world.level.block.entity.BlockEntity} providing access to data required for rendering.
 */
public interface WorkbenchVisualsProvider {

    /**
     * Get the crafting result for the currently stored ingredients.
     * <p>Can return {@link ItemStack#EMPTY} to not render anything.
     *
     * @return the crafting result
     */
    ItemStack getCraftingResult();

    /**
     * @return storage for animation data
     */
    CraftingTableAnimationController getAnimationController();
}
