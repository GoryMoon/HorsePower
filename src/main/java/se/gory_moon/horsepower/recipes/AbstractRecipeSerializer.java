package se.gory_moon.horsepower.recipes;

import com.google.gson.JsonObject;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.Optional;

public abstract class AbstractRecipeSerializer<T extends AbstractHPRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T> {

    protected RecipeData readData(JsonObject json) {
        AbstractHPRecipe.Type type = hasTypes() ? AbstractHPRecipe.Type.fromName(JSONUtils.getString(json, "recipe_type", AbstractHPRecipe.Type.BOTH.getName())): null;
        Ingredient ingredient = Ingredient.deserialize(JSONUtils.isJsonArray(json, "ingredient") ? JSONUtils.getJsonArray(json, "ingredient"): JSONUtils.getJsonObject(json, "ingredient"));
        int inputCount = JSONUtils.getInt(json, "input_count", 1);
        ItemStack result = JSONUtils.hasField(json, "result") ?ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result")):ItemStack.EMPTY;
        Optional<JsonObject> obj = Optional.ofNullable(JSONUtils.getJsonObject(json, "secondary", null));
        ItemStack secondary = obj.map(ShapedRecipe::deserializeItem).orElse(ItemStack.EMPTY);
        int time = JSONUtils.getInt(json, "time", 1);
        int secondaryChance = JSONUtils.getInt(json, "secondary_chance", 0);
   
        FluidStack fluidStack = parseFluidOutput(json); //can be null
        return new RecipeData(type, ingredient, result, secondary, time, secondaryChance, fluidStack,inputCount);
    }

    protected static FluidStack parseFluidOutput(JsonObject result) {
		FluidStack fluidStack = null;
		
		if(JSONUtils.hasField(result, "fluid")) {
			JsonObject fluidJson = JSONUtils.getJsonObject(result, "fluid");
	        int fluidAmount = JSONUtils.getInt(fluidJson, "amount");
	        String outputFluidName = JSONUtils.getString(fluidJson, "id");
	        if(outputFluidName != null && !outputFluidName.isEmpty() && fluidAmount > 0)
	        {
	            Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(outputFluidName));
	            if (fluid != null)
	            	fluidStack = new FluidStack(fluid, fluidAmount);
	        }
		}
		return fluidStack;
	}

    protected RecipeData readData(PacketBuffer buffer) {
        AbstractHPRecipe.Type type = hasTypes() ? AbstractHPRecipe.Type.fromId(buffer.readInt()): null;
        Ingredient ingredient = Ingredient.read(buffer);
        ItemStack result = buffer.readItemStack();
        ItemStack secondary = buffer.readItemStack();
        int time = buffer.readInt();
        int secondaryChance = buffer.readInt();
        FluidStack outputFluid = buffer.readBoolean() ? FluidStack.loadFluidStackFromNBT(buffer.readCompoundTag()): null;
        int inputCount = buffer.readInt();
        return new RecipeData(type, ingredient, result, secondary, time, secondaryChance, outputFluid, inputCount);
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
        buffer.writeInt(recipe.inputCount);
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
        public final int inputCount;

        public RecipeData(AbstractHPRecipe.Type type, Ingredient ingredient, ItemStack result, ItemStack secondary, int time, int secondaryChance, FluidStack outputFluid, int inputCount) {
            this.type = type;
            this.ingredient = ingredient;
            this.result = result;
            this.secondary = secondary;
            this.time = time;
            this.secondaryChance = secondaryChance;
            this.outputFluid = outputFluid;
            this.inputCount = inputCount;
        }
    }
}
