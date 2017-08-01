package se.gory_moon.horsepower.jei.press;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.item.ItemStack;
import se.gory_moon.horsepower.recipes.HPRecipes;
import se.gory_moon.horsepower.recipes.PressRecipe;

import java.util.ArrayList;
import java.util.List;

public class PressRecipeMaker {

    public static List<PressRecipeWrapper> getPressRecipes(IJeiHelpers helpers) {
        IStackHelper stackHelper = helpers.getStackHelper();
        ArrayList<PressRecipe> grindingRecipes = HPRecipes.instance().getPressRecipes();

        List<PressRecipeWrapper> recipes = new ArrayList<>();

        for (PressRecipe recipe : grindingRecipes) {
            ItemStack input = recipe.getInput();
            ItemStack output = recipe.getOutput();

            List<ItemStack> inputs = stackHelper.getSubtypes(input);
            PressRecipeWrapper pressRecipeWrapper = new PressRecipeWrapper(inputs, output);
            recipes.add(pressRecipeWrapper);
        }

        return recipes;
    }
}
