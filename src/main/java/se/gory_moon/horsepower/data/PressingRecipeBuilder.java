package se.gory_moon.horsepower.data;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.fluids.FluidStack;
import se.gory_moon.horsepower.recipes.RecipeSerializers;

public class PressingRecipeBuilder extends AbstractRecipeBuilder {

    private PressingRecipeBuilder(IItemProvider result, int count, Ingredient input, FluidStack fluidStack, int inputCount) {
        super(null, result, count, input, 0, null, 0, 0, fluidStack, inputCount);
    }

    public static PressingRecipeBuilder pressingRecipe(IItemProvider result, int count, Ingredient input, int inputCount) {
        return new PressingRecipeBuilder(result, count, input, null, inputCount);
    }

    public static PressingRecipeBuilder pressingRecipe(FluidStack result, Ingredient input, int inputCount) {
        return new PressingRecipeBuilder(null, 0, input, result, inputCount);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RecipeSerializers.PRESSING_SERIALIZER;
    }
}
