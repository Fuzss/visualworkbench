package fuzs.visualworkbench.world.level.block.entity;

import fuzs.puzzleslib.api.block.v1.TickingBlockEntity;
import fuzs.puzzleslib.api.core.v1.CommonAbstractions;
import fuzs.visualworkbench.VisualWorkbench;
import fuzs.visualworkbench.config.ClientConfig;
import fuzs.visualworkbench.init.ModRegistry;
import fuzs.visualworkbench.util.MathHelper;
import fuzs.visualworkbench.world.inventory.VisualCraftingMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class VisualCraftingTableBlockEntity extends BlockEntity implements MenuProvider, TickingBlockEntity {
    private static final String TAG_CRAFT_SLOTS = "CraftSlots";
    private static final String TAG_RESULT_SLOTS = "ResultSlots";
    private static final String TAG_RECIPE_USED = "RecipeUsed";

    private TransientCraftingContainer craftSlots;
    private ResultContainer resultSlots;
    public int ticks;
    public float currentAngle;
    public float nextAngle;
    private int sector;
    private boolean animating;
    private float animationAngleStart;
    private float animationAngleEnd;
    private double startTicks;
    private double playerAngle;

    public VisualCraftingTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModRegistry.CRAFTING_TABLE_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.crafting");
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.craftSlots = new TransientCraftingContainer(new AbstractContainerMenu(null, -1) {
            @Override
            public ItemStack quickMoveStack(Player player, int index) {
                return null;
            }

            @Override
            public boolean stillValid(Player player) {
                return true;
            }

            @Override
            public void slotsChanged(Container container) {
                VisualCraftingTableBlockEntity.this.setChanged();
            }
        }, 3, 3, NonNullList.createWithCapacity(9));
        ContainerSerializationHelper.loadAllItems(tag.getCompound(TAG_CRAFT_SLOTS), this.craftSlots);
        this.resultSlots = new ResultContainer();
        ContainerSerializationHelper.loadAllItems(tag.getCompound(TAG_RESULT_SLOTS), this.resultSlots);
        Optional<? extends Recipe<?>> recipe = CommonAbstractions.INSTANCE.getMinecraftServer().getRecipeManager().byKey(ResourceLocation.tryParse(tag.getString(TAG_RECIPE_USED)));
        recipe.ifPresent(this.resultSlots::setRecipeUsed);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        CompoundTag compoundTag1 = new CompoundTag();
        ContainerSerializationHelper.saveAllItems(compoundTag1, this.craftSlots);
        tag.put(TAG_CRAFT_SLOTS, compoundTag1);
        CompoundTag compoundTag2 = new CompoundTag();
        ContainerSerializationHelper.saveAllItems(compoundTag2, this.resultSlots);
        tag.put(TAG_RESULT_SLOTS, compoundTag2);
        if (this.resultSlots.getRecipeUsed() != null) {
            tag.putString(TAG_RECIPE_USED, this.resultSlots.getRecipeUsed().getId().toString());
        }
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

    public CraftingContainer getCraftSlots() {
        return this.craftSlots;
    }

    public ResultContainer getResultSlots() {
        return this.resultSlots;
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new VisualCraftingMenu(id, playerInventory, this, ContainerLevelAccess.create(this.getLevel(), this.getBlockPos()));
    }

    public int getRedstoneSignal() {
        int i = 0;
        for (int j = 0; j < this.craftSlots.getContainerSize(); ++j) {
            ItemStack itemStack = this.craftSlots.getItem(j);
            if (!itemStack.isEmpty()) {
                ++i;
            }
        }
        return i;
    }

    @Override
    public void clientTick() {
        ++this.ticks;
        if (this.craftSlots.isEmpty() || !VisualWorkbench.CONFIG.get(ClientConfig.class).rotateIngredients) return;
        Player player = this.getLevel().getNearestPlayer((double) this.worldPosition.getX() + 0.5D, (double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D, 3.0D, false);
        if (player != null) {
            double d0 = player.getX() - ((double) this.worldPosition.getX() + 0.5D);
            double d1 = player.getZ() - ((double) this.worldPosition.getZ() + 0.5D);
            this.playerAngle = (Math.atan2(-d0, -d1) + 3.9269908169872414D) % 6.283185307179586D;
        }
        // most animation code is taken from the old RealBench mod (https://www.curseforge.com/minecraft/mc-mods/realbench)
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
        if (this.animating) {
            if (this.ticks >= this.startTicks + 20) {
                this.animating = false;
                this.currentAngle = this.nextAngle = (this.animationAngleEnd + 360.0F) % 360.0F;
            } else {
                this.currentAngle = (MathHelper.easeOutQuad(this.ticks - this.startTicks, this.animationAngleStart, this.animationAngleEnd - this.animationAngleStart, 20.0) + 360.0F) % 360.0F;
                this.nextAngle = (MathHelper.easeOutQuad(Math.min(this.ticks + 1 - this.startTicks, 20), this.animationAngleStart, this.animationAngleEnd - this.animationAngleStart, 20.0) + 360.0F) % 360.0F;
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
}
