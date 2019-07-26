package se.gory_moon.horsepower.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import se.gory_moon.horsepower.blocks.ModBlocks;

public class MillingRecipe extends AbstractHPRecipe {

    private Type recipeType;

    public MillingRecipe(Type recipeType, ResourceLocation id, String group, Ingredient input, ItemStack result, ItemStack secondary, int time, int secondaryChance) {
        super(RecipeSerializers.MILLING_TYPE, id, group, input, result, null, time, secondary, secondaryChance);
        this.recipeType = recipeType;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(recipeType == Type.HAND ? ModBlocks.BLOCK_HAND_MILLSTONE.orElseThrow(RuntimeException::new): ModBlocks.BLOCK_MILLSTONE.orElseThrow(RuntimeException::new));
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RecipeSerializers.MILLING_SERIALIZER;
    }

    public Type getRecipeType() {
        return recipeType;
    }

    public enum Type {
        BOTH,
        HAND,
        HORSE
    }
}
