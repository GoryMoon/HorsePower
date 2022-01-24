package se.gory_moon.horsepower.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import se.gory_moon.horsepower.Registration;
import se.gory_moon.horsepower.recipes.serializers.RecipeSerializers;

public class ChoppingRecipe extends AbstractHPRecipe {

    public ChoppingRecipe(ResourceLocation id, Ingredient input, ItemStack result, ItemStack secondary, int time, int secondaryChance, Type recipeType, int inputCount) {
        super(RecipeSerializers.CHOPPING_TYPE, id, input, result, null, time, secondary, secondaryChance, recipeType, inputCount);
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(recipeType == Type.MANUAL ? Registration.MANUAL_CHOPPER_BLOCK.get(): Registration.CHOPPER_BLOCK.get());
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RecipeSerializers.CHOPPING_SERIALIZER;
    }
}
