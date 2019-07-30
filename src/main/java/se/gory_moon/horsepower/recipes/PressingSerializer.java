package se.gory_moon.horsepower.recipes;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class PressingSerializer extends HPRecipeSerializer<PressingRecipe> {

    @Override
    public PressingRecipe read(ResourceLocation recipeId, JsonObject json) {
        RecipeData data = readData(json);
        return new PressingRecipe(recipeId, data.ingredient, data.result, data.outputFluid, data.time, data.type);
    }

    @Override
    public PressingRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
        RecipeData data = readData(buffer);
        return new PressingRecipe(recipeId, data.ingredient, data.result, data.outputFluid, data.time, data.type);
    }

    @Override
    public boolean hasTypes() {
        return false;
    }

    @Override
    public String getTypeName() {
        return null;
    }
}
