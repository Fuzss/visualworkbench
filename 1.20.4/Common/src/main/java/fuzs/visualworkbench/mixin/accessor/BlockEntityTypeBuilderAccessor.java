package fuzs.visualworkbench.mixin.accessor;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(BlockEntityType.Builder.class)
public interface BlockEntityTypeBuilderAccessor {

    @Accessor("validBlocks")
    @Mutable
    void visualworkbench$setValidBlocks(Set<Block> validBlocks);
}
