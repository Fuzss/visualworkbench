package fuzs.visualworkbench.world.level.block.entity;

import fuzs.visualworkbench.VisualWorkbench;
import fuzs.visualworkbench.config.ClientConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * All animation code in this class is taken from the old <a href="https://www.curseforge.com/minecraft/mc-mods/realbench">RealBench</a> mod.
 */
public class CraftingTableAnimationController {
    private final Vec3 position;

    public int ticks;
    public float currentAngle;
    public float nextAngle;
    public int sector;
    public boolean animating;
    public float animationAngleStart;
    public float animationAngleEnd;
    public double startTicks;
    public double playerAngle;

    public CraftingTableAnimationController(BlockPos blockPos) {
        this.position = blockPos.getCenter();
    }

    public void tick(Level level) {
        ++this.ticks;
        this.setPlayerAngle(level);
        this.setCurrentSector();
        this.tryAnimate();
    }

    private void setPlayerAngle(Level level) {
        Player player;
        if (VisualWorkbench.CONFIG.get(ClientConfig.class).rotateIngredients) {
            player = level.getNearestPlayer(this.position.x(), this.position.y(), this.position.z(), 3.0, false);
        } else {
            player = null;
        }
        if (player != null) {
            double d0 = player.getX() - this.position.x();
            double d1 = player.getZ() - this.position.z();
            this.playerAngle = (Math.atan2(-d0, -d1) + 3.9269908169872414D) % 6.283185307179586D;
        }
    }

    private void setCurrentSector() {
        int sector = (int) (this.playerAngle * 2.0 / Math.PI);
        if (this.sector != sector) {
            this.animating = true;
            this.animationAngleStart = this.currentAngle;
            float delta1 = sector * 90.0F - this.currentAngle;
            float abs1 = Math.abs(delta1);
            float delta2 = delta1 + 360.0F;
            float shift = Math.abs(delta2);
            float delta3 = delta1 - 360.0F;
            float abs3 = Math.abs(delta3);
            if (abs3 < abs1 && abs3 < shift) {
                this.animationAngleEnd = delta3 + this.currentAngle;
            } else if (shift < abs1 && shift < abs3) {
                this.animationAngleEnd = delta2 + this.currentAngle;
            } else {
                this.animationAngleEnd = delta1 + this.currentAngle;
            }
            this.startTicks = this.ticks;
            this.sector = sector;
        }
    }

    private void tryAnimate() {
        if (this.animating) {
            if (this.ticks >= this.startTicks + 20) {
                this.animating = false;
                this.currentAngle = this.nextAngle = (this.animationAngleEnd + 360.0F) % 360.0F;
            } else {
                this.currentAngle = (calcEaseOutQuad(this.ticks - this.startTicks, this.animationAngleStart, this.animationAngleEnd - this.animationAngleStart, 20.0) + 360.0F) % 360.0F;
                this.nextAngle = (calcEaseOutQuad(Math.min(this.ticks + 1 - this.startTicks, 20), this.animationAngleStart, this.animationAngleEnd - this.animationAngleStart, 20.0) + 360.0F) % 360.0F;
                if (this.currentAngle != 0.0F || this.nextAngle != 0.0F) {
                    if (this.currentAngle == 0.0F && this.nextAngle >= 180.0F) {
                        this.currentAngle = 360.0F;
                    }
                    if (this.nextAngle == 0.0F && this.currentAngle >= 180.0F) {
                        this.nextAngle = 360.0F;
                    }
                }
            }
        }
    }

    private static float calcEaseOutQuad(double t, float b, float c, double d) {
        float z = (float) t / (float) d;
        return -c * z * (z - 2.0F) + b;
    }
}