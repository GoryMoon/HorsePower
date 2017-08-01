package se.gory_moon.horsepower.tweaker.recipes;

import com.google.common.collect.Lists;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import se.gory_moon.horsepower.HorsePowerMod;
import se.gory_moon.horsepower.recipes.HPRecipes;
import se.gory_moon.horsepower.recipes.PressRecipe;
import se.gory_moon.horsepower.tweaker.TweakerPluginImpl;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.List;

import static crafttweaker.api.minecraft.CraftTweakerMC.getItemStack;
import static crafttweaker.api.minecraft.CraftTweakerMC.getItemStacks;

@ZenClass("mods.horsepower.Press")
public class PressRecipeTweaker {

    @ZenMethod
    public static void add(IIngredient input, IItemStack output) {
        List<IItemStack> items = input.getItems();
        if(items == null) {
            HorsePowerMod.logger.error("Cannot turn " + input.toString() + " into a press recipe");
        }

        ItemStack[] items2 = getItemStacks(items);
        ItemStack output2 = getItemStack(output);

        AddPressRecipe recipe = new AddPressRecipe(input, items2, output2, ItemStack.EMPTY, 0, 0);
        CraftTweakerAPI.apply(recipe);
        TweakerPluginImpl.actions.add(recipe);
    }

    @ZenMethod
    public static void remove(IIngredient output) {

        List<PressRecipe> toRemove = Lists.newArrayList();
        List<Integer> removeIndex = Lists.newArrayList();

        for (int i = 0; i < HPRecipes.instance().getGrindstoneRecipes().size(); i++) {
            PressRecipe recipe = HPRecipes.instance().getPressRecipes().get(i);
            if (OreDictionary.itemMatches(CraftTweakerMC.getItemStack(output), recipe.getOutput(), false)) {
                toRemove.add(recipe);
                removeIndex.add(i);
            }
        }
        RemovePressRecipe recipe = new RemovePressRecipe(toRemove, removeIndex);
        CraftTweakerAPI.apply(recipe);
        TweakerPluginImpl.actions.add(recipe);
    }



    private static class AddPressRecipe implements IAction {

        private final IIngredient ingredient;
        private final ItemStack[] input;
        private final ItemStack output;
        private final ItemStack secondary;
        private final int secondaryChance;
        private final int time;

        public AddPressRecipe(IIngredient ingredient, ItemStack[] inputs, ItemStack output2, ItemStack secondary, int secondaryChance, int time) {
            this.ingredient = ingredient;
            this.input = inputs;
            this.output = output2;
            this.secondary = secondary;
            this.secondaryChance = secondaryChance;
            this.time = time;
        }

        @Override
        public void apply() {
            for (ItemStack stack: input) {
                PressRecipe recipe = new PressRecipe(stack, output, secondary, secondary.isEmpty() ? 0: secondaryChance, time);
                HPRecipes.instance().addPressRecipe(recipe);
            }
        }

        @Override
        public String describe() {
            return "Adding press recipe for " + ingredient;
        }
    }

    private static class RemovePressRecipe implements IAction {
        private final List<Integer> removingIndices;
        private final List<PressRecipe> recipes;

        private RemovePressRecipe(List<PressRecipe> recipes, List<Integer> removingIndices) {
            this.recipes = recipes;
            this.removingIndices = removingIndices;
        }

        @Override
        public void apply() {
            ArrayList<PressRecipe> recipeList = HPRecipes.instance().getPressRecipes();
            for(int i = this.removingIndices.size() - 1; i >= 0; --i) {
                recipeList.remove(removingIndices.get(i).intValue());
            }
        }

        @Override
        public String describe() {
            return "Removing " + recipes.size() + " press recipes";
        }
    }

}
