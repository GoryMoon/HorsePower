package se.gory_moon.horsepower.recipes;

import net.minecraft.item.ItemStack;
import se.gory_moon.horsepower.util.HPUtils;

public class ComparableItemStack {

    private final ItemStack stack;

    public ComparableItemStack(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public int hashCode() {
        return HPUtils.getItemStackHashCode(stack);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ComparableItemStack))
            return false;

        ComparableItemStack that = (ComparableItemStack) o;

        return ItemStack.areItemStackTagsEqual(stack, that.stack) && stack.isItemEqual(that.stack);
    }

    @Override
    public String toString() {
        return stack.toString();
    }
}
