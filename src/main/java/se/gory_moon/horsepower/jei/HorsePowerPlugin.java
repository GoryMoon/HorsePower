package se.gory_moon.horsepower.jei;

import mezz.jei.api.*;
import mezz.jei.api.gui.ICraftingGridHelper;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.blocks.BlockHPChoppingBase;
import se.gory_moon.horsepower.blocks.ModBlocks;
import se.gory_moon.horsepower.jei.chopping.ChoppingRecipeMaker;
import se.gory_moon.horsepower.jei.chopping.ChoppingRecipeWrapper;
import se.gory_moon.horsepower.jei.chopping.HorsePowerChoppingCategory;
import se.gory_moon.horsepower.jei.grinding.GrindingRecipeMaker;
import se.gory_moon.horsepower.jei.grinding.GrindstoneRecipeWrapper;
import se.gory_moon.horsepower.jei.grinding.HorsePowerGrindingCategory;
import se.gory_moon.horsepower.jei.press.HorsePowerPressCategory;
import se.gory_moon.horsepower.jei.press.PressRecipeMaker;
import se.gory_moon.horsepower.jei.press.PressRecipeWrapper;
import se.gory_moon.horsepower.recipes.*;

@JEIPlugin
public class HorsePowerPlugin implements IModPlugin {

    public static final String HAND_GRINDING = "horsepower.hand_grinding";
    public static final String GRINDING = "horsepower.grinding";
    public static final String MANUAL_CHOPPING = "horsepower.manual_chopping";
    public static final String CHOPPING = "horsepower.chopping";
    public static final String PRESS_ITEM = "horsepower.press";
    public static final String PRESS_FLUID = "horsepower.press_fluid";

    public static IJeiHelpers jeiHelpers;
    public static IGuiHelper guiHelper;
    private static IJeiRuntime jeiRuntime;
    public static IRecipeRegistry recipeRegistry;
    public static ICraftingGridHelper craftingGridHelper;

    @Override
    public void register(IModRegistry registry) {

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

        registry.handleRecipes(PressRecipe.class, PressRecipeWrapper::new, PRESS_ITEM);
        registry.handleRecipes(PressRecipe.class, PressRecipeWrapper::new, PRESS_FLUID);
        registry.addRecipes(PressRecipeMaker.getPressItemRecipes(jeiHelpers), PRESS_ITEM);
        registry.addRecipes(PressRecipeMaker.getPressFluidRecipes(jeiHelpers), PRESS_FLUID);

        registry.handleRecipes(ShapedChoppingRecipe.class, ShapedChoppingCraftingWrapper::new, VanillaRecipeCategoryUid.CRAFTING);
        registry.handleRecipes(ShapelessChoppingRecipe.class, ShapelessChoppingCraftingWrapper::new, VanillaRecipeCategoryUid.CRAFTING);

        if (Configs.recipes.useSeperateGrindstoneRecipes)
            registry.addRecipeCatalyst(new ItemStack(ModBlocks.BLOCK_HAND_GRINDSTONE), HAND_GRINDING);
        else
            registry.addRecipeCatalyst(new ItemStack(ModBlocks.BLOCK_HAND_GRINDSTONE), GRINDING);
        if (Configs.general.enableHandChoppingBlock) {
            ItemStack itemStackManualChopper = BlockHPChoppingBase.createItemStack(ModBlocks.BLOCK_MANUAL_CHOPPER, 1, new ItemStack(Item.getItemFromBlock(Blocks.LOG)));
            if (Configs.recipes.useSeperateChoppingRecipes)
                registry.addRecipeCatalyst(itemStackManualChopper, MANUAL_CHOPPING);
            else
                registry.addRecipeCatalyst(itemStackManualChopper, CHOPPING);
        }
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.BLOCK_GRINDSTONE), GRINDING);

        ItemStack itemStackChopper = BlockHPChoppingBase.createItemStack(ModBlocks.BLOCK_CHOPPER, 1, new ItemStack(Item.getItemFromBlock(Blocks.LOG)));
        registry.addRecipeCatalyst(itemStackChopper, CHOPPING);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.BLOCK_PRESS), PRESS_ITEM);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.BLOCK_PRESS), PRESS_FLUID);

        registry.addIngredientInfo(new ItemStack(ModBlocks.BLOCK_GRINDSTONE), VanillaTypes.ITEM, "info.horsepower:grindstone.info1", "info.horsepower:grindstone.info2", "info.horsepower:grindstone.info3");
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
            if (itemStack.getMetadata() == OreDictionary.WILDCARD_VALUE || nbtTagCompound == null || nbtTagCompound.isEmpty()) {
                return null;
            }
            return nbtTagCompound.toString();
        });
        subtypeRegistry.registerSubtypeInterpreter(Item.getItemFromBlock(ModBlocks.BLOCK_MANUAL_CHOPPER), itemStack -> {
            NBTTagCompound nbtTagCompound = itemStack.getTagCompound();
            if (itemStack.getMetadata() == OreDictionary.WILDCARD_VALUE || nbtTagCompound == null || nbtTagCompound.isEmpty()) {
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

        registry.addRecipeCategories(new HorsePowerPressCategory(registry.getJeiHelpers().getGuiHelper(), false));
        registry.addRecipeCategories(new HorsePowerPressCategory(registry.getJeiHelpers().getGuiHelper(), true));
    }

}
