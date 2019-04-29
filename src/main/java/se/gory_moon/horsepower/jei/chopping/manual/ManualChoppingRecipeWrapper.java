package se.gory_moon.horsepower.jei.chopping.manual;

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
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.blocks.BlockChoppingBlock;
import se.gory_moon.horsepower.jei.HorsePowerCategory;
import se.gory_moon.horsepower.jei.HorsePowerPlugin;
import se.gory_moon.horsepower.recipes.ChoppingBlockRecipe;
import se.gory_moon.horsepower.tileentity.TileEntityManualChopper;
import se.gory_moon.horsepower.util.Localization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ManualChoppingRecipeWrapper implements IRecipeWrapper {

    private static List<ItemStack> axes = new ArrayList<>();

    private final List<ItemStack> inputs;
    private final ItemStack output;
    private final int time;
    private final IDrawableAnimated arrow;

    public ManualChoppingRecipeWrapper(ChoppingBlockRecipe recipe) {
        this(Collections.singletonList(recipe.getInput()), recipe.getOutput(), recipe.getTime());
    }

    public ManualChoppingRecipeWrapper(List<ItemStack> inputs, ItemStack output, int time) {
        this.inputs = inputs;
        this.output = output;
        this.time = time;

        IGuiHelper guiHelper = HorsePowerPlugin.guiHelper;
        IDrawableStatic arrowDrawable = guiHelper.createDrawable(HorsePowerCategory.COMPONENTS, 60, 0, 24, 17);
        arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 50, IDrawableAnimated.StartDirection.LEFT, false);
    }

    public static void setAxes() {
        axes.clear();
        for (ItemStack stack: HorsePowerPlugin.ingredientRegistry.getAllIngredients(VanillaTypes.ITEM)) {
            if (!stack.isEmpty() && BlockChoppingBlock.isValidChoppingTool(stack, null)) {
                axes.add(stack);
            }
        }
    }

    private int getChoppingAmount() {
        int mult = Configs.recipes.useSeperateChoppingRecipes ? 1: Configs.general.choppMultiplier;
        return time * mult;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        List<List<ItemStack>> inputLists = new ArrayList<>();
        List<List<ItemStack>> outputLists = new ArrayList<>();

        inputLists.add(inputs);
        inputLists.add(axes);
        List<ItemStack> outputs = new ArrayList<>(axes.size());
        for (ItemStack stack: axes) {
            ItemStack result = output.copy();
            double base = TileEntityManualChopper.getBaseAmount(stack, null) / 100D;

            result.setCount((int) Math.ceil((double) result.getCount() * base));
            outputs.add(result);
        }
        outputLists.add(outputs);

        ingredients.setInputLists(VanillaTypes.ITEM, inputLists);
        ingredients.setOutputLists(VanillaTypes.ITEM, outputLists);
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        List<String> tooltip = Lists.newArrayList();
        if (mouseX >= 23 && mouseY >= 22 && mouseX < 47 && mouseY < 39) {
            tooltip.add(Localization.GUI.JEI.MANUAL_CHOPPING.translate(getChoppingAmount()));
        }
        return tooltip;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        arrow.draw(minecraft, 23, 22);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ManualChoppingRecipeWrapper)) return false;

        ManualChoppingRecipeWrapper that = (ManualChoppingRecipeWrapper) o;
        boolean flag = true;
        for (ItemStack stack: inputs) {
            for (ItemStack stack1: that.inputs) {
                if (stack1.getMetadata() == OreDictionary.WILDCARD_VALUE && !OreDictionary.itemMatches(stack, stack1, false))
                    flag = false;
            }
        }

        return time == that.time && flag && output.equals(that.output);
    }

    @Override
    public int hashCode() {
        int result = inputs.hashCode() + 1;
        result = 31 * result + output.hashCode();
        result = 31 * result + time;
        return result;
    }
}
