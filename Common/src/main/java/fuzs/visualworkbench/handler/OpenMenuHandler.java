package fuzs.visualworkbench.handler;

import fuzs.visualworkbench.world.level.block.VisualCraftingTableBlock;
import fuzs.visualworkbench.world.level.block.entity.CraftingTableBlockEntity;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Optional;

public class OpenMenuHandler {

    public static Optional<InteractionResult> onUseBlock(Player player, Level world, InteractionHand hand, BlockHitResult hitResult) {
        if (world.getBlockState(hitResult.getBlockPos()).getBlock() instanceof VisualCraftingTableBlock block) {
            if (block.hasBlockEntity() && world.getBlockEntity(hitResult.getBlockPos()) instanceof CraftingTableBlockEntity blockEntity) {
                boolean preventInteraction = player.isSecondaryUseActive() && (!player.getMainHandItem().isEmpty() || !player.getOffhandItem().isEmpty());
                if (!preventInteraction || player.isSpectator()) {
                    if (!world.isClientSide) {
                        player.openMenu(blockEntity);
                        // vanilla returns success instead of consume for spectators on server, not sure if it actually makes a difference
                        if (player.isSpectator()) return Optional.of(InteractionResult.SUCCESS);
                        player.awardStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
                    }
                    return Optional.of(InteractionResult.sidedSuccess(world.isClientSide));
                }
            }
        }
        return Optional.empty();
    }
}
