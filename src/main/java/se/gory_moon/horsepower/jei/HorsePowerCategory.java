package se.gory_moon.horsepower.jei;
/*
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import se.gory_moon.horsepower.util.color.Colors;

import java.util.List;
import java.util.Random;
import java.util.UUID;

public abstract class HorsePowerCategory<T extends IRecipeWrapper> extends BlankRecipeCategory<T> {

    protected IDrawable background;
    protected HorseDrawable currentDrawable;
    protected HorseDrawable horse;
    protected HorseDrawable hedgehog;
    protected HorseDrawable character;

    public static final ResourceLocation COMPONENTS = new ResourceLocation("horsepower", "textures/gui/COMPONENTS.png");

    public HorsePowerCategory(IGuiHelper guiHelper) {
        this(guiHelper, false, 146, 74, new ResourceLocation("horsepower", "textures/gui/jei.png"));
    }

    public HorsePowerCategory(IGuiHelper guiHelper, boolean grinding, int width, int height, ResourceLocation location) {
        background = guiHelper.createDrawable(location, 0, 0, width, height);
        horse = getHorseDrawable(guiHelper, 0, 20, 100, grinding, null);
        hedgehog = getHorseDrawable(guiHelper, 40, 5, 35, grinding, Colors.LIGHTBLUE + "Sonic!");
        character = getHorseDrawable(guiHelper, 80, 10, 100, grinding, Colors.PURPLE + "It's mini you, Darkosto!\n" + Colors.LIGHTBLUE + "Happy birthday!");
    }

    private static HorseDrawable getHorseDrawable(IGuiHelper guiHelper, int y, int animCycle, int pathCycle, boolean grinding, String hovering) {
        IDrawableStatic horseAnim1 = guiHelper.createDrawable(COMPONENTS, 0, y, 30, 20);
        IDrawableStatic horseAnim2 = guiHelper.createDrawable(COMPONENTS, 0, y + 20, 30, 20);
        IDrawableStatic horseAnim3 = guiHelper.createDrawable(COMPONENTS, 30, y, 30, 20);
        IDrawableStatic horseAnim4 = guiHelper.createDrawable(COMPONENTS, 30, y + 20, 30, 20);
        ITickTimer animTimer = guiHelper.createTickTimer(animCycle, 1, false);
        ITickTimer pathTimer = guiHelper.createTickTimer(pathCycle, grinding ? 352: 324, false);
        return new HorseDrawable(guiHelper, horseAnim1, horseAnim2, horseAnim3, horseAnim4, animTimer, pathTimer, grinding, hovering);
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        currentDrawable.draw(minecraft, 2, 0);
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        return currentDrawable.getTooltipStrings(mouseX, mouseY);
    }

    protected void openRecipe() {
        currentDrawable = horse;
        Random rand = Minecraft.getMinecraft().world.rand;

        if (rand.nextInt(100) <= 10 && UUID.fromString("10755ea6-9721-467a-8b5c-92adf689072c").equals(Minecraft.getMinecraft().player.getGameProfile().getId()))
            currentDrawable = character;
        else if (rand.nextInt(3000) <= 50 && Loader.isModLoaded("animania"))
            currentDrawable = hedgehog;
    }
}
*/