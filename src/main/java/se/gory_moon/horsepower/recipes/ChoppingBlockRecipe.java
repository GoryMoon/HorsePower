package se.gory_moon.horsepower.recipes;

import net.minecraft.item.ItemStack;

public class ChoppingBlockRecipe extends HPRecipeBase {

    public ChoppingBlockRecipe(ItemStack input, ItemStack output, ItemStack secondary, int secondaryChance, int time) {
        super(getWithSize(input, 1), output, ItemStack.EMPTY, 0, time);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ChoppingBlockRecipe && super.equals(o);
    }
}
