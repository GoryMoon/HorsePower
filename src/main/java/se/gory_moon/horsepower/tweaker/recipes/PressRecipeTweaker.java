package se.gory_moon.horsepower.tweaker.recipes;
/*
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import se.gory_moon.horsepower.HorsePowerMod;
import se.gory_moon.horsepower.recipes.HPRecipes;
import se.gory_moon.horsepower.recipes.PressRecipe;
import se.gory_moon.horsepower.tweaker.BaseHPAction;
import se.gory_moon.horsepower.tweaker.TweakerPluginImpl;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static crafttweaker.api.minecraft.CraftTweakerMC.*;

@ZenClass("mods.horsepower.Press")
public class PressRecipeTweaker {

    @ZenMethod
    public static void add(IIngredient input, IItemStack output) {
        AddPressRecipe recipe = new AddPressRecipe(input, output);
        TweakerPluginImpl.toAdd.add(recipe);
        TweakerPluginImpl.actions.add(recipe);
    }

    @ZenMethod
    public static void add(IIngredient input, ILiquidStack output) {
        AddPressRecipe recipe = new AddPressRecipe(input, output);
        TweakerPluginImpl.toAdd.add(recipe);
        TweakerPluginImpl.actions.add(recipe);
    }

    @ZenMethod
    public static void remove(IIngredient output) {
        RemovePressRecipe recipe = new RemovePressRecipe(output);
        TweakerPluginImpl.toRemove.add(recipe);
        TweakerPluginImpl.actions.add(recipe);
    }

    private static class AddPressRecipe extends BaseHPAction {

        private final IIngredient input;
        private final IItemStack output;
        private final ILiquidStack fluidOuput;

        public AddPressRecipe(IIngredient input, IItemStack output) {
            this.input = input;
            this.output = output;
            this.fluidOuput = null;
        }

        public AddPressRecipe(IIngredient input, ILiquidStack output) {
            this.input = input;
            this.fluidOuput = output;
            this.output = null;
        }

        @Override
        public void apply() {
            List<IItemStack> items = input.getItems();
            if(items == null) {
                HorsePowerMod.logger.error("Cannot turn " + input.toString() + " into a press recipe");
            }

            ItemStack[] items2 = getItemStacks(items);
            ItemStack output2 = getItemStack(output);
            FluidStack fluidStack = getLiquidStack(fluidOuput);

            for (ItemStack stack: items2) {
                PressRecipe recipe;
                if (fluidStack == null)
                    recipe = new PressRecipe(stack, output2, ItemStack.EMPTY, 0, 0);
                else
                    recipe = new PressRecipe(stack, fluidStack);
                HPRecipes.instance().addPressRecipe(recipe);
            }
        }

        @Override
        public String describe() {
            return "Adding press recipe for " + input;
        }
    }

    private static class RemovePressRecipe extends BaseHPAction {

        private final IIngredient output;

        public RemovePressRecipe(IIngredient output) {
            this.output = output;
        }

        @Override
        public void apply() {
            ArrayList<PressRecipe> toRemove = new ArrayList<>();
            Collection<PressRecipe> recipeList = HPRecipes.instance().getPressRecipes();

            for (PressRecipe recipe: recipeList) {
                if (OreDictionary.itemMatches(CraftTweakerMC.getItemStack(output), recipe.getOutput(), false)) {
                    toRemove.add(recipe);
                }
            }

            for(int i = toRemove.size() - 1; i >= 0; --i) {
                recipeList.remove(toRemove.get(i));
            }
        }

        @Override
        public String describe() {
            return "Removing press recipes for " + output;
        }
    }

}
*/