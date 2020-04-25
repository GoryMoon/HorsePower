package se.gory_moon.horsepower.data;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.fluids.FluidStack;
import se.gory_moon.horsepower.recipes.RecipeSerializers;

public class ChoppingRecipeBuilder extends AbstractRecipeBuilder {

    private ChoppingRecipeBuilder(IItemProvider result, int count, Ingredient input) {
        super(null, result, count, input, 0, null, 0, 0, null, 1);
    }

    public static ChoppingRecipeBuilder choppingRecipe(IItemProvider result, int count, Ingredient input) {
        return new ChoppingRecipeBuilder(result,count,input);
    }
    
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RecipeSerializers.CHOPPING_SERIALIZER;
    }
}
