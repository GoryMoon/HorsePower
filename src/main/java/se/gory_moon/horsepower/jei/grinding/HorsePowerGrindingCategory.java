package se.gory_moon.horsepower.jei.grinding;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.util.ResourceLocation;
import se.gory_moon.horsepower.jei.HorseDrawable;
import se.gory_moon.horsepower.jei.HorsePowerCategory;
import se.gory_moon.horsepower.jei.HorsePowerPlugin;
import se.gory_moon.horsepower.lib.Reference;
import se.gory_moon.horsepower.util.Localization;


public class HorsePowerGrindingCategory extends HorsePowerCategory<GrindstoneRecipeWrapper> {

    private static final int inputSlot = 0;
    private static final int outputSlot = 1;
    private static final int secondarySlot = 2;

    private final String localizedName;

    public HorsePowerGrindingCategory(IGuiHelper guiHelper) {
        super(guiHelper);
        ResourceLocation location = new ResourceLocation("horsepower", "textures/gui/jei_grindstone.png");
        background = guiHelper.createDrawable(location, 0, 0, 146, 85);

        IDrawableStatic horseAnim1 = guiHelper.createDrawable(location, 0, 90, 30, 20);
        IDrawableStatic horseAnim2 = guiHelper.createDrawable(location, 0, 110, 30, 20);
        IDrawableStatic horseAnim3 = guiHelper.createDrawable(location, 30, 90, 30, 20);
        IDrawableStatic horseAnim4 = guiHelper.createDrawable(location, 30, 110, 30, 20);
        horse = new HorseDrawable(guiHelper, horseAnim1, horseAnim2, horseAnim3, horseAnim4, true);

        localizedName = Localization.GUI.CATEGORY_GRINDING.translate();
    }

    @Override
    public String getUid() {
        return HorsePowerPlugin.GRINDING;
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
    public void setRecipe(IRecipeLayout recipeLayout, GrindstoneRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        guiItemStacks.init(inputSlot, true, 34, 27);
        guiItemStacks.init(outputSlot, false, 90, 27);
        guiItemStacks.init(secondarySlot, false, 90, 50);
        guiItemStacks.addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {
            if (slotIndex == secondarySlot && !ingredient.isEmpty()) {
                tooltip.add(tooltip.size()-1,  "Chance: " + recipeWrapper.getSecondaryChance() + "%");
            }
        });

        guiItemStacks.set(ingredients);
    }
}
