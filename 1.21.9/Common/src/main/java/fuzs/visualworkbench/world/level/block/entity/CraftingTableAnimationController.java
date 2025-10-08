package fuzs.visualworkbench.world.level.block.entity;

import net.minecraft.world.level.Level;

/**
 * A client-side provider for ticking and storing crafting table item animations on a block entity.
 * <p>
 * Initialise an instance in your block entity constructor, then implement a call for {@link #tick(Level)} on the
 * client.
 */
public interface CraftingTableAnimationController {

    void tick(Level level);

    int getTime();

    float getCurrentAngle();

    float getNextAngle();
}
