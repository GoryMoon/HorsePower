package se.gory_moon.horsepower.jei.chopping;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.item.ItemStack;
import se.gory_moon.horsepower.recipes.ChoppingBlockRecipe;
import se.gory_moon.horsepower.recipes.HPRecipes;

import java.util.ArrayList;
import java.util.List;

public class ChoppingRecipeMaker {

    public static List<ChoppingRecipeWrapper> getChoppingRecipes(IJeiHelpers helpers, boolean hand) {
        IStackHelper stackHelper = helpers.getStackHelper();
        ArrayList<ChoppingBlockRecipe> grindingRecipes = hand ? HPRecipes.instance().getManualChoppingRecipes(): HPRecipes.instance().getChoppingRecipes();

        List<ChoppingRecipeWrapper> recipes = new ArrayList<>();

        for (ChoppingBlockRecipe recipe : grindingRecipes) {
            ItemStack input = recipe.getInput();
            ItemStack output = recipe.getOutput();

            List<ItemStack> inputs = stackHelper.getSubtypes(input);
            ChoppingRecipeWrapper grindstoneRecipeWrapper = new ChoppingRecipeWrapper(inputs, output, recipe.getTime(), hand);
            recipes.add(grindstoneRecipeWrapper);
        }

        return recipes;
    }
}
