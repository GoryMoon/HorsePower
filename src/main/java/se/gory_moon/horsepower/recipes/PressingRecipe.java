package se.gory_moon.horsepower.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import se.gory_moon.horsepower.Registration;

public class PressingRecipe extends AbstractHPRecipe {

    protected PressingRecipe(ResourceLocation id, Ingredient input, ItemStack result, FluidStack outputFluid, int time, int inputCount) {
        super(RecipeSerializers.PRESSING_TYPE, id, input, result, outputFluid, time, ItemStack.EMPTY, 0, null, inputCount);
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Registration.PRESS_BLOCK.get());
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RecipeSerializers.PRESSING_SERIALIZER;
    }
}
