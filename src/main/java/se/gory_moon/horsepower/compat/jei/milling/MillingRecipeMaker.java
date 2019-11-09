package se.gory_moon.horsepower.compat.jei.milling;
/*
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.item.ItemStack;
import se.gory_moon.horsepower.recipes.MillstoneRecipe;
import se.gory_moon.horsepower.recipes.HPRecipes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GrindingRecipeMaker {

    public GrindingRecipeMaker() {
    }

    public static List<GrindstoneRecipeWrapper> getMillstoneRecipes(IJeiHelpers helpers, boolean hand) {
        IStackHelper stackHelper = helpers.getStackHelper();
        Collection<MillstoneRecipe> grindingRecipes = hand ? HPRecipes.instance().getHandMillstoneRecipes(): HPRecipes.instance().getMillstoneRecipes();

        List<GrindstoneRecipeWrapper> recipes = new ArrayList<>();

        for (MillstoneRecipe recipe : grindingRecipes) {
            ItemStack input = recipe.getInput();
            ItemStack result = recipe.getOutput();
            ItemStack secondary = recipe.getSecondary();

            List<ItemStack> inputs = stackHelper.getSubtypes(input);
            GrindstoneRecipeWrapper grindstoneRecipeWrapper = new GrindstoneRecipeWrapper(inputs, result, secondary, recipe.getSecondaryChance(), recipe.getTime());
            recipes.add(grindstoneRecipeWrapper);
        }

        return recipes;
    }
}
*/