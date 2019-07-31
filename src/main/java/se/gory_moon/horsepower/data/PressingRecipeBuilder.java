package se.gory_moon.horsepower.data;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.fluids.FluidStack;
import se.gory_moon.horsepower.recipes.RecipeSerializers;

public class PressingRecipeBuilder extends AbstractRecipeBuilder {

    private PressingRecipeBuilder(IItemProvider result, int count, Ingredient input, FluidStack fluidStack) {
        super(null, result, count, input, 0, null, 0, 0, fluidStack);
    }

    public static PressingRecipeBuilder pressingRecipe(IItemProvider result, int count, Ingredient input) {
        return new PressingRecipeBuilder(result, count, input, null);
    }

    public static PressingRecipeBuilder pressingRecipe(FluidStack result, Ingredient input) {
        return new PressingRecipeBuilder(null, 0, input, result);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RecipeSerializers.PRESSING_SERIALIZER;
    }
}
