package se.gory_moon.horsepower.tweaker.recipes;

import com.google.common.collect.Lists;
import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import minetweaker.api.minecraft.MineTweakerMC;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.HorsePowerMod;
import se.gory_moon.horsepower.recipes.ChoppingBlockRecipe;
import se.gory_moon.horsepower.recipes.HPRecipes;
import se.gory_moon.horsepower.tweaker.TweakerPluginImpl;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.List;

import static minetweaker.api.minecraft.MineTweakerMC.getItemStack;
import static minetweaker.api.minecraft.MineTweakerMC.getItemStacks;

@ZenClass("mods.horsepower.ChoppingBlock")
public class ChoppingRecipeTweaker {

    @ZenMethod
    public static void add(IIngredient input, IItemStack output, int time, @Optional boolean hand) {
        List<IItemStack> items = input.getItems();
        if(items == null) {
            HorsePowerMod.logger.error("Cannot turn " + input.toString() + " into a chopping recipe");
        }

        ItemStack[] items2 = getItemStacks(items);
        ItemStack output2 = getItemStack(output);

        AddChoppingRecipe recipe = new AddChoppingRecipe(input, items2, output2, ItemStack.EMPTY, 0, time, hand);
        MineTweakerAPI.apply(recipe);
        TweakerPluginImpl.actions.add(recipe);
    }

    @ZenMethod
    public static void remove(IIngredient output, @Optional boolean hand) {

        List<ChoppingBlockRecipe> toRemove = Lists.newArrayList();
        List<Integer> removeIndex = Lists.newArrayList();

        for (int i = 0; i < HPRecipes.instance().getGrindstoneRecipes().size(); i++) {
            ChoppingBlockRecipe recipe = HPRecipes.instance().getChoppingRecipes().get(i);
            if (OreDictionary.itemMatches(MineTweakerMC.getItemStack(output), recipe.getOutput(), false)) {
                toRemove.add(recipe);
                removeIndex.add(i);
            }
        }
        RemoveChoppingRecipe recipe = new RemoveChoppingRecipe(toRemove, removeIndex, hand);
        MineTweakerAPI.apply(recipe);
        TweakerPluginImpl.actions.add(recipe);
    }



    private static class AddChoppingRecipe implements IUndoableAction {

        private final IIngredient ingredient;
        private final ItemStack[] input;
        private final ItemStack output;
        private final ItemStack secondary;
        private final int secondaryChance;
        private final int time;
        private final boolean hand;

        public AddChoppingRecipe(IIngredient ingredient, ItemStack[] inputs, ItemStack output2, ItemStack secondary, int secondaryChance, int time, boolean hand) {
            this.ingredient = ingredient;
            this.input = inputs;
            this.output = output2;
            this.secondary = secondary;
            this.secondaryChance = secondaryChance;
            this.time = time;
            this.hand = hand;
        }

        @Override
        public void apply() {
            for (ItemStack stack: input) {
                ChoppingBlockRecipe recipe = new ChoppingBlockRecipe(stack, output, secondary, secondary.isEmpty() ? 0: secondaryChance, time);
                HPRecipes.instance().addChoppingRecipe(recipe, hand);
                MineTweakerAPI.getIjeiRecipeRegistry().addRecipe(recipe, "horsepower.chopping");
            }
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void undo() {
            for (ItemStack stack: input) {
                ChoppingBlockRecipe recipe = HPRecipes.instance().getChoppingBlockRecipe(stack, hand);
                HPRecipes.instance().removeChoppingRecipe(recipe, hand);
                MineTweakerAPI.getIjeiRecipeRegistry().removeRecipe(recipe, "horsepower.chopping");
            }
            TweakerPluginImpl.actions.remove(this);
        }

        @Override
        public String describe() {
            return "Adding chopping recipe for " + ingredient;
        }

        @Override
        public String describeUndo() {
            return "Removing chopping recipe for " + ingredient;
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }

    private static class RemoveChoppingRecipe implements IUndoableAction {
        private final List<Integer> removingIndices;
        private final List<ChoppingBlockRecipe> recipes;
        private final boolean hand;

        private RemoveChoppingRecipe(List<ChoppingBlockRecipe> recipes, List<Integer> removingIndices, boolean hand) {
            this.recipes = recipes;
            this.removingIndices = removingIndices;
            this.hand = hand;
        }

        @Override
        public void apply() {
            ArrayList<ChoppingBlockRecipe> recipeList = hand && Configs.recipes.useSeperateChoppingRecipes ? HPRecipes.instance().getManualChoppingRecipes(): HPRecipes.instance().getChoppingRecipes();
            for(int i = this.removingIndices.size() - 1; i >= 0; --i) {
                recipeList.remove(removingIndices.get(i).intValue());
                MineTweakerAPI.getIjeiRecipeRegistry().removeRecipe(recipes.get(i), "horsepower.chopping");
            }
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void undo() {
            ArrayList<ChoppingBlockRecipe> recipeList = hand && Configs.recipes.useSeperateChoppingRecipes ? HPRecipes.instance().getManualChoppingRecipes(): HPRecipes.instance().getChoppingRecipes();
            for(int i = 0; i < this.removingIndices.size(); ++i) {
                int index = Math.min(recipeList.size(), removingIndices.get(i));
                recipeList.add(index, recipes.get(i));
                MineTweakerAPI.getIjeiRecipeRegistry().addRecipe(recipes.get(i), "horsepower.chopping");
            }
            TweakerPluginImpl.actions.remove(this);
        }

        @Override
        public String describe() {
            return "Removing " + recipes.size() + " chopping recipes";
        }

        @Override
        public String describeUndo() {
            return "Restoring " + recipes.size() + " chopping recipes";
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }

}
