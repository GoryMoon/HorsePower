package se.gory_moon.horsepower.recipes;

import net.minecraft.item.ItemStack;

public class ChoppingBlockRecipe extends HPRecipeBase {

    public ChoppingBlockRecipe(ItemStack input, ItemStack output, int time) {
        super(input, output, time);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ChoppingBlockRecipe && super.equals(o);
    }
}
