package fuzs.visualworkbench.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.puzzleslib.api.block.v1.entity.TickingEntityBlock;
import fuzs.visualworkbench.init.ModRegistry;
import fuzs.visualworkbench.world.level.block.entity.CraftingTableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockTypes;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.function.Predicate;

public class CraftingTableWithInventoryBlock extends BaseEntityBlock implements TickingEntityBlock<CraftingTableBlockEntity> {
    public static final MapCodec<CraftingTableWithInventoryBlock> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(BlockTypes.CODEC.fieldOf("block").forGetter(block -> block.block)).apply(instance, CraftingTableWithInventoryBlock::new)
    );

    private final Block block;

    public CraftingTableWithInventoryBlock(Block block) {
        super(BlockBehaviour.Properties.ofFullCopy(block).dropsLike(block));
        this.block = block;
    }

    @Override
    public String getDescriptionId() {
        return this.block.getDescriptionId();
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntityType<? extends CraftingTableBlockEntity> getBlockEntityType() {
        return ModRegistry.CRAFTING_TABLE_BLOCK_ENTITY_TYPE.value();
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else if (level.getBlockEntity(pos) instanceof CraftingTableBlockEntity blockEntity) {
            player.openMenu(blockEntity);
            player.awardStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
        }

        return InteractionResult.CONSUME;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        Containers.dropContentsOnDestroy(state, newState, level, pos);
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState blockState) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos) {
        // similar to Crafter
        if (level.getBlockEntity(blockPos) instanceof CraftingTableBlockEntity blockEntity) {
            return (int) blockEntity.getItems().stream().filter(Predicate.not(ItemStack::isEmpty)).count();
        } else {
            return 0;
        }
    }
}
