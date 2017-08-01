package se.gory_moon.horsepower.jei.press;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import se.gory_moon.horsepower.jei.HorsePowerCategory;
import se.gory_moon.horsepower.jei.HorsePowerPlugin;
import se.gory_moon.horsepower.lib.Reference;
import se.gory_moon.horsepower.util.Localization;

public class HorsePowerPressCategory extends HorsePowerCategory<PressRecipeWrapper> {

    private static final int inputSlot = 0;
    private static final int outputSlot = 1;

    private final String localizedName;

    public HorsePowerPressCategory(IGuiHelper guiHelper) {
        super(guiHelper, 0, false);

        localizedName = Localization.GUI.CATEGORY_PRESS.translate();
    }

    @Override
    public String getUid() {
        return HorsePowerPlugin.PRESS;
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

        guiItemStacks.init(inputSlot, true, 34, 32);
        guiItemStacks.init(outputSlot, false, 90, 32);

        guiItemStacks.set(ingredients);
        super.openRecipe();
    }
}
