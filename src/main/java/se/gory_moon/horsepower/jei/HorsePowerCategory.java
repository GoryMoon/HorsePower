package se.gory_moon.horsepower.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public abstract class HorsePowerCategory<T extends IRecipeWrapper> extends BlankRecipeCategory<T> {

    protected IDrawable background;
    protected HorseDrawable horse;

    public HorsePowerCategory(IGuiHelper guiHelper) {
        ResourceLocation location = new ResourceLocation("horsepower", "textures/gui/jei.png");
        background = guiHelper.createDrawable(location, 0, 0, 146, 74);

        IDrawableStatic horseAnim1 = guiHelper.createDrawable(location, 0, 74, 30, 20);
        IDrawableStatic horseAnim2 = guiHelper.createDrawable(location, 0, 94, 30, 20);
        IDrawableStatic horseAnim3 = guiHelper.createDrawable(location, 30, 74, 30, 20);
        IDrawableStatic horseAnim4 = guiHelper.createDrawable(location, 30, 94, 30, 20);
        horse = new HorseDrawable(guiHelper, horseAnim1, horseAnim2, horseAnim3, horseAnim4, false);
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        horse.draw(minecraft, 2, 0);
    }
}
