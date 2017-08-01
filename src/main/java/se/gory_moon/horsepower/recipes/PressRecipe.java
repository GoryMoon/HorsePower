package se.gory_moon.horsepower.recipes;

import net.minecraft.item.ItemStack;

public class PressRecipe extends HPRecipeBase {

    public PressRecipe(ItemStack input, ItemStack output, ItemStack secondary, int secondaryChance, int time) {
        super(input, output, ItemStack.EMPTY, 0, 0);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof PressRecipe && super.equals(o);
    }
}
