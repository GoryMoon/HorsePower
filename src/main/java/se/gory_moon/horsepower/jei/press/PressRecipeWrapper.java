package se.gory_moon.horsepower.jei.press;
/*
import com.google.common.collect.Lists;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.jei.HorsePowerCategory;
import se.gory_moon.horsepower.jei.HorsePowerPlugin;
import se.gory_moon.horsepower.recipes.PressRecipe;
import se.gory_moon.horsepower.util.color.Colors;

import java.util.Collections;
import java.util.List;

public class PressRecipeWrapper extends BlankRecipeWrapper {

    private final List<List<ItemStack>> inputs;
    private final ItemStack output;
    private final FluidStack fluidOutput;

    private final double printLaps;
    private final IDrawableAnimated arrow;
    public final boolean isFluid;

    public PressRecipeWrapper(PressRecipe recipe) {
        this(Collections.singletonList(recipe.getInput()), recipe.getOutput(), recipe.getOutputFluid());
    }

    public PressRecipeWrapper(List<ItemStack> inputs, ItemStack output, FluidStack fluidOutput) {
        this.inputs = Collections.singletonList(inputs);
        this.output = output;
        this.fluidOutput = fluidOutput;
        this.isFluid = fluidOutput != null;

        IGuiHelper guiHelper = HorsePowerPlugin.guiHelper;
        IDrawableStatic arrowDrawable = guiHelper.createDrawable(HorsePowerCategory.COMPONENTS, 60, 0, 24, 17);
        double time = (double)(Configs.general.pointsForPress > 0 ? Configs.general.pointsForPress: 1);
        int laps = (int)((time / 8D) * 100);
        printLaps = (double) Math.round((time / 8D) * 100.0D) / 100.0D;
        arrow = guiHelper.createAnimatedDrawable(arrowDrawable, laps, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputLists(ItemStack.class, inputs);
        if (isFluid)
            ingredients.setOutput(FluidStack.class, fluidOutput);
        else
            ingredients.setOutput(ItemStack.class, output);
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        List<String> tooltip = Lists.newArrayList();
        if (mouseX >= 55 && mouseY >= 21 && mouseX < 80 && mouseY < 33) {
            tooltip.add("Time to press: " + printLaps + " lap" + (printLaps >= 2D ? "s": ""));
        }
        return tooltip;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        arrow.draw(minecraft, isFluid ? 62: 57, 32);
        minecraft.fontRenderer.drawStringWithShadow("x" + printLaps, 58, 23, Colors.WHITE.getRGB());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PressRecipeWrapper)) return false;

        PressRecipeWrapper that = (PressRecipeWrapper) o;
        boolean flag = true;
        for (ItemStack stack: inputs.get(0)) {
            for (ItemStack stack1: that.inputs.get(0)) {
                if (stack1.getMetadata() == OreDictionary.WILDCARD_VALUE && !OreDictionary.itemMatches(stack, stack1, false))
                    flag = false;
            }
        }

        return flag && (output != null && that.output != null && output.equals(that.output) || (fluidOutput != null && that.fluidOutput != null && fluidOutput.equals(that.fluidOutput)));
    }

    @Override
    public int hashCode() {
        int result = inputs.hashCode();
        result = 31 * result + output.hashCode();
        return result;
    }
}
*/