package se.gory_moon.horsepower.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class MillingSerializer extends HPRecipeSerializer<MillingRecipe> {

    @Override
    public MillingRecipe read(ResourceLocation recipeId, JsonObject json) {
        String group = JSONUtils.getString(json, "group", "");
        JsonElement jsonelement = JSONUtils.isJsonArray(json, "ingredient") ? JSONUtils.getJsonArray(json, "ingredient") : JSONUtils.getJsonObject(json, "ingredient");
        Ingredient ingredient = Ingredient.deserialize(jsonelement);
        ItemStack result = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
        int time = JSONUtils.getInt(json, "time", 1);
        return new MillingRecipe(MillingRecipe.Type.HAND, recipeId, group, ingredient, result, ItemStack.EMPTY, time, 0);
    }

    @Override
    public MillingRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
        String group = buffer.readString(Short.MAX_VALUE);
        Ingredient ingredient = Ingredient.read(buffer);
        ItemStack result = buffer.readItemStack();
        int time = buffer.readInt();
        return new MillingRecipe(MillingRecipe.Type.HAND, recipeId, group, ingredient, result, ItemStack.EMPTY, time, 0);
    }

    @Override
    public void write(PacketBuffer buffer, MillingRecipe recipe) {
        buffer.writeString(recipe.group);
        recipe.input.write(buffer);
        buffer.writeItemStack(recipe.result);
        buffer.writeInt(recipe.time);
    }
}
