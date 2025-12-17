package fuzs.visualworkbench.client.renderer.blockentity.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

import java.util.ArrayList;
import java.util.List;

public class CraftingTableRenderState extends BlockEntityRenderState {
    public List<ItemStackRenderState> items = new ArrayList<>();
    public final ItemStackRenderState result = new ItemStackRenderState();
    public float time;
    public float angle;
    public int itemLightCoords;
}
