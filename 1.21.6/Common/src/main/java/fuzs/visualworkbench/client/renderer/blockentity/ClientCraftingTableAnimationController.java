package fuzs.visualworkbench.client.renderer.blockentity;

import fuzs.visualworkbench.VisualWorkbench;
import fuzs.visualworkbench.config.ClientConfig;
import fuzs.visualworkbench.world.level.block.entity.CraftingTableAnimationController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * All animation code in this class is taken from the old <a
 * href="https://www.curseforge.com/minecraft/mc-mods/realbench">RealBench</a> mod.
 */
public final class ClientCraftingTableAnimationController implements CraftingTableAnimationController {
    private final RenderState renderState = new RenderState();
    private final Vec3 position;
    private int sector;
    private boolean animating;
    private float animationAngleStart;
    private float animationAngleEnd;
    private double startTicks;
    private double playerAngle;

    public ClientCraftingTableAnimationController(BlockPos blockPos) {
        this.position = blockPos.getCenter();
    }

    @Override
    public void tick(Level level) {
        ++this.renderState.ticks;
        this.setPlayerAngle(level);
        this.setCurrentSector(this.renderState);
        this.updateAnimationAngles(this.renderState);
    }

    @Override
    public RenderState renderState() {
        return this.renderState;
    }

    private void setPlayerAngle(Level level) {
        Player player = this.getPlayer(level, VisualWorkbench.CONFIG.get(ClientConfig.class).rotateIngredients);
        if (player != null) {
            double d0 = player.getX() - this.position.x();
            double d1 = player.getZ() - this.position.z();
            this.playerAngle = (Math.atan2(-d0, -d1) + 3.9269908169872414D) % 6.283185307179586D;
        }
    }

    @Nullable
    private Player getPlayer(Level level, ClientConfig.RotateIngredients rotateIngredients) {
        if (rotateIngredients != ClientConfig.RotateIngredients.NEVER) {
            return level.getNearestPlayer(this.position.x(),
                    this.position.y(),
                    this.position.z(),
                    3.0,
                    (Entity entity) -> {
                        if (!EntitySelector.NO_SPECTATORS.test(entity)) {
                            return false;
                        } else if (rotateIngredients == ClientConfig.RotateIngredients.CLOSEST_PLAYER) {
                            return true;
                        } else {
                            Minecraft minecraft = Minecraft.getInstance();
                            if (entity == minecraft.player) {
                                return minecraft.screen instanceof CraftingScreen;
                            } else {
                                return false;
                            }
                        }
                    });
        } else {
            return null;
        }
    }

    private void setCurrentSector(RenderState renderState) {
        int sector = (int) (this.playerAngle * 2.0 / Math.PI);
        if (this.sector != sector) {
            this.animating = true;
            this.animationAngleStart = renderState.currentAngle;
            float delta1 = sector * 90.0F - renderState.currentAngle;
            float abs1 = Math.abs(delta1);
            float delta2 = delta1 + 360.0F;
            float shift = Math.abs(delta2);
            float delta3 = delta1 - 360.0F;
            float abs3 = Math.abs(delta3);
            if (abs3 < abs1 && abs3 < shift) {
                this.animationAngleEnd = delta3 + renderState.currentAngle;
            } else if (shift < abs1 && shift < abs3) {
                this.animationAngleEnd = delta2 + renderState.currentAngle;
            } else {
                this.animationAngleEnd = delta1 + renderState.currentAngle;
            }
            this.startTicks = renderState.ticks;
            this.sector = sector;
        }
    }

    private void updateAnimationAngles(RenderState renderState) {
        if (this.animating) {
            if (renderState.ticks >= this.startTicks + 20) {
                this.animating = false;
                renderState.currentAngle = renderState.nextAngle = (this.animationAngleEnd + 360.0F) % 360.0F;
            } else {
                renderState.currentAngle = (calcEaseOutQuad(renderState.ticks - this.startTicks,
                        this.animationAngleStart,
                        this.animationAngleEnd - this.animationAngleStart,
                        20.0) + 360.0F) % 360.0F;
                renderState.nextAngle = (calcEaseOutQuad(Math.min(renderState.ticks + 1 - this.startTicks, 20),
                        this.animationAngleStart,
                        this.animationAngleEnd - this.animationAngleStart,
                        20.0) + 360.0F) % 360.0F;
                if (renderState.currentAngle != 0.0F || renderState.nextAngle != 0.0F) {
                    if (renderState.currentAngle == 0.0F && renderState.nextAngle >= 180.0F) {
                        renderState.currentAngle = 360.0F;
                    }
                    if (renderState.nextAngle == 0.0F && renderState.currentAngle >= 180.0F) {
                        renderState.nextAngle = 360.0F;
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
