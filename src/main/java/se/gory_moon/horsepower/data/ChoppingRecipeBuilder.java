package se.gory_moon.horsepower.data;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.fluids.FluidStack;
import se.gory_moon.horsepower.recipes.AbstractHPRecipe;
import se.gory_moon.horsepower.recipes.RecipeSerializers;

public class ChoppingRecipeBuilder extends AbstractRecipeBuilder {

    private ChoppingRecipeBuilder(AbstractHPRecipe.Type type, IItemProvider result, int count, Ingredient input) {
        super(type, result, count, input, 0, null, 0, 0, null, 1);
    }

    public static ChoppingRecipeBuilder choppingRecipe(AbstractHPRecipe.Type type, IItemProvider result, int count, Ingredient input) {
        return new ChoppingRecipeBuilder(type, result,count,input);
    }
    
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RecipeSerializers.CHOPPING_SERIALIZER;
    }
}
