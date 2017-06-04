package se.gorymoon.horsepower.jei;

import mezz.jei.api.*;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import net.minecraft.item.ItemStack;
import se.gorymoon.horsepower.HorsePowerMod;
import se.gorymoon.horsepower.blocks.ModBlocks;
import se.gorymoon.horsepower.jei.grinding.GrindingRecipeMaker;
import se.gorymoon.horsepower.jei.grinding.GrindstoneRecipeWrapper;
import se.gorymoon.horsepower.jei.grinding.HorsePowerGrindingCategory;
import se.gorymoon.horsepower.recipes.GrindstoneRecipe;
import se.gorymoon.horsepower.recipes.GrindstoneRecipes;

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

        registry.addRecipeCategories(new HorsePowerGrindingCategory(jeiHelpers.getGuiHelper()));
        registry.handleRecipes(GrindstoneRecipe.class, GrindstoneRecipeWrapper::new, GRINDING);
        registry.addRecipes(GrindingRecipeMaker.getGrindstoneRecipes(jeiHelpers), GRINDING);


        registry.addRecipeCategoryCraftingItem(new ItemStack(ModBlocks.BLOCK_GRINDSTONE), GRINDING);

        //TODO add description the the mill
        registry.addDescription(new ItemStack(ModBlocks.BLOCK_GRINDSTONE), "info.horsepower:grindstone.info1", "info.horsepower:grindstone.info2", "info.horsepower:grindstone.info3");
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        this.jeiRuntime = jeiRuntime;
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {

    }

    @Override
    public void registerIngredients(IModIngredientRegistration registry) {

    }

    @Override
    public void removeRecipe() {
        for (GrindstoneRecipe recipe: GrindstoneRecipes.instance().getGrindstoneRecipes()) {
            jeiRuntime.getRecipeRegistry().removeRecipe(jeiRuntime.getRecipeRegistry().getRecipeWrapper(recipe, GRINDING), GRINDING);
        }
    }

    @Override
    public void addRecipes() {
        for (GrindstoneRecipe recipe: GrindstoneRecipes.instance().getGrindstoneRecipes()) {
            jeiRuntime.getRecipeRegistry().addRecipe(jeiRuntime.getRecipeRegistry().getRecipeWrapper(recipe, GRINDING), GRINDING);
        }
    }
}
