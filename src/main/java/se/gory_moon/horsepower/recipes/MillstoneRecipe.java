package se.gory_moon.horsepower.recipes;

import net.minecraft.item.ItemStack;

public class MillstoneRecipe extends HPRecipeBase {

    public MillstoneRecipe(ItemStack input, ItemStack output, ItemStack secondary, int secondaryChance, int time) {
        super(input, output, secondary, secondaryChance, time);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof MillstoneRecipe && super.equals(o);
    }
}
