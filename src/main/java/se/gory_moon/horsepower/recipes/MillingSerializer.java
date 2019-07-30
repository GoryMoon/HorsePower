package se.gory_moon.horsepower.recipes;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class MillingSerializer extends HPRecipeSerializer<MillingRecipe> {

    @Override
    public MillingRecipe read(ResourceLocation recipeId, JsonObject json) {
        RecipeData data = readData(json);
        return new MillingRecipe(recipeId, data.ingredient, data.result, data.secondary, data.time, data.secondaryChance, data.type);
    }

    @Override
    public MillingRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
        RecipeData data = readData(buffer);
        return new MillingRecipe(recipeId, data.ingredient, data.result, data.secondary, data.time, data.secondaryChance, data.type);
    }

    @Override
    public boolean hasTypes() {
        return true;
    }

    @Override
    public String getTypeName() {
        return "milling_type";
    }
}
