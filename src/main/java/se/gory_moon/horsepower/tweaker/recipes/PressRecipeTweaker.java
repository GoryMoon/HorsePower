package se.gory_moon.horsepower.tweaker.recipes;

import com.google.common.collect.Lists;
import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import minetweaker.api.minecraft.MineTweakerMC;
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

import static minetweaker.api.minecraft.MineTweakerMC.getItemStack;
import static minetweaker.api.minecraft.MineTweakerMC.getItemStacks;

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
        MineTweakerAPI.apply(recipe);
        TweakerPluginImpl.actions.add(recipe);
    }

    @ZenMethod
    public static void remove(IIngredient output) {

        List<PressRecipe> toRemove = Lists.newArrayList();
        List<Integer> removeIndex = Lists.newArrayList();

        for (int i = 0; i < HPRecipes.instance().getGrindstoneRecipes().size(); i++) {
            PressRecipe recipe = HPRecipes.instance().getPressRecipes().get(i);
            if (OreDictionary.itemMatches(MineTweakerMC.getItemStack(output), recipe.getOutput(), false)) {
                toRemove.add(recipe);
                removeIndex.add(i);
            }
        }
        RemovePressRecipe recipe = new RemovePressRecipe(toRemove, removeIndex);
        MineTweakerAPI.apply(recipe);
        TweakerPluginImpl.actions.add(recipe);
    }



    private static class AddPressRecipe implements IUndoableAction {

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
                MineTweakerAPI.getIjeiRecipeRegistry().addRecipe(recipe, "horsepower.press");
            }
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void undo() {
            for (ItemStack stack: input) {
                PressRecipe recipe = HPRecipes.instance().getPressRecipe(stack);
                HPRecipes.instance().removePressRecipe(recipe);
                MineTweakerAPI.getIjeiRecipeRegistry().removeRecipe(recipe, "horsepower.press");
            }
            TweakerPluginImpl.actions.remove(this);
        }

        @Override
        public String describe() {
            return "Adding press recipe for " + ingredient;
        }

        @Override
        public String describeUndo() {
            return "Removing press recipe for " + ingredient;
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }

    private static class RemovePressRecipe implements IUndoableAction {
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
                MineTweakerAPI.getIjeiRecipeRegistry().removeRecipe(recipes.get(i), "horsepower.press");
            }
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void undo() {
            ArrayList<PressRecipe> recipeList = HPRecipes.instance().getPressRecipes();
            for(int i = 0; i < this.removingIndices.size(); ++i) {
                int index = Math.min(recipeList.size(), removingIndices.get(i));
                recipeList.add(index, recipes.get(i));
                MineTweakerAPI.getIjeiRecipeRegistry().addRecipe(recipes.get(i), "horsepower.press");
            }
            TweakerPluginImpl.actions.remove(this);
        }

        @Override
        public String describe() {
            return "Removing " + recipes.size() + " press recipes";
        }

        @Override
        public String describeUndo() {
            return "Restoring " + recipes.size() + " press recipes";
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }

}
