package se.gory_moon.horsepower.jei.chopping;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.item.ItemStack;
import se.gory_moon.horsepower.jei.chopping.manual.ManualChoppingRecipeWrapper;
import se.gory_moon.horsepower.recipes.ChoppingBlockRecipe;
import se.gory_moon.horsepower.recipes.HPRecipes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ChoppingRecipeMaker {

    public static List<IRecipeWrapper> getChoppingRecipes(IJeiHelpers helpers, boolean hand, boolean separate) {
        IStackHelper stackHelper = helpers.getStackHelper();
        Collection<ChoppingBlockRecipe> choppingRecipes = separate ? HPRecipes.instance().getManualChoppingRecipes(): HPRecipes.instance().getChoppingRecipes();

        List<IRecipeWrapper> recipes = new ArrayList<>();

        for (ChoppingBlockRecipe recipe : choppingRecipes) {
            ItemStack input = recipe.getInput();
            ItemStack output = recipe.getOutput();

            List<ItemStack> inputs = stackHelper.getSubtypes(input);
            if (hand)
                recipes.add(new ManualChoppingRecipeWrapper(inputs, output, recipe.getTime()));
            else
                recipes.add(new ChoppingRecipeWrapper(inputs, output, recipe.getTime()));
        }

        return recipes;
    }
}
