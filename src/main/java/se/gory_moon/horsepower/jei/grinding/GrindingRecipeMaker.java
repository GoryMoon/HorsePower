package se.gory_moon.horsepower.jei.grinding;
/*
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.item.ItemStack;
import se.gory_moon.horsepower.recipes.GrindstoneRecipe;
import se.gory_moon.horsepower.recipes.HPRecipes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GrindingRecipeMaker {

    public GrindingRecipeMaker() {
    }

    public static List<GrindstoneRecipeWrapper> getGrindstoneRecipes(IJeiHelpers helpers, boolean hand) {
        IStackHelper stackHelper = helpers.getStackHelper();
        Collection<GrindstoneRecipe> grindingRecipes = hand ? HPRecipes.instance().getHandGrindstoneRecipes(): HPRecipes.instance().getGrindstoneRecipes();

        List<GrindstoneRecipeWrapper> recipes = new ArrayList<>();

        for (GrindstoneRecipe recipe : grindingRecipes) {
            ItemStack input = recipe.getInput();
            ItemStack output = recipe.getOutput();
            ItemStack secondary = recipe.getSecondary();

            List<ItemStack> inputs = stackHelper.getSubtypes(input);
            GrindstoneRecipeWrapper grindstoneRecipeWrapper = new GrindstoneRecipeWrapper(inputs, output, secondary, recipe.getSecondaryChance(), recipe.getTime());
            recipes.add(grindstoneRecipeWrapper);
        }

        return recipes;
    }
}
*/