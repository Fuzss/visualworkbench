package fuzs.visualworkbench.mixin;

import fuzs.visualworkbench.VisualWorkbench;
import fuzs.visualworkbench.config.CommonConfig;
import fuzs.visualworkbench.handler.BlockConversionHandler;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(WorldGenRegion.class)
abstract class WorldGenRegionMixin {

    @ModifyVariable(method = "setBlock", at = @At(value = "LOAD", ordinal = 0), argsOnly = true)
    public BlockState setBlock(BlockState blockState) {
        if (VisualWorkbench.CONFIG.get(CommonConfig.class).convertVanillaWorkbenchDuringWorldGen) {
            return BlockConversionHandler.convertFromVanillaBlock(blockState);
        } else {
            return blockState;
        }
    }
}
