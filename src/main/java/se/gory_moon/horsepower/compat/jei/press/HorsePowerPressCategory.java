package se.gory_moon.horsepower.compat.jei.press;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.blocks.ModBlocks;
import se.gory_moon.horsepower.compat.jei.HorsePowerCategory;
import se.gory_moon.horsepower.compat.jei.HorsePowerPlugin;
import se.gory_moon.horsepower.recipes.PressingRecipe;
import se.gory_moon.horsepower.util.Constants;
import se.gory_moon.horsepower.util.Localization;

public class HorsePowerPressCategory extends HorsePowerCategory<PressingRecipe> {

    private final String localizedName;
	private boolean isLiquid;
	
    private static final int inputSlot = 0;
    private static final int outputSlot = 1;
	
	public HorsePowerPressCategory(IGuiHelper guiHelper, boolean isLiquid) {
		super(guiHelper, false, 146, 74, new ResourceLocation(Constants.MOD_ID, isLiquid? "textures/gui/jei_fluid.png" :  "textures/gui/jei.png"));

		this.isLiquid = isLiquid;
		localizedName = isLiquid ? Localization.JEI.CATEGORY$PRESS_FLUID.translate(): Localization.JEI.CATEGORY$PRESS_ITEM.translate();
	}

	@Override
	public ResourceLocation getUid() {
		return isLiquid ? HorsePowerPlugin.PRESS_FLUID: HorsePowerPlugin.PRESS_ITEM;
	}

	@Override
	public Class<? extends PressingRecipe> getRecipeClass() {
		return PressingRecipe.class;
	}

	@Override
	public String getTitle() {
		return localizedName;
	}

	@Override
	public IDrawable getIcon() {
		return HorsePowerPlugin.guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.PRESS_BLOCK.get()));
	}

	@Override
	public void setIngredients(PressingRecipe recipe, IIngredients ingredients) {
        ingredients.setInputIngredients(recipe.getIngredients());
        ingredients.setOutputs(VanillaTypes.ITEM, Stream.of(recipe.getRecipeOutput(), recipe.getSecondaryOutput()).map(stack -> stack.isEmpty() ? null: stack).collect(Collectors.toList()));
        if(isLiquid)
            ingredients.setOutput(VanillaTypes.FLUID, recipe.getFluidOutput());
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, PressingRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
        IGuiFluidStackGroup guiFluidStack = recipeLayout.getFluidStacks();

        guiItemStacks.init(inputSlot, true, 34, 33);
        if (isLiquid)
            guiFluidStack.init(outputSlot, false, 95, 23, 16, 27, Configs.SERVER.pressTankSize.get().intValue(), true, null);
        else
            guiItemStacks.init(outputSlot, false, 90, 32);

        guiItemStacks.set(ingredients);
        if (isLiquid)
            guiFluidStack.set(ingredients);
    	super.openRecipe();
	}	
	//TODO: override getTooltipStrings and draw to draw arrows and tooltips

}