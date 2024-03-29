package fuzs.visualworkbench.handler;

import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import fuzs.visualworkbench.core.CommonAbstractions;
import fuzs.visualworkbench.world.level.block.VisualCraftingTableBlock;
import fuzs.visualworkbench.world.level.block.entity.CraftingTableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

public class OpenMenuHandler {

    public static EventResultHolder<InteractionResult> onUseBlock(Player player, Level level, InteractionHand hand, BlockHitResult hitResult) {

        BlockPos pos = hitResult.getBlockPos();
        if (level.getBlockState(pos).getBlock() instanceof VisualCraftingTableBlock block) {
            if (block.hasBlockEntity() && level.getBlockEntity(pos) instanceof CraftingTableBlockEntity blockEntity) {

                if (player.isSpectator()) {
                    if (!level.isClientSide) {
                        player.openMenu(blockEntity);
                    }
                    return EventResultHolder.interrupt(InteractionResult.SUCCESS);
                }

                UseOnContext context = new UseOnContext(player, hand, hitResult);
                InteractionResult result = CommonAbstractions.INSTANCE.onItemUseFirst(player.getItemInHand(hand), context);
                if (result != InteractionResult.PASS) {
                    return EventResultHolder.interrupt(result);
                }

                boolean preventInteraction = !CommonAbstractions.INSTANCE.doesSneakBypassUse(player.getMainHandItem(), level, pos, player) || !CommonAbstractions.INSTANCE.doesSneakBypassUse(player.getOffhandItem(), level, pos, player);
                if (!preventInteraction || !player.isSecondaryUseActive()) {
                    if (!level.isClientSide) {
                        player.openMenu(blockEntity);
                        player.awardStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
                    }
                    return EventResultHolder.interrupt(InteractionResult.sidedSuccess(level.isClientSide));
                }

                // don't cancel here, vanilla will try to interact with the block again, which will fail since the player is sneaking,
                // but afterward there are additional attempts to use the currently held item which we don't need to copy here
            }
        }

        return EventResultHolder.pass();
    }
}
