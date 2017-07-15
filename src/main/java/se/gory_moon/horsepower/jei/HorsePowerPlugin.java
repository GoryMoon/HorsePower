package se.gory_moon.horsepower.jei;

import mezz.jei.api.*;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.HorsePowerMod;
import se.gory_moon.horsepower.blocks.ModBlocks;
import se.gory_moon.horsepower.jei.chopping.ChoppingRecipeMaker;
import se.gory_moon.horsepower.jei.chopping.ChoppingRecipeWrapper;
import se.gory_moon.horsepower.jei.chopping.HorsePowerChoppingCategory;
import se.gory_moon.horsepower.jei.grinding.GrindingRecipeMaker;
import se.gory_moon.horsepower.jei.grinding.GrindstoneRecipeWrapper;
import se.gory_moon.horsepower.jei.grinding.HorsePowerGrindingCategory;
import se.gory_moon.horsepower.recipes.ChoppingBlockRecipe;
import se.gory_moon.horsepower.recipes.GrindstoneRecipe;
import se.gory_moon.horsepower.recipes.HandGrindstoneRecipe;

@JEIPlugin
public class HorsePowerPlugin implements IModPlugin, IJeiPlugin {

    public static final String HAND_GRINDING = "horsepower.hand_grinding";
    public static final String GRINDING = "horsepower.grinding";
    public static final String CHOPPING = "horsepower.chopping";

    public static IJeiHelpers jeiHelpers;
    public static IGuiHelper guiHelper;
    private static IJeiRuntime jeiRuntime;

    @Override
    public void register(IModRegistry registry) {
        HorsePowerMod.jeiPlugin = this;
        jeiHelpers = registry.getJeiHelpers();
        guiHelper = jeiHelpers.getGuiHelper();

        if (Configs.useSeperateRecipes) {
            registry.handleRecipes(HandGrindstoneRecipe.class, GrindstoneRecipeWrapper::new, HAND_GRINDING);
            registry.addRecipes(GrindingRecipeMaker.getGrindstoneRecipes(jeiHelpers, true), HAND_GRINDING);
        }

        registry.handleRecipes(GrindstoneRecipe.class, GrindstoneRecipeWrapper::new, GRINDING);
        registry.addRecipes(GrindingRecipeMaker.getGrindstoneRecipes(jeiHelpers, false), GRINDING);

        registry.handleRecipes(ChoppingBlockRecipe.class, ChoppingRecipeWrapper::new, CHOPPING);
        registry.addRecipes(ChoppingRecipeMaker.getChoppingRecipes(jeiHelpers), CHOPPING);

        if (Configs.useSeperateRecipes)
            registry.addRecipeCatalyst(new ItemStack(ModBlocks.BLOCK_HAND_GRINDSTONE), HAND_GRINDING);
        else
            registry.addRecipeCatalyst(new ItemStack(ModBlocks.BLOCK_HAND_GRINDSTONE), GRINDING);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.BLOCK_GRINDSTONE), GRINDING);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.BLOCK_CHOPPER), CHOPPING);

        registry.addIngredientInfo(new ItemStack(ModBlocks.BLOCK_GRINDSTONE), ItemStack.class, "info.horsepower:grindstone.info1", "info.horsepower:grindstone.info2", "info.horsepower:grindstone.info3");
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        HorsePowerPlugin.jeiRuntime = jeiRuntime;
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {

    }

    @Override
    public void registerIngredients(IModIngredientRegistration registry) {

    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        if (Configs.useSeperateRecipes)
            registry.addRecipeCategories(new HorsePowerGrindingCategory(registry.getJeiHelpers().getGuiHelper(), true));
        registry.addRecipeCategories(new HorsePowerGrindingCategory(registry.getJeiHelpers().getGuiHelper(), false));
        registry.addRecipeCategories(new HorsePowerChoppingCategory(registry.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void removeRecipe() {
        for (GrindstoneRecipeWrapper recipe: GrindingRecipeMaker.getGrindstoneRecipes(jeiHelpers, true)) {
            jeiRuntime.getRecipeRegistry().removeRecipe(recipe, HAND_GRINDING);
        }

        for (GrindstoneRecipeWrapper recipe: GrindingRecipeMaker.getGrindstoneRecipes(jeiHelpers, false)) {
            jeiRuntime.getRecipeRegistry().removeRecipe(recipe, GRINDING);
        }

        for (ChoppingRecipeWrapper recipe: ChoppingRecipeMaker.getChoppingRecipes(jeiHelpers)) {
            jeiRuntime.getRecipeRegistry().removeRecipe(recipe, CHOPPING);
        }
    }

    @Override
    public void addRecipes() {
        for (GrindstoneRecipeWrapper recipe: GrindingRecipeMaker.getGrindstoneRecipes(jeiHelpers, true)) {
            jeiRuntime.getRecipeRegistry().addRecipe(recipe, HAND_GRINDING);
        }

        for (GrindstoneRecipeWrapper recipe: GrindingRecipeMaker.getGrindstoneRecipes(jeiHelpers, false)) {
            jeiRuntime.getRecipeRegistry().addRecipe(recipe, GRINDING);
        }

        for (ChoppingRecipeWrapper recipe: ChoppingRecipeMaker.getChoppingRecipes(jeiHelpers)) {
            jeiRuntime.getRecipeRegistry().addRecipe(recipe, CHOPPING);
        }
    }
}
