package fuzs.visualworkbench.proxy;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;

public class ClientProxy extends ServerProxy {

    @Override
    public int getLightColor(BlockAndTintGetter blockAndTintGetter, BlockPos pos) {
        return LevelRenderer.getLightColor(blockAndTintGetter, pos);
    }
}
