package se.gory_moon.horsepower.recipes.serializers;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import se.gory_moon.horsepower.recipes.MillingRecipe;

public class MillingSerializer extends AbstractRecipeSerializer<MillingRecipe> {

    @Override
    public MillingRecipe read(ResourceLocation recipeId, JsonObject json) {
        RecipeData data = readData(json);
        return new MillingRecipe(recipeId, data.ingredient, data.result, data.secondary, data.time, data.secondaryChance, data.type, data.inputCount);
    }

    @Override
    public MillingRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
        RecipeData data = readData(buffer);
        return new MillingRecipe(recipeId, data.ingredient, data.result, data.secondary, data.time, data.secondaryChance, data.type, data.inputCount);
    }

    @Override
    public boolean hasTypes() {
        return true;
    }
}
