package fuzs.visualworkbench.world.level.block.entity;

import fuzs.visualworkbench.registry.ModRegistry;
import fuzs.visualworkbench.world.inventory.ModCraftingMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class CraftingTableBlockEntity extends BaseContainerBlockEntity {
    private static final String LAST_RECIPE_ID_TAG = "LastRecipeId";

    private final NonNullList<ItemStack> inventory = NonNullList.withSize(9, ItemStack.EMPTY);
    private int lastResultId;
    public int combinedLight;
    public int ticks;
    public float currentAngle;
    public float nextAngle;
    private byte sector;
    private boolean animating;
    private float animationAngleStart;
    private float animationAngleEnd;
    private double startTicks;
    private double playerAngle;
    private final ContainerData dataAccess = new ContainerData() {

        @Override
        public int get(int index) {
            return index == 0 ? CraftingTableBlockEntity.this.getLastResultId() : 0;
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) {
                CraftingTableBlockEntity.this.setLastResultId(value);
            }
        }

        @Override
        public int getCount() {
            return 1;
        }
    };

    public CraftingTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModRegistry.CRAFTING_TABLE_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    protected Component getDefaultName() {
        return new TranslatableComponent("container.crafting");
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.inventory.clear();
        ContainerHelper.loadAllItems(tag, this.inventory);
        this.lastResultId = tag.getInt(LAST_RECIPE_ID_TAG);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, this.inventory, true);
        tag.putInt(LAST_RECIPE_ID_TAG, this.lastResultId);
    }

    @Override
    @Nullable
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    @Override
    public int getContainerSize() {
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : this.inventory) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int index) {
        return index >= 0 && index < this.inventory.size() ? this.inventory.get(index) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        ItemStack itemStack = ContainerHelper.removeItem(this.inventory, index, count);
        if (!itemStack.isEmpty()) {
            // vanilla is fine, but crafting tweaks mod doesn't update the client properly without this
            this.setChanged();
        }
        return itemStack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        ItemStack itemStack = ContainerHelper.takeItem(this.inventory, index);
        if (!itemStack.isEmpty()) {
            // vanilla is fine, but crafting tweaks mod doesn't update the client properly without this
            this.setChanged();
        }
        return itemStack;
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        if (index >= 0 && index < this.inventory.size()) {
            this.inventory.set(index, stack);
            // vanilla is fine, but crafting tweaks mod doesn't update the client properly without this
            this.setChanged();
        }
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.getLevel().getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return !(player.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) > 64.0D);
        }
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory playerInventory) {
        return new ModCraftingMenu(id, playerInventory, this, ContainerLevelAccess.create(this.getLevel(), this.getBlockPos()), this.dataAccess);
    }

    @Override
    public void clearContent() {
        this.inventory.clear();
    }

    public int getLastResultId() {
        return this.lastResultId;
    }

    private void setLastResultId(int lastResultId) {
        this.lastResultId = lastResultId;
        this.setChanged();
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, CraftingTableBlockEntity pBlockEntity) {
        if (pBlockEntity.isEmpty()) return;
        // renderer checks inside this block where lighting will be 0, so we instead check above
        pBlockEntity.combinedLight = pBlockEntity.getLevel() != null ? getLightColor(pLevel, pBlockEntity.getBlockPos().above()) : 15728880;
        ++pBlockEntity.ticks;
        Player playerentity = pLevel.getNearestPlayer((double)pBlockEntity.worldPosition.getX() + 0.5D, (double)pBlockEntity.worldPosition.getY() + 0.5D, (double)pBlockEntity.worldPosition.getZ() + 0.5D, 3.0D, false);
        if (playerentity != null) {
            double d0 = playerentity.getX() - ((double)pBlockEntity.worldPosition.getX() + 0.5D);
            double d1 = playerentity.getZ() - ((double)pBlockEntity.worldPosition.getZ() + 0.5D);
            pBlockEntity.playerAngle = (Math.atan2(-d0, -d1) + 3.9269908169872414D) % 6.283185307179586D;
        }
        // most of pBlockEntity code is taken from the old RealBench mod (https://www.curseforge.com/minecraft/mc-mods/realbench)
        byte sector = (byte) ((int) (pBlockEntity.playerAngle * 2.0 / Math.PI));
        if (pBlockEntity.sector != sector) {
            pBlockEntity.animating = true;
            pBlockEntity.animationAngleStart = pBlockEntity.currentAngle;
            float delta1 = (float) sector * 90.0F - pBlockEntity.currentAngle;
            float abs1 = Math.abs(delta1);
            float delta2 = delta1 + 360.0F;
            float shift = Math.abs(delta2);
            float delta3 = delta1 - 360.0F;
            float abs3 = Math.abs(delta3);
            if (abs3 < abs1 && abs3 < shift) {
                pBlockEntity.animationAngleEnd = delta3 + pBlockEntity.currentAngle;
            } else if (shift < abs1 && shift < abs3) {
                pBlockEntity.animationAngleEnd = delta2 + pBlockEntity.currentAngle;
            } else {
                pBlockEntity.animationAngleEnd = delta1 + pBlockEntity.currentAngle;
            }
            pBlockEntity.startTicks = pBlockEntity.ticks;
            pBlockEntity.sector = sector;
        }
        if (pBlockEntity.animating) {
            if (pBlockEntity.ticks >= pBlockEntity.startTicks + 20) {
                pBlockEntity.animating = false;
                pBlockEntity.currentAngle = pBlockEntity.nextAngle = (pBlockEntity.animationAngleEnd + 360.0F) % 360.0F;
            } else {
                pBlockEntity.currentAngle = (easeOutQuad(pBlockEntity.ticks - pBlockEntity.startTicks, pBlockEntity.animationAngleStart, pBlockEntity.animationAngleEnd - pBlockEntity.animationAngleStart, 20.0) + 360.0F) % 360.0F;
                pBlockEntity.nextAngle = (easeOutQuad(Math.min(pBlockEntity.ticks + 1 - pBlockEntity.startTicks, 20), pBlockEntity.animationAngleStart, pBlockEntity.animationAngleEnd - pBlockEntity.animationAngleStart, 20.0) + 360.0F) % 360.0F;
                if (pBlockEntity.currentAngle != 0.0F || pBlockEntity.nextAngle != 0.0F) {
                    if (pBlockEntity.currentAngle == 0.0F && pBlockEntity.nextAngle >= 180.0F) {
                        pBlockEntity.currentAngle = 360.0F;
                    }
                    if (pBlockEntity.nextAngle == 0.0F && pBlockEntity.currentAngle >= 180.0F) {
                        pBlockEntity.nextAngle = 360.0F;
                    }
                }
            }
        }
    }

    private static float easeOutQuad(double t, float b, float c, double d) {
        float z = (float) t / (float) d;
        return -c * z * (z - 2.0F) + b;
    }

    /**
     * from {@link net.minecraft.client.renderer.LevelRenderer#getLightColor}
     */
    public static int getLightColor(BlockAndTintGetter displayReader, BlockPos pos) {
        return getLightColor(displayReader, displayReader.getBlockState(pos), pos);
    }

    /**
     * from {@link net.minecraft.client.renderer.LevelRenderer#getLightColor}
     */
    public static int getLightColor(BlockAndTintGetter displayReader, BlockState state, BlockPos pos) {
        if (state.emissiveRendering(displayReader, pos)) {
            return 15728880;
        } else {
            int i = displayReader.getBrightness(LightLayer.SKY, pos);
            int j = displayReader.getBrightness(LightLayer.BLOCK, pos);
            int k = state.getLightEmission(displayReader, pos);
            if (j < k) {
                j = k;
            }
            return i << 20 | j << 4;
        }
    }
}
