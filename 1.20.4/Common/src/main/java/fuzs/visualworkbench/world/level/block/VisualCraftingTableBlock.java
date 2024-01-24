package fuzs.visualworkbench.world.level.block;

import fuzs.puzzleslib.api.block.v1.TickingBlockEntity;
import fuzs.puzzleslib.api.block.v1.TickingEntityBlock;
import fuzs.visualworkbench.init.ModRegistry;
import fuzs.visualworkbench.world.level.block.entity.VisualCraftingTableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class VisualCraftingTableBlock extends BaseEntityBlock implements TickingEntityBlock {
    private final Block block;

    public VisualCraftingTableBlock(Block block) {
        super(BlockBehaviour.Properties.copy(block).dropsLike(block));
        this.block = block;
    }

    @Override
    public String getDescriptionId() {
        return this.block.getDescriptionId();
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState blockState) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos) {
        return level.getBlockEntity(blockPos) instanceof VisualCraftingTableBlockEntity crafterBlockEntity ? crafterBlockEntity.getRedstoneSignal() : 0;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VisualCraftingTableBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            if (level.getBlockEntity(pos) instanceof VisualCraftingTableBlockEntity blockEntity) {
                player.openMenu(blockEntity);
                player.awardStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
            }
            return InteractionResult.CONSUME;
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof VisualCraftingTableBlockEntity blockEntity) {
                Containers.dropContents(level, pos, blockEntity.getCraftSlots());
            }
            super.onRemove(state, level, pos, newState, movedByPiston);
        }
    }

    @Override
    public <T extends BlockEntity & TickingBlockEntity> BlockEntityType<T> getBlockEntityType() {
        return (BlockEntityType<T>) ModRegistry.CRAFTING_TABLE_BLOCK_ENTITY.get();
    }
}
