package se.gory_moon.horsepower.recipes;

import com.google.gson.JsonObject;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class PressingSerializer extends AbstractRecipeSerializer<PressingRecipe> {

    @Override
    public PressingRecipe read(ResourceLocation recipeId, JsonObject json) {
        RecipeData data = readData(json);
        return new PressingRecipe(recipeId, data.ingredient, data.result, data.outputFluid, data.time, data.inputCount);
    }

    @Override
    public PressingRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
        RecipeData data = readData(buffer);
        return new PressingRecipe(recipeId, data.ingredient, data.result, data.outputFluid, data.time, data.inputCount);
    }

    @Override
    public boolean hasTypes() {
        return false;
    }
}
