package se.gory_moon.horsepower.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fluids.FluidStack;
import se.gory_moon.horsepower.util.Utils;

public abstract class HPRecipeBase {

    private ItemStack input;
    private ItemStack output;
    private FluidStack outputFluid;
    private ItemStack secondary;
    private int time;
    private int secondaryChance;

    public HPRecipeBase(ItemStack input, ItemStack output, ItemStack secondary, int secondaryChance, int time) {
        this.input = input;
        this.output = output;
        this.time = time;
        this.secondary = secondary;
        this.secondaryChance = MathHelper.clamp(secondaryChance, 0, 100);
    }

    public HPRecipeBase(ItemStack input, FluidStack output, int time) {
        this.input = input;
        this.output = ItemStack.EMPTY;
        this.outputFluid = output;
        this.secondary = ItemStack.EMPTY;
        this.time = time;
    }

    public ItemStack getInput() {
        return input;
    }

    public ItemStack getOutput() {
        return output;
    }

    public FluidStack getOutputFluid() {
        return outputFluid;
    }

    public ItemStack getSecondary() {
        return secondary;
    }

    public int getSecondaryChance() {
        return secondaryChance;
    }

    public int getTime() {
        return time;
    }

    public static ItemStack getWithSize(ItemStack stack, int size) {
        stack.setCount(size);
        return stack;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof HPRecipeBase))
            return false;

        HPRecipeBase recipe = (HPRecipeBase) o;

        return time == recipe.time && secondaryChance == recipe.secondaryChance && input.isItemEqual(recipe.input) && output.isItemEqual(recipe.output) && secondary.isItemEqual(recipe.secondary);
    }

    @Override
    public int hashCode() {
        int result = Utils.getItemStackHashCode(input);
        result = 31 * result + Utils.getItemStackHashCode(output);
        result = 31 * result + Utils.getItemStackHashCode(secondary);
        result = 31 * result + secondaryChance;
        result = 31 * result + time;
        return result;
    }

    @Override
    public String toString() {
        return input + " -> " + output +
                (time > -1 ? " = " + time: "") +
                (!secondary.isEmpty() ? "{" + secondary + "->" + secondaryChance + "%}": "");
    }
}
