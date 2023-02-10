package fuzs.visualworkbench.proxy;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;

public class ServerProxy implements Proxy {

    @Override
    public int getLightColor(BlockAndTintGetter blockAndTintGetter, BlockPos pos) {
        return 15728880;
    }
}
