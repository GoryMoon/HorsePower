package se.gory_moon.horsepower.recipes;

import net.minecraft.item.ItemStack;

public class HandMillstoneRecipe extends MillstoneRecipe {

    public HandMillstoneRecipe(ItemStack input, ItemStack output, ItemStack secondary, int secondaryChance, int time) {
        super(input, output, secondary, secondaryChance, time);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof HandMillstoneRecipe && super.equals(o);
    }
}
