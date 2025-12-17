package fuzs.visualworkbench.world.level.block.entity;

import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.visualworkbench.client.renderer.blockentity.ClientCraftingTableAnimationController;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * An extension for a {@link net.minecraft.world.level.block.entity.BlockEntity} providing access to data required for
 * rendering.
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

    /**
     * @param blockPos the position of the block entity
     * @return the animation controller
     */
    static CraftingTableAnimationController createAnimationController(BlockPos blockPos) {
        if (ModLoaderEnvironment.INSTANCE.isClient()) {
            return new ClientCraftingTableAnimationController(blockPos);
        } else {
            return new CraftingTableAnimationController() {
                @Override
                public void tick(Level level) {
                    // NO-OP
                }

                @Override
                public int getTime() {
                    return 0;
                }

                @Override
                public float getCurrentAngle() {
                    return 0.0F;
                }

                @Override
                public float getNextAngle() {
                    return 0.0F;
                }
            };
        }
    }
}
