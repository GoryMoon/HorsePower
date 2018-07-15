package se.gory_moon.horsepower.jei.press;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.util.ResourceLocation;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.jei.HorsePowerCategory;
import se.gory_moon.horsepower.jei.HorsePowerPlugin;
import se.gory_moon.horsepower.lib.Reference;
import se.gory_moon.horsepower.util.Localization;

public class HorsePowerPressFluidCategory extends HorsePowerCategory<PressRecipeWrapper> {

    private static final int inputSlot = 0;
    private static final int outputSlot = 0;

    private final String localizedName;

    public HorsePowerPressFluidCategory(IGuiHelper guiHelper) {
        super(guiHelper, 0, false, 146, 74, new ResourceLocation("horsepower", "textures/gui/jei_fluid.png"));

        localizedName = Localization.GUI.CATEGORY_PRESS_FLUID.translate();
    }

    @Override
    public String getUid() {
        return HorsePowerPlugin.PRESS_FLUID;
    }

    @Override
    public String getTitle() {
        return localizedName;
    }

    @Override
    public String getModName() {
        return Reference.NAME;
    }


    @Override
    public void setRecipe(IRecipeLayout recipeLayout, PressRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
        IGuiFluidStackGroup guiFluidStack = recipeLayout.getFluidStacks();

        guiItemStacks.init(inputSlot, true, 34, 33);
        guiFluidStack.init(outputSlot, false, 95, 23, 16, 27, Configs.general.pressFluidTankSize, true, null);

        guiItemStacks.set(ingredients);
        guiFluidStack.set(ingredients);
        super.openRecipe();
    }
}
