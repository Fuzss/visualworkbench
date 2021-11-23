package fuzs.visualworkbench.block;

public interface CraftingBlockEntityProvider {
    boolean withCraftingBlockEntity(boolean levelLoaded);

    enum CraftingBlockEntityState {
        INVALID, ABSENT, PRESENT
    }
}
