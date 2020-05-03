package se.gory_moon.horsepower.recipes;

import com.google.common.collect.Maps;
import net.minecraft.item.ItemStack;
import se.gory_moon.horsepower.Configs;

import java.util.Map;

public class HPRecipes {

    private static HPRecipes INSTANCE = new HPRecipes();

//    private final Map<ComparableItemStack, ChoppingBlockRecipe> choppingBlockRecipes = Maps.newHashMap();
//    private final Map<ComparableItemStack, ChoppingBlockRecipe> manualChoppingBlockRecipes = Maps.newHashMap();

    public static HPRecipes instance() {
        return INSTANCE;
    }

    public static AbstractHPRecipe checkTypeRecipe(AbstractHPRecipe recipe, AbstractHPRecipe.Type type) {
        return !hasTypeRecipe(recipe, type) ? null: recipe;
    }

    public static boolean hasTypeRecipe(AbstractHPRecipe recipe, AbstractHPRecipe.Type type) {
        return recipe != null && (recipe.getRecipeType() == null || type == null || recipe.getRecipeType().is(type));
    }

    public static int getTypeTime(AbstractHPRecipe recipe, AbstractHPRecipe.Type type) {
        return hasTypeRecipe(recipe, type) ? recipe.getTime(): 16;
    }

//    public ChoppingBlockRecipe getChoppingBlockRecipe(ItemStack stack, boolean hand) {
//        if (stack.isEmpty())
//            return null;
//        return hand && Configs.recipes.useSeperateChoppingRecipes ? manualChoppingBlockRecipes.get(new ComparableItemStack(stack)): choppingBlockRecipes.get(new ComparableItemStack(stack));
//    }

//    public ItemStack getChopperResult(ItemStack stack, boolean hand) {
//        ChoppingBlockRecipe recipe = getChoppingBlockRecipe(stack, hand);
//        return recipe != null ? recipe.getOutput(): ItemStack.EMPTY;
//    }

//    public boolean hasChopperRecipe(ItemStack stack, boolean hand) {
//        return getChoppingBlockRecipe(stack, hand) != null;
//    }

//    public int getChoppingTime(ItemStack stack, boolean hand) {
//        int mult = Configs.recipes.useSeperateChoppingRecipes ? 1: (hand ? Configs.SERVER.choppingMultiplier.get(): 1);
//        ChoppingBlockRecipe recipe = getChoppingBlockRecipe(stack, hand);
//        return mult * (recipe != null ? recipe.getTime(): 1);
//    }
}
