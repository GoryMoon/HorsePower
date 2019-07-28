package se.gory_moon.horsepower.recipes;

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
        Ingredient ingredient = Ingredient.deserialize(JSONUtils.isJsonArray(json, "ingredient") ? JSONUtils.getJsonArray(json, "ingredient") : JSONUtils.getJsonObject(json, "ingredient"));
        ItemStack result = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
        int time = JSONUtils.getInt(json, "time", 1);
        JsonObject obj = JSONUtils.getJsonObject(json, "secondary", null);
        ItemStack secondary = obj == null ? ItemStack.EMPTY: ShapedRecipe.deserializeItem(obj);
        int secondaryChance = JSONUtils.getInt(json, "time", 1);
        MillingRecipe.Type type = MillingRecipe.Type.fromName(JSONUtils.getString(json, "milling_type", MillingRecipe.Type.BOTH.getName()));
        return new MillingRecipe(type, recipeId, ingredient, result, secondary, time, secondaryChance);
    }

    @Override
    public MillingRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
        MillingRecipe.Type type = MillingRecipe.Type.fromId(buffer.readInt());
        Ingredient ingredient = Ingredient.read(buffer);
        ItemStack result = buffer.readItemStack();
        ItemStack secondary = buffer.readItemStack();
        int time = buffer.readInt();
        int secondaryChance = buffer.readInt();
        return new MillingRecipe(type, recipeId, ingredient, result, secondary, time, secondaryChance);
    }

    @Override
    public void write(PacketBuffer buffer, MillingRecipe recipe) {
        buffer.writeInt(recipe.getRecipeType().getId());
        recipe.input.write(buffer);
        buffer.writeItemStack(recipe.result);
        buffer.writeItemStack(recipe.secondary);
        buffer.writeInt(recipe.time);
        buffer.writeInt(recipe.secondaryChance);
    }
}
