package se.gory_moon.horsepower.data;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import se.gory_moon.horsepower.recipes.AbstractHPRecipe;
import se.gory_moon.horsepower.recipes.serializers.RecipeSerializers;

public class ChoppingRecipeBuilder extends AbstractRecipeBuilder {

    private ChoppingRecipeBuilder(AbstractHPRecipe.Type type, IItemProvider result, int count, Ingredient input, int time) {
        super(type, result, count, input, time, null, 0, 0, null, 1);
    }

    public static ChoppingRecipeBuilder choppingRecipe(AbstractHPRecipe.Type type, IItemProvider result, int count, Ingredient input, int time) {
        return new ChoppingRecipeBuilder(type, result,count,input, time);
    }
    
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RecipeSerializers.CHOPPING_SERIALIZER;
    }
}
