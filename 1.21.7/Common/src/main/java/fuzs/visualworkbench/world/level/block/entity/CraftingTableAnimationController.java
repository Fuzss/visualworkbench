package fuzs.visualworkbench.world.level.block.entity;

import net.minecraft.world.level.Level;

/**
 * A client-side provider for ticking and storing crafting table item animations on a block entity.
 * <p>
 * Initialize an instance in your block entity constructor, then implement a call for {@link #tick(Level)} on the
 * client.
 */
public interface CraftingTableAnimationController {

    void tick(Level level);

    RenderState renderState();

    class RenderState {
        public int ticks;
        public float currentAngle;
        public float nextAngle;
    }
}
