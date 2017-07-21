package se.gory_moon.horsepower.jei;

import mezz.jei.api.*;
import mezz.jei.api.gui.ICraftingGridHelper;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;
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
import se.gory_moon.horsepower.recipes.ChoppingRecipe;
import se.gory_moon.horsepower.recipes.GrindstoneRecipe;
import se.gory_moon.horsepower.recipes.HandGrindstoneRecipe;

@JEIPlugin
public class HorsePowerPlugin implements IModPlugin, IJeiPlugin {

    public static final String HAND_GRINDING = "horsepower.hand_grinding";
    public static final String GRINDING = "horsepower.grinding";
    public static final String MANUAL_CHOPPING = "horsepower.manual_chopping";
    public static final String CHOPPING = "horsepower.chopping";

    public static IJeiHelpers jeiHelpers;
    public static IGuiHelper guiHelper;
    private static IJeiRuntime jeiRuntime;
    public static IRecipeRegistry recipeRegistry;
    public static ICraftingGridHelper craftingGridHelper;

    @Override
    public void register(IModRegistry registry) {
        HorsePowerMod.jeiPlugin = this;
        jeiHelpers = registry.getJeiHelpers();
        guiHelper = jeiHelpers.getGuiHelper();
        craftingGridHelper = guiHelper.createCraftingGridHelper(1, 0);

        if (Configs.recipes.useSeperateGrindstoneRecipes) {
            registry.handleRecipes(HandGrindstoneRecipe.class, GrindstoneRecipeWrapper::new, HAND_GRINDING);
            registry.addRecipes(GrindingRecipeMaker.getGrindstoneRecipes(jeiHelpers, true), HAND_GRINDING);
        }

        if (Configs.general.enableHandChoppingBlock && Configs.recipes.useSeperateChoppingRecipes) {
            registry.handleRecipes(ChoppingBlockRecipe.class, ChoppingRecipeWrapper::new, MANUAL_CHOPPING);
            registry.addRecipes(ChoppingRecipeMaker.getChoppingRecipes(jeiHelpers, true), MANUAL_CHOPPING);
        }

        registry.handleRecipes(GrindstoneRecipe.class, GrindstoneRecipeWrapper::new, GRINDING);
        registry.addRecipes(GrindingRecipeMaker.getGrindstoneRecipes(jeiHelpers, false), GRINDING);

        registry.handleRecipes(ChoppingBlockRecipe.class, ChoppingRecipeWrapper::new, CHOPPING);
        registry.addRecipes(ChoppingRecipeMaker.getChoppingRecipes(jeiHelpers, false), CHOPPING);

        registry.handleRecipes(ChoppingRecipe.class, ChoppingBlockCraftingWrapper::new, VanillaRecipeCategoryUid.CRAFTING);

        if (Configs.recipes.useSeperateGrindstoneRecipes)
            registry.addRecipeCatalyst(new ItemStack(ModBlocks.BLOCK_HAND_GRINDSTONE), HAND_GRINDING);
        else
            registry.addRecipeCatalyst(new ItemStack(ModBlocks.BLOCK_HAND_GRINDSTONE), GRINDING);
        if (Configs.general.enableHandChoppingBlock) {
            if (Configs.recipes.useSeperateChoppingRecipes)
                registry.addRecipeCatalyst(new ItemStack(ModBlocks.BLOCK_MANUAL_CHOPPER), MANUAL_CHOPPING);
            else
                registry.addRecipeCatalyst(new ItemStack(ModBlocks.BLOCK_MANUAL_CHOPPER), CHOPPING);
        }
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.BLOCK_GRINDSTONE), GRINDING);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.BLOCK_CHOPPER), CHOPPING);

        registry.addIngredientInfo(new ItemStack(ModBlocks.BLOCK_GRINDSTONE), ItemStack.class, "info.horsepower:grindstone.info1", "info.horsepower:grindstone.info2", "info.horsepower:grindstone.info3");
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        HorsePowerPlugin.jeiRuntime = jeiRuntime;
        recipeRegistry = jeiRuntime.getRecipeRegistry();
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
        subtypeRegistry.registerSubtypeInterpreter(Item.getItemFromBlock(ModBlocks.BLOCK_CHOPPER), itemStack -> {
            NBTTagCompound nbtTagCompound = itemStack.getTagCompound();
            if (itemStack.getMetadata() == OreDictionary.WILDCARD_VALUE || nbtTagCompound == null || nbtTagCompound.hasNoTags()) {
                return null;
            }
            return nbtTagCompound.toString();
        });
        subtypeRegistry.registerSubtypeInterpreter(Item.getItemFromBlock(ModBlocks.BLOCK_MANUAL_CHOPPER), itemStack -> {
            NBTTagCompound nbtTagCompound = itemStack.getTagCompound();
            if (itemStack.getMetadata() == OreDictionary.WILDCARD_VALUE || nbtTagCompound == null || nbtTagCompound.hasNoTags()) {
                return null;
            }
            return nbtTagCompound.toString();
        });
    }

    @Override
    public void registerIngredients(IModIngredientRegistration registry) {

    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        if (Configs.recipes.useSeperateGrindstoneRecipes)
            registry.addRecipeCategories(new HorsePowerGrindingCategory(registry.getJeiHelpers().getGuiHelper(), true));
        registry.addRecipeCategories(new HorsePowerGrindingCategory(registry.getJeiHelpers().getGuiHelper(), false));
        if (Configs.general.enableHandChoppingBlock && Configs.recipes.useSeperateChoppingRecipes)
            registry.addRecipeCategories(new HorsePowerChoppingCategory(registry.getJeiHelpers().getGuiHelper(), true));
        registry.addRecipeCategories(new HorsePowerChoppingCategory(registry.getJeiHelpers().getGuiHelper(), false));
    }

    @Override
    public void removeRecipe() {
        if (Configs.recipes.useSeperateChoppingRecipes) {
            for (GrindstoneRecipeWrapper recipe : GrindingRecipeMaker.getGrindstoneRecipes(jeiHelpers, true)) {
                jeiRuntime.getRecipeRegistry().removeRecipe(recipe, HAND_GRINDING);
            }
        }

        for (GrindstoneRecipeWrapper recipe: GrindingRecipeMaker.getGrindstoneRecipes(jeiHelpers, false)) {
            jeiRuntime.getRecipeRegistry().removeRecipe(recipe, GRINDING);
        }

        if (Configs.recipes.useSeperateChoppingRecipes && Configs.general.enableHandChoppingBlock) {
            for (ChoppingRecipeWrapper recipe : ChoppingRecipeMaker.getChoppingRecipes(jeiHelpers, true)) {
                jeiRuntime.getRecipeRegistry().removeRecipe(recipe, MANUAL_CHOPPING);
            }
        }

        for (ChoppingRecipeWrapper recipe: ChoppingRecipeMaker.getChoppingRecipes(jeiHelpers, false)) {
            jeiRuntime.getRecipeRegistry().removeRecipe(recipe, CHOPPING);
        }
    }

    @Override
    public void addRecipes() {
        if (Configs.recipes.useSeperateChoppingRecipes) {
            for (GrindstoneRecipeWrapper recipe : GrindingRecipeMaker.getGrindstoneRecipes(jeiHelpers, true)) {
                jeiRuntime.getRecipeRegistry().addRecipe(recipe, HAND_GRINDING);
            }
        }

        for (GrindstoneRecipeWrapper recipe: GrindingRecipeMaker.getGrindstoneRecipes(jeiHelpers, false)) {
            jeiRuntime.getRecipeRegistry().addRecipe(recipe, GRINDING);
        }

        if (Configs.recipes.useSeperateChoppingRecipes && Configs.general.enableHandChoppingBlock) {
            for (ChoppingRecipeWrapper recipe : ChoppingRecipeMaker.getChoppingRecipes(jeiHelpers, true)) {
                jeiRuntime.getRecipeRegistry().addRecipe(recipe, MANUAL_CHOPPING);
            }
        }

        for (ChoppingRecipeWrapper recipe: ChoppingRecipeMaker.getChoppingRecipes(jeiHelpers, false)) {
            jeiRuntime.getRecipeRegistry().addRecipe(recipe, CHOPPING);
        }
    }
}
