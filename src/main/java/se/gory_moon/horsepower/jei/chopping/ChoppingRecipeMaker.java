package se.gory_moon.horsepower.jei.chopping;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.item.ItemStack;
import se.gory_moon.horsepower.recipes.ChopperRecipe;
import se.gory_moon.horsepower.recipes.HPRecipes;

import java.util.ArrayList;
import java.util.List;

public class ChoppingRecipeMaker {

    public static List<ChoppingRecipeWrapper> getGrindstoneRecipes(IJeiHelpers helpers) {
        IStackHelper stackHelper = helpers.getStackHelper();
        HPRecipes furnaceRecipes = HPRecipes.instance();
        ArrayList<ChopperRecipe> grindingRecipes = furnaceRecipes.getChoppingRecipes();

        List<ChoppingRecipeWrapper> recipes = new ArrayList<>();

        for (ChopperRecipe recipe : grindingRecipes) {
            ItemStack input = recipe.getInput();
            ItemStack output = recipe.getOutput();

            List<ItemStack> inputs = stackHelper.getSubtypes(input);
            ChoppingRecipeWrapper grindstoneRecipeWrapper = new ChoppingRecipeWrapper(inputs, output, recipe.getTime());
            recipes.add(grindstoneRecipeWrapper);
        }

        return recipes;
    }
}
