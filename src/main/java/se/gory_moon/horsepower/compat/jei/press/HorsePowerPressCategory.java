package se.gory_moon.horsepower.compat.jei.press;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.Registration;
import se.gory_moon.horsepower.compat.jei.HorsePowerCategory;
import se.gory_moon.horsepower.compat.jei.HorsePowerPlugin;
import se.gory_moon.horsepower.recipes.MillingRecipe;
import se.gory_moon.horsepower.recipes.PressingRecipe;
import se.gory_moon.horsepower.util.Constants;
import se.gory_moon.horsepower.util.Localization;

public class HorsePowerPressCategory extends HorsePowerCategory<PressingRecipe> {

    private final String localizedName;
	private boolean isLiquid;
	
    private static final int inputSlot = 0;
    private static final int outputSlot = 1;
	
    private final IDrawableAnimated arrow;
    
	public HorsePowerPressCategory(IGuiHelper guiHelper, boolean isLiquid) {
		super(guiHelper, false, 146, 74, new ResourceLocation(Constants.MOD_ID, isLiquid? "textures/gui/jei_fluid.png" :  "textures/gui/jei.png"));

		this.isLiquid = isLiquid;
		localizedName = isLiquid ? Localization.JEI.CATEGORY$PRESS_FLUID.translate(): Localization.JEI.CATEGORY$PRESS_ITEM.translate();
	
		arrow = guiHelper.drawableBuilder(HorsePowerCategory.COMPONENTS, 60, 0, 24, 17)
	                .buildAnimated(150, IDrawableAnimated.StartDirection.LEFT, false);
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
		return HorsePowerPlugin.guiHelper.createDrawableIngredient(new ItemStack(Registration.PRESS_BLOCK.get()));
	}

	@Override
	public void setIngredients(PressingRecipe recipe, IIngredients ingredients) {
	    List<ItemStack> inputIngredients = recipe.getIngredients().stream().flatMap(i -> Stream.of(i.getMatchingStacks())).distinct().map(stack -> {stack.setCount(recipe.getInputCount());return stack;}).collect(Collectors.toList());
	    ingredients.setInputLists(VanillaTypes.ITEM,Arrays.asList(inputIngredients));
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
	
	@Override
	public void draw(PressingRecipe recipe, double mouseX, double mouseY) {
	    super.draw(recipe, mouseX, mouseY);
	    arrow.draw(isLiquid ? 61 : 57, 32);
	}
	
    @Override
    public List<String> getTooltipStrings(PressingRecipe recipe, double mouseX, double mouseY) {
        List<String> tooltip = super.getTooltipStrings(recipe, mouseX, mouseY);
        if (mouseX >= 55 && mouseY >= 25 && mouseX < 86 && mouseY < 50) {
            double printLaps = Math.round((Configs.SERVER.pointsPerPress.get().intValue() / 8D) * 100.0D) / 100.0D;
            tooltip.add(new TranslationTextComponent("info.horsepower.horse.pressing.time", Double.valueOf(printLaps)).getFormattedText());
        }
        return tooltip;
    }

}