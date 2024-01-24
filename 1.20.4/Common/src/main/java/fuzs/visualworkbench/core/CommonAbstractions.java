package fuzs.visualworkbench.core;

import fuzs.puzzleslib.api.core.v1.ServiceProviderHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.LevelReader;

public interface CommonAbstractions {
    CommonAbstractions INSTANCE = ServiceProviderHelper.load(CommonAbstractions.class);

    boolean doesSneakBypassUse(ItemStack stack, LevelReader level, BlockPos pos, Player player);

    InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context);
}
