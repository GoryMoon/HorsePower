package se.gory_moon.horsepower.jei;

import mezz.jei.api.*;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;
import se.gory_moon.horsepower.HorsePowerMod;
import se.gory_moon.horsepower.blocks.ModBlocks;
import se.gory_moon.horsepower.jei.grinding.GrindingRecipeMaker;
import se.gory_moon.horsepower.jei.grinding.GrindstoneRecipeWrapper;
import se.gory_moon.horsepower.jei.grinding.HorsePowerGrindingCategory;
import se.gory_moon.horsepower.recipes.GrindstoneRecipe;

@JEIPlugin
public class HorsePowerPlugin implements IModPlugin, IJeiPlugin {

    public static final String GRINDING = "horsepower.grinding";

    public static IJeiHelpers jeiHelpers;
    public static IGuiHelper guiHelper;
    private static IJeiRuntime jeiRuntime;

    @Override
    public void register(IModRegistry registry) {
        HorsePowerMod.jeiPlugin = this;
        jeiHelpers = registry.getJeiHelpers();
        guiHelper = jeiHelpers.getGuiHelper();

        registry.handleRecipes(GrindstoneRecipe.class, GrindstoneRecipeWrapper::new, GRINDING);
        registry.addRecipes(GrindingRecipeMaker.getGrindstoneRecipes(jeiHelpers), GRINDING);

        registry.addRecipeCatalyst(new ItemStack(ModBlocks.BLOCK_HAND_GRINSTONE), GRINDING);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.BLOCK_GRINDSTONE), GRINDING);

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
        registry.addRecipeCategories(new HorsePowerGrindingCategory(registry.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void removeRecipe() {
        for (GrindstoneRecipeWrapper recipe: GrindingRecipeMaker.getGrindstoneRecipes(jeiHelpers)) {
            jeiRuntime.getRecipeRegistry().removeRecipe(recipe, GRINDING);
        }
    }

    @Override
    public void addRecipes() {
        for (GrindstoneRecipeWrapper recipe: GrindingRecipeMaker.getGrindstoneRecipes(jeiHelpers)) {
            jeiRuntime.getRecipeRegistry().addRecipe(recipe, GRINDING);
        }
    }
}
