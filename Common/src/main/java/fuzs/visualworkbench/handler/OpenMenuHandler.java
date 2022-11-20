package fuzs.visualworkbench.handler;

import fuzs.visualworkbench.world.level.block.VisualCraftingTableBlock;
import fuzs.visualworkbench.world.level.block.entity.CraftingTableBlockEntity;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

public class OpenMenuHandler {

    public InteractionResult onUseBlock(Player player, Level world, InteractionHand hand, BlockHitResult hitResult) {
        if (world.getBlockState(hitResult.getBlockPos()).getBlock() instanceof VisualCraftingTableBlock block && block.hasBlockEntity() && world.getBlockEntity(hitResult.getBlockPos()) instanceof CraftingTableBlockEntity blockEntity && !player.isCrouching()) {
            if (world.isClientSide) {
                return InteractionResult.SUCCESS;
            } else {
                player.openMenu(blockEntity);
                player.awardStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.PASS;
    }
}
