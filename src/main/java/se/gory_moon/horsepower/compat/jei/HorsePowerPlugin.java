package se.gory_moon.horsepower.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import se.gory_moon.horsepower.HorsePower;
import se.gory_moon.horsepower.blocks.ModBlocks;
import se.gory_moon.horsepower.compat.jei.chopping.HorsePowerChoppingCategory;
import se.gory_moon.horsepower.compat.jei.milling.HorsePowerMillingCategory;
import se.gory_moon.horsepower.compat.jei.press.HorsePowerPressCategory;
import se.gory_moon.horsepower.recipes.AbstractHPRecipe;
import se.gory_moon.horsepower.recipes.MillingRecipe;
import se.gory_moon.horsepower.recipes.PressingRecipe;
import se.gory_moon.horsepower.recipes.RecipeSerializers;
import se.gory_moon.horsepower.util.Constants;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@JeiPlugin
public class HorsePowerPlugin implements IModPlugin {

    public static final ResourceLocation UID = new ResourceLocation(Constants.MOD_ID, "jei");

    public static final ResourceLocation MANUAL_MILLING = new ResourceLocation(Constants.MOD_ID, "manual_milling");
    public static final ResourceLocation MILLING = new ResourceLocation(Constants.MOD_ID, "milling");
    public static final ResourceLocation MANUAL_CHOPPING = new ResourceLocation(Constants.MOD_ID, "manual_chopping");
    public static final ResourceLocation CHOPPING = new ResourceLocation(Constants.MOD_ID, "chopping");
    public static final ResourceLocation PRESS_ITEM = new ResourceLocation(Constants.MOD_ID, "pressing");
    public static final ResourceLocation PRESS_FLUID = new ResourceLocation(Constants.MOD_ID, "pressing_fluid");
    public static IRecipeManager recipeManager;

    private static boolean millingRecipePredicate(IRecipe<IInventory> recipe, AbstractHPRecipe.Type type) {
        return recipe instanceof MillingRecipe && ((MillingRecipe) recipe).getRecipeType().is(type);
    }
    private static boolean pressingRecipePredicate(IRecipe<IInventory> recipe) {
    	return recipe instanceof PressingRecipe && ((PressingRecipe) recipe).getFluidOutput() != null;
    }



    public static IJeiHelpers jeiHelpers;
    public static IGuiHelper guiHelper;
    private static IJeiRuntime jeiRuntime;

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }
    public static ICraftingGridHelper craftingGridHelper;

/*

    @Override
    public void register(IModRegistry registry) {

        jeiHelpers = registry.getJeiHelpers();
        guiHelper = jeiHelpers.getGuiHelper();
        craftingGridHelper = guiHelper.createCraftingGridHelper(1, 0);

        if (Configs.general.enableHandChoppingBlock && Configs.recipes.useSeperateChoppingRecipes) {
            registry.handleRecipes(ChoppingBlockRecipe.class, ChoppingRecipeWrapper::new, MANUAL_CHOPPING);
            registry.addRecipes(ChoppingRecipeMaker.getChoppingRecipes(jeiHelpers, true), MANUAL_CHOPPING);
        }

        registry.handleRecipes(ChoppingBlockRecipe.class, ChoppingRecipeWrapper::new, CHOPPING);
        registry.addRecipes(ChoppingRecipeMaker.getChoppingRecipes(jeiHelpers, false), CHOPPING);

        registry.handleRecipes(PressRecipe.class, PressRecipeWrapper::new, PRESS_ITEM);
        registry.handleRecipes(PressRecipe.class, PressRecipeWrapper::new, PRESS_FLUID);
        registry.addRecipes(PressRecipeMaker.getPressItemRecipes(jeiHelpers), PRESS_ITEM);
        registry.addRecipes(PressRecipeMaker.getPressFluidRecipes(jeiHelpers), PRESS_FLUID);

        registry.handleRecipes(ShapedChoppingRecipe.class, ShapedChoppingCraftingWrapper::new, VanillaRecipeCategoryUid.CRAFTING);
        registry.handleRecipes(ShapelessChoppingRecipe.class, ShapelessChoppingCraftingWrapper::new, VanillaRecipeCategoryUid.CRAFTING);


        if (Configs.general.enableHandChoppingBlock) {
            ItemStack itemStackManualChopper = BlockHPChoppingBase.createItemStack(ModBlocks.BLOCK_MANUAL_CHOPPER, 1, new ItemStack(Item.getItemFromBlock(Blocks.LOG)));
            if (Configs.recipes.useSeperateChoppingRecipes)
                registry.addRecipeCatalyst(itemStackManualChopper, MANUAL_CHOPPING);
            else
                registry.addRecipeCatalyst(itemStackManualChopper, CHOPPING);
        }
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.BLOCK_MILLSTONE), GRINDING);

        ItemStack itemStackChopper = BlockHPChoppingBase.createItemStack(ModBlocks.BLOCK_CHOPPER, 1, new ItemStack(Item.getItemFromBlock(Blocks.LOG)));
        registry.addRecipeCatalyst(itemStackChopper, CHOPPING);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.BLOCK_PRESS), PRESS_ITEM);
        registry.addRecipeCatalyst(new ItemStack(ModBlocks.BLOCK_PRESS), PRESS_FLUID);

        registry.addIngredientInfo(new ItemStack(ModBlocks.BLOCK_MILLSTONE), ItemStack.class, "info.horsepower:grindstone.info1", "info.horsepower:grindstone.info2", "info.horsepower:grindstone.info3");
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
    public void registerCategories(IRecipeCategoryRegistration registry) {
        if (Configs.recipes.useSeperateMillstoneRecipes)
            registry.addRecipeCategories(new HorsePowerGrindingCategory(registry.getJeiHelpers().getGuiHelper(), true));
        registry.addRecipeCategories(new HorsePowerGrindingCategory(registry.getJeiHelpers().getGuiHelper(), false));
        if (Configs.general.enableHandChoppingBlock && Configs.recipes.useSeperateChoppingRecipes)
            registry.addRecipeCategories(new HorsePowerChoppingCategory(registry.getJeiHelpers().getGuiHelper(), true));
        registry.addRecipeCategories(new HorsePowerChoppingCategory(registry.getJeiHelpers().getGuiHelper(), false));

        registry.addRecipeCategories(new HorsePowerPressCategory(registry.getJeiHelpers().getGuiHelper(), false));
        registry.addRecipeCategories(new HorsePowerPressCategory(registry.getJeiHelpers().getGuiHelper(), true));
    }
*/

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        jeiHelpers = registration.getJeiHelpers();
        guiHelper = jeiHelpers.getGuiHelper();
        registration.addRecipeCategories(
                new HorsePowerMillingCategory(guiHelper, false),
                new HorsePowerMillingCategory(guiHelper, true),
                new HorsePowerPressCategory(guiHelper, false),
                new HorsePowerPressCategory(guiHelper, true),
                new HorsePowerChoppingCategory(guiHelper,true)
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ClientWorld world = Minecraft.getInstance().world;
        RecipeManager minecraftRecipeManager = world.getRecipeManager();
		Collection<IRecipe<IInventory>> millingTypeRecipes = minecraftRecipeManager.getRecipes(RecipeSerializers.MILLING_TYPE).values();
        List<IRecipe<IInventory>> millingRecipes = millingTypeRecipes.stream().filter(recipe -> millingRecipePredicate(recipe, AbstractHPRecipe.Type.HORSE)).collect(Collectors.toList());
        List<IRecipe<IInventory>> manualMillingRecipes = millingTypeRecipes.stream().filter(recipe -> millingRecipePredicate(recipe, AbstractHPRecipe.Type.MANUAL)).collect(Collectors.toList());

        Collection<IRecipe<IInventory>> pressingTypeRecipes = minecraftRecipeManager.getRecipes(RecipeSerializers.PRESSING_TYPE).values();
        List<IRecipe<IInventory>> pressingFluidRecipes = pressingTypeRecipes.stream().filter(HorsePowerPlugin::pressingRecipePredicate).collect(Collectors.toList());
        List<IRecipe<IInventory>> pressingItemRecipes = pressingTypeRecipes.stream().filter(recipe -> !pressingRecipePredicate(recipe)).collect(Collectors.toList());
        registration.addRecipes(millingRecipes, MILLING);
        registration.addRecipes(manualMillingRecipes, MANUAL_MILLING);
        registration.addRecipes(pressingItemRecipes, PRESS_ITEM);
        registration.addRecipes(pressingFluidRecipes, PRESS_FLUID);
        
        Collection<IRecipe<IInventory>> choppingTypeRecipes = minecraftRecipeManager.getRecipes(RecipeSerializers.CHOPPING_TYPE).values();
        HorsePower.LOGGER.info("JEI chopping recipes: "+choppingTypeRecipes.size());
        registration.addRecipes(choppingTypeRecipes, CHOPPING);
    }


    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.MILLSTONE_BLOCK.get()), MILLING);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.manualMillstoneBlock.get()), MANUAL_MILLING);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.PRESS_BLOCK.get()), PRESS_ITEM);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.PRESS_BLOCK.get()), PRESS_FLUID);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.choppingBlock.get()), CHOPPING);
        
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        HorsePowerPlugin.jeiRuntime = jeiRuntime;
        HorsePowerPlugin.recipeManager = jeiRuntime.getRecipeManager();
    }
}
