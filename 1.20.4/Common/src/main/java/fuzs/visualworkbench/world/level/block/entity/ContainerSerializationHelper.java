package fuzs.visualworkbench.world.level.block.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public class ContainerSerializationHelper {

    public static CompoundTag saveAllItems(CompoundTag tag, Container container) {
        return saveAllItems(tag, container, true);
    }

    public static CompoundTag saveAllItems(CompoundTag tag, Container container, boolean saveEmpty) {
        ListTag listTag = new ListTag();
        for (int i = 0; i < container.getContainerSize(); ++i) {
            ItemStack itemStack = container.getItem(i);
            if (!itemStack.isEmpty()) {
                CompoundTag compoundTag = new CompoundTag();
                compoundTag.putByte("Slot", (byte) i);
                itemStack.save(compoundTag);
                listTag.add(compoundTag);
            }
        }
        if (!listTag.isEmpty() || saveEmpty) {
            tag.put("Items", listTag);
        }
        return tag;
    }

    public static void loadAllItems(CompoundTag tag, Container container) {
        ListTag listTag = tag.getList("Items", 10);
        for (int i = 0; i < listTag.size(); ++i) {
            CompoundTag compoundTag = listTag.getCompound(i);
            int j = compoundTag.getByte("Slot") & 255;
            if (j < container.getContainerSize()) {
                container.setItem(j, ItemStack.of(compoundTag));
            }
        }
    }
}
