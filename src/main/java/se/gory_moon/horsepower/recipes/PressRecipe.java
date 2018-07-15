package se.gory_moon.horsepower.recipes;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class PressRecipe extends HPRecipeBase {

    public PressRecipe(ItemStack input, ItemStack output, ItemStack secondary, int secondaryChance, int time) {
        super(input, output, ItemStack.EMPTY, 0, 0);
    }

    public PressRecipe(ItemStack input, FluidStack output) {
        super(input, output, 0);
    }

    public boolean isLiquidRecipe() {
        return getOutputFluid() != null;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof PressRecipe && super.equals(o);
    }
}
