package se.gorymoon.horsepower.jei.grinding;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.item.ItemStack;
import se.gorymoon.horsepower.recipes.GrindstoneRecipe;
import se.gorymoon.horsepower.recipes.GrindstoneRecipes;

import java.util.ArrayList;
import java.util.List;

public class GrindingRecipeMaker {

    public GrindingRecipeMaker() {
    }

    public static List<GrindstoneRecipeWrapper> getGrindstoneRecipes(IJeiHelpers helpers) {
        IStackHelper stackHelper = helpers.getStackHelper();
        GrindstoneRecipes furnaceRecipes = GrindstoneRecipes.instance();
        ArrayList<GrindstoneRecipe> grindingRecipes = furnaceRecipes.getGrindstoneRecipes();;

        List<GrindstoneRecipeWrapper> recipes = new ArrayList<>();

        for (GrindstoneRecipe recipe : grindingRecipes) {
            ItemStack input = recipe.getInput();
            ItemStack output = recipe.getOutput();

            List<ItemStack> inputs = stackHelper.getSubtypes(input);
            GrindstoneRecipeWrapper grindstoneRecipeWrapper = new GrindstoneRecipeWrapper(inputs, output, recipe.getTime());
            recipes.add(grindstoneRecipeWrapper);
        }

        return recipes;
    }
}
