package se.gory_moon.horsepower.jei.grinding;

import com.google.common.collect.Lists;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import se.gory_moon.horsepower.jei.HorsePowerCategory;
import se.gory_moon.horsepower.jei.HorsePowerPlugin;
import se.gory_moon.horsepower.recipes.GrindstoneRecipe;
import se.gory_moon.horsepower.util.Utils;
import se.gory_moon.horsepower.util.color.Colors;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GrindstoneRecipeWrapper implements IRecipeWrapper {

    private final List<List<ItemStack>> inputs;
    private final ItemStack output;
    private final ItemStack secondary;
    private final int secondaryChance;
    private final int time;
    private final double printLaps;
    private final IDrawableAnimated arrow;

    public GrindstoneRecipeWrapper(GrindstoneRecipe recipe) {
        this(Collections.singletonList(recipe.getInput()), recipe.getOutput(), recipe.getSecondary(), recipe.getSecondaryChance(), recipe.getTime());
    }

    public GrindstoneRecipeWrapper(List<ItemStack> inputs, ItemStack output, ItemStack secondary, int secondaryChance, int time) {
        this.inputs = Collections.singletonList(inputs);
        this.output = output;
        this.secondary = secondary;
        this.secondaryChance = secondaryChance;
        this.time = time;

        IGuiHelper guiHelper = HorsePowerPlugin.guiHelper;
        IDrawableStatic arrowDrawable = guiHelper.createDrawable(HorsePowerCategory.COMPONENTS, 60, 0, 24, 17);
        int laps = (int)((time / 8D) * 100);
        printLaps = (double) Math.round((time / 8D) * 100.0D) / 100.0D;
        arrow = guiHelper.createAnimatedDrawable(arrowDrawable, laps, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputLists(VanillaTypes.ITEM, inputs);
        ingredients.setOutputs(VanillaTypes.ITEM, Lists.newArrayList(output, secondary));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        arrow.draw(minecraft, 57, 27);
        minecraft.fontRenderer.drawStringWithShadow("x" + printLaps, 33, 48, Colors.WHITE.getRGB());
        if (secondaryChance > 0)
            minecraft.fontRenderer.drawString(secondaryChance + "%", 65, 58, 0x808080);
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        List<String> tooltip = Lists.newArrayList();
        if (mouseX >= 55 && mouseY >= 21 && mouseX < 80 && mouseY < 45) {
            tooltip.add("Time to grind: " + printLaps + " lap" + (printLaps >= 2D ? "s": ""));
        }
        return tooltip;
    }

    public int getSecondaryChance() {
        return secondaryChance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GrindstoneRecipeWrapper)) return false;

        GrindstoneRecipeWrapper that = (GrindstoneRecipeWrapper) o;
        boolean flag = true;
        for (ItemStack stack: inputs.get(0)) {
            for (ItemStack stack1: that.inputs.get(0)) {
                if (stack1.getMetadata() == OreDictionary.WILDCARD_VALUE && !OreDictionary.itemMatches(stack, stack1, false))
                    flag = false;
            }
        }

        return time == that.time && flag && output.equals(that.output);
    }

    @Override
    public int hashCode() {
        int result = inputs.stream().map(itemStacks -> itemStacks.stream().map(Utils::getItemStackHashCode).collect(Collectors.toList())).hashCode();
        result = 31 * result + Utils.getItemStackHashCode(output);
        result = 31 * result + Utils.getItemStackHashCode(secondary);
        result = 31 * result + secondaryChance;
        result = 31 * result + time;
        return result;
    }
}
