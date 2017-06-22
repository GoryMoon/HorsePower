package se.gory_moon.horsepower.recipes;

import net.minecraft.item.ItemStack;

public class HPRecipeBase {

    private ItemStack input;
    private ItemStack output;
    private int time;

    public HPRecipeBase(ItemStack input, ItemStack output, int time) {
        input.setCount(1);
        this.input = input;
        this.output = output;
        this.time = time;
    }

    public ItemStack getInput() {
        return input;
    }

    public ItemStack getOutput() {
        return output;
    }

    public int getTime() {
        return time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GrindstoneRecipe)) return false;

        HPRecipeBase recipe = (HPRecipeBase) o;

        return time == recipe.time && input.equals(recipe.input) && output.equals(recipe.output);
    }

    @Override
    public int hashCode() {
        int result = input.hashCode();
        result = 31 * result + output.hashCode();
        result = 31 * result + time;
        return result;
    }
}
