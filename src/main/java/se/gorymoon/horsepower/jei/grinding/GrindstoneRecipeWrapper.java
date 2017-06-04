package se.gorymoon.horsepower.jei.grinding;

import com.google.common.collect.Lists;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import se.gorymoon.horsepower.jei.HorsePowerPlugin;
import se.gorymoon.horsepower.recipes.GrindstoneRecipe;
import se.gorymoon.horsepower.util.Colors;

import java.util.Collections;
import java.util.List;

public class GrindstoneRecipeWrapper extends BlankRecipeWrapper {

    private final List<List<ItemStack>> inputs;
    private final ItemStack output;
    private final int time;
    private final double printLaps;
    private final IDrawableAnimated arrow;

    public GrindstoneRecipeWrapper(GrindstoneRecipe recipe) {
        this(Collections.singletonList(recipe.getInput()), recipe.getOutput(), recipe.getTime());
    }

    public GrindstoneRecipeWrapper(List<ItemStack> inputs, ItemStack output, int time) {
        this.inputs = Collections.singletonList(inputs);
        this.output = output;
        this.time = time;

        IGuiHelper guiHelper = HorsePowerPlugin.guiHelper;
        ResourceLocation location = new ResourceLocation("horsepower", "textures/gui/jei.png");
        IDrawableStatic arrowDrawable = guiHelper.createDrawable(location, 146, 0, 24, 17);
        int laps = (int)((time / 8D) * 100);
        printLaps = (double) Math.round((time / 8D) * 100.0D) / 100.0D;
        arrow = guiHelper.createAnimatedDrawable(arrowDrawable, laps, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputLists(ItemStack.class, inputs);
        ingredients.setOutput(ItemStack.class, output);
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        arrow.draw(minecraft, 57, 32);
        minecraft.fontRendererObj.drawStringWithShadow("x" + printLaps, 58, 23, Colors.WHITE.getRGB());
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        List<String> tooltip = Lists.newArrayList();
        if (mouseX >= 55 && mouseY >= 21 && mouseX < 80 && mouseY < 33) {
            tooltip.add("Time to grind: " + printLaps + " lap" + (printLaps >= 2D ? "s": ""));
        }
        return tooltip;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GrindstoneRecipeWrapper)) return false;

        GrindstoneRecipeWrapper that = (GrindstoneRecipeWrapper) o;

        return time == that.time && inputs.equals(that.inputs) && output.equals(that.output);
    }

    @Override
    public int hashCode() {
        int result = inputs.hashCode();
        result = 31 * result + output.hashCode();
        result = 31 * result + time;
        return result;
    }
}
