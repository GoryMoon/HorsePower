package se.gory_moon.horsepower.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import se.gory_moon.horsepower.Registration;

public class MillingRecipe extends AbstractHPRecipe {

    public MillingRecipe(ResourceLocation id, Ingredient input, ItemStack result, ItemStack secondary, int time, int secondaryChance, Type recipeType, int inputCount) {
        super(RecipeSerializers.MILLING_TYPE, id, input, result, null, time, secondary, secondaryChance, recipeType, inputCount);
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(recipeType == Type.MANUAL ? Registration.MANUAL_MILLSTONE_BLOCK.get(): Registration.MILLSTONE_BLOCK.get());
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RecipeSerializers.MILLING_SERIALIZER;
    }
}
