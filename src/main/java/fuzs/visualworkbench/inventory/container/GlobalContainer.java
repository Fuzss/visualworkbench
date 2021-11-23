package fuzs.visualworkbench.inventory.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.world.server.ServerWorld;

import java.util.function.Consumer;

public interface GlobalContainer {
    default void slotsChangedGlobally(IInventory inventory, IWorldPosCallable access, PlayerEntity excluded, Consumer<GlobalContainer> consumer) {

    }
    default void slotsChangedGlobally(IInventory inventory, IWorldPosCallable access, PlayerEntity excluded) {
        access.execute((level, pos) -> {
            if (!level.isClientSide) {
                ((ServerWorld) level).players().stream()
                        .filter(player -> player != excluded)
                        .map(player -> player.containerMenu)
                        .filter(this::isSameMenu)
                        .forEach(menu -> ((GlobalContainer) menu).slotsChangedLocally(inventory));
            }
        });
    }

    boolean isSameMenu(Container other);

    void slotsChangedLocally(IInventory inventory);
}
