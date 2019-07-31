package se.gory_moon.horsepower.data;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import se.gory_moon.horsepower.recipes.MillingRecipe;
import se.gory_moon.horsepower.recipes.RecipeSerializers;

public class MillingRecipeBuilder extends AbstractRecipeBuilder {

    private MillingRecipeBuilder(MillingRecipe.Type type, IItemProvider result, int count, Ingredient input, int time, IItemProvider secondary, int secondaryCount, int secondaryChance) {
        super(type, result, count, input, time, secondary, secondaryCount, secondaryChance, null);
    }

    public static MillingRecipeBuilder millingRecipe(IItemProvider result, int count, Ingredient input, int time) {
        return new MillingRecipeBuilder(MillingRecipe.Type.BOTH, result, count, input, time, null, 0, 0);
    }

    public static MillingRecipeBuilder millingRecipe(IItemProvider result, int count, Ingredient input, int time, IItemProvider secondary, int secondaryCount, int secondaryChance) {
        return new MillingRecipeBuilder(MillingRecipe.Type.BOTH, result, count, input, time, secondary, secondaryCount, secondaryChance);
    }

    public static MillingRecipeBuilder handMillingRecipe(IItemProvider result, int count, Ingredient input, int time) {
        return new MillingRecipeBuilder(MillingRecipe.Type.BOTH, result, count, input, time, null, 0, 0);
    }

    public static MillingRecipeBuilder handMillingRecipe(IItemProvider result, int count, Ingredient input, int time, IItemProvider secondary, int secondaryCount, int secondaryChance) {
        return new MillingRecipeBuilder(MillingRecipe.Type.BOTH, result, count, input, time, secondary, secondaryCount, secondaryChance);
    }

    public static MillingRecipeBuilder horseMillingRecipe(IItemProvider result, int count, Ingredient input, int time) {
        return new MillingRecipeBuilder(MillingRecipe.Type.BOTH, result, count, input, time, null, 0, 0);
    }

    public static MillingRecipeBuilder horseMillingRecipe(IItemProvider result, int count, Ingredient input, int time, IItemProvider secondary, int secondaryCount, int secondaryChance) {
        return new MillingRecipeBuilder(MillingRecipe.Type.BOTH, result, count, input, time, secondary, secondaryCount, secondaryChance);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RecipeSerializers.MILLING_SERIALIZER;
    }
}
