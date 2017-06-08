package se.gory_moon.horsepower.jei.grinding;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import se.gory_moon.horsepower.jei.HorsePowerPlugin;
import se.gory_moon.horsepower.util.Localization;


public class HorsePowerGrindingCategory extends BlankRecipeCategory<GrindstoneRecipeWrapper> {

    private static final int inputSlot = 0;
    private static final int outputSlot = 1;

    private final IDrawable background;
    private final String localizedName;
    private final HorseDrawable horse;

    public HorsePowerGrindingCategory(IGuiHelper guiHelper) {
        ResourceLocation location = new ResourceLocation("horsepower", "textures/gui/jei.png");
        background = guiHelper.createDrawable(location, 0, 0, 146, 74);

        IDrawableStatic horseAnim1 = guiHelper.createDrawable(location, 0, 74, 30, 20);
        IDrawableStatic horseAnim2 = guiHelper.createDrawable(location, 0, 94, 30, 20);
        IDrawableStatic horseAnim3 = guiHelper.createDrawable(location, 30, 74, 30, 20);
        IDrawableStatic horseAnim4 = guiHelper.createDrawable(location, 30, 94, 30, 20);
        horse = new HorseDrawable(guiHelper, horseAnim1, horseAnim2, horseAnim3, horseAnim4);

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
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        horse.draw(minecraft, 2, 0);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, GrindstoneRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        guiItemStacks.init(inputSlot, true, 34, 32);
        guiItemStacks.init(outputSlot, false, 90, 32);

        guiItemStacks.set(ingredients);
    }
}
