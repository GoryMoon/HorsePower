package se.gory_moon.horsepower.recipes;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.Optional;

public abstract class AbstractRecipeSerializer<T extends AbstractHPRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T> {

    protected RecipeData readData(JsonObject json) {
        AbstractHPRecipe.Type type = hasTypes() ? AbstractHPRecipe.Type.fromName(JSONUtils.getString(json, "recipe_type", AbstractHPRecipe.Type.BOTH.getName())): null;
        Ingredient ingredient = Ingredient.deserialize(JSONUtils.isJsonArray(json, "ingredient") ? JSONUtils.getJsonArray(json, "ingredient") : JSONUtils.getJsonObject(json, "ingredient"));
        ItemStack result = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
        Optional<JsonObject> obj = Optional.ofNullable(JSONUtils.getJsonObject(json, "secondary", null));
        ItemStack secondary = obj.map(ShapedRecipe::deserializeItem).orElse(ItemStack.EMPTY);
        int time = JSONUtils.getInt(json, "time", 1);
        int secondaryChance = JSONUtils.getInt(json, "secondary_chance", 1);

        //TODO parse fluid in recipe
        return new RecipeData(type, ingredient, result, secondary, time, secondaryChance, null);
    }

    protected RecipeData readData(PacketBuffer buffer) {
        AbstractHPRecipe.Type type = hasTypes() ? AbstractHPRecipe.Type.fromId(buffer.readInt()): null;
        Ingredient ingredient = Ingredient.read(buffer);
        ItemStack result = buffer.readItemStack();
        ItemStack secondary = buffer.readItemStack();
        int time = buffer.readInt();
        int secondaryChance = buffer.readInt();
        FluidStack outputFluid = buffer.readBoolean() ? FluidStack.loadFluidStackFromNBT(buffer.readCompoundTag()): null;
        return new RecipeData(type, ingredient, result, secondary, time, secondaryChance, outputFluid);
    }

    @Override
    public void write(PacketBuffer buffer, T recipe) {
        if (hasTypes()) {
            buffer.writeInt(recipe.recipeType.getId());
        }
        recipe.input.write(buffer);
        buffer.writeItemStack(recipe.result);
        buffer.writeItemStack(recipe.secondary);
        buffer.writeInt(recipe.time);
        buffer.writeInt(recipe.secondaryChance);
        if (recipe.outputFluid == null) {
            buffer.writeBoolean(false);
        } else {
            buffer.writeBoolean(true);
            buffer.writeCompoundTag(recipe.outputFluid.writeToNBT(new CompoundNBT()));
        }
    }

    public abstract boolean hasTypes();

    public static class RecipeData {
        public final AbstractHPRecipe.Type type;
        public final Ingredient ingredient;
        public final ItemStack result;
        public final ItemStack secondary;
        public final int time;
        public final int secondaryChance;
        public final FluidStack outputFluid;

        public RecipeData(AbstractHPRecipe.Type type, Ingredient ingredient, ItemStack result, ItemStack secondary, int time, int secondaryChance, FluidStack outputFluid) {
            this.type = type;
            this.ingredient = ingredient;
            this.result = result;
            this.secondary = secondary;
            this.time = time;
            this.secondaryChance = secondaryChance;
            this.outputFluid = outputFluid;
        }
    }
}
