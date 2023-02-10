package fuzs.visualworkbench.proxy;

import fuzs.puzzleslib.core.DistTypeExecutor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;

public interface Proxy {
    @SuppressWarnings("Convert2MethodRef")
    Proxy INSTANCE = DistTypeExecutor.getForDistType(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());

    int getLightColor(BlockAndTintGetter blockAndTintGetter, BlockPos pos);
}
