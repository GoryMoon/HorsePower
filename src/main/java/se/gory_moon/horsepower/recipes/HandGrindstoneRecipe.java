package se.gory_moon.horsepower.recipes;

import net.minecraft.item.ItemStack;

public class HandGrindstoneRecipe extends GrindstoneRecipe {

    public HandGrindstoneRecipe(ItemStack input, ItemStack output, ItemStack secondary, int secondaryChance, int time) {
        super(input, output, secondary, secondaryChance, time);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof HandGrindstoneRecipe && super.equals(o);
    }
}
