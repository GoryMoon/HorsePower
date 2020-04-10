package se.gory_moon.horsepower.data;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import se.gory_moon.horsepower.recipes.MillingRecipe;
import se.gory_moon.horsepower.recipes.RecipeSerializers;

public class MillingRecipeBuilder extends AbstractRecipeBuilder {

    private MillingRecipeBuilder(MillingRecipe.Type type, IItemProvider result, int count, Ingredient input, int time, IItemProvider secondary, int secondaryCount, int secondaryChance, int inputCount) {
        super(type, result, count, input, time, secondary, secondaryCount, secondaryChance, null, inputCount);
    }

    public static MillingRecipeBuilder millingRecipe(IItemProvider result, int count, Ingredient input, int time, int inputCount) {
        return new MillingRecipeBuilder(MillingRecipe.Type.BOTH, result, count, input, time, null, 0, 0, inputCount);
    }

    public static MillingRecipeBuilder millingRecipe(IItemProvider result, int count, Ingredient input, int time, IItemProvider secondary, int secondaryCount, int secondaryChance, int inputCount) {
        return new MillingRecipeBuilder(MillingRecipe.Type.BOTH, result, count, input, time, secondary, secondaryCount, secondaryChance, inputCount);
    }

    public static MillingRecipeBuilder manualMillingRecipe(IItemProvider result, int count, Ingredient input, int time, int inputCount) {
        return new MillingRecipeBuilder(MillingRecipe.Type.MANUAL, result, count, input, time, null, 0, 0, inputCount);
    }

    public static MillingRecipeBuilder manualMillingRecipe(IItemProvider result, int count, Ingredient input, int time, IItemProvider secondary, int secondaryCount, int secondaryChance, int inputCount) {
        return new MillingRecipeBuilder(MillingRecipe.Type.MANUAL, result, count, input, time, secondary, secondaryCount, secondaryChance, inputCount);
    }

    public static MillingRecipeBuilder horseMillingRecipe(IItemProvider result, int count, Ingredient input, int time, int inputCount) {
        return new MillingRecipeBuilder(MillingRecipe.Type.HORSE, result, count, input, time, null, 0, 0, inputCount);
    }

    public static MillingRecipeBuilder horseMillingRecipe(IItemProvider result, int count, Ingredient input, int time, IItemProvider secondary, int secondaryCount, int secondaryChance, int inputCount) {
        return new MillingRecipeBuilder(MillingRecipe.Type.HORSE, result, count, input, time, secondary, secondaryCount, secondaryChance, inputCount);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RecipeSerializers.MILLING_SERIALIZER;
    }
}
