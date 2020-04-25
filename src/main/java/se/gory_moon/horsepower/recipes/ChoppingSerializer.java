package se.gory_moon.horsepower.recipes;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class ChoppingSerializer extends AbstractRecipeSerializer<ChoppingRecipe> {

    @Override
    public ChoppingRecipe read(ResourceLocation recipeId, JsonObject json) {
        RecipeData data = readData(json);
        return new ChoppingRecipe(recipeId, data.ingredient, data.result, data.secondary, data.time, data.secondaryChance, data.type, data.inputCount);
    }

    @Override
    public ChoppingRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
        RecipeData data = readData(buffer);
        return new ChoppingRecipe(recipeId, data.ingredient, data.result, data.secondary, data.time, data.secondaryChance, data.type, data.inputCount);
    }

    @Override
    public boolean hasTypes() {
        return false;
    }
}
