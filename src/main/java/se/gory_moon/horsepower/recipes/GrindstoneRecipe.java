package se.gory_moon.horsepower.recipes;

import net.minecraft.item.ItemStack;

public class GrindstoneRecipe extends HPRecipeBase {

    public GrindstoneRecipe(ItemStack input, ItemStack output, int time) {
        super(input, output, time);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof GrindstoneRecipe && super.equals(o);
    }
}
