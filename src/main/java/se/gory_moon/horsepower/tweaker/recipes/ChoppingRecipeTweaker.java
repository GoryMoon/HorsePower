package se.gory_moon.horsepower.tweaker.recipes;
/*
import com.google.common.collect.Lists;
import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import minetweaker.api.minecraft.MineTweakerMC;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import se.gory_moon.horsepower.HorsePowerMod;
import se.gory_moon.horsepower.recipes.ChoppingBlockRecipe;
import se.gory_moon.horsepower.recipes.HPRecipes;
import se.gory_moon.horsepower.tweaker.TweakerPluginImpl;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.List;

import static minetweaker.api.minecraft.MineTweakerMC.getItemStack;
import static minetweaker.api.minecraft.MineTweakerMC.getItemStacks;

@ZenClass("mods.horsepower.ChoppingBlock")
public class ChoppingRecipeTweaker {

    @ZenMethod
    public static void add(IIngredient input, IItemStack output, int time) {
        List<IItemStack> items = input.getItems();
        if(items == null) {
            HorsePowerMod.logger.error("Cannot turn " + input.toString() + " into a chopping recipe");
        }

        ItemStack[] items2 = getItemStacks(items);
        ItemStack output2 = getItemStack(output);

        AddChoppingRecipe recipe = new AddChoppingRecipe(input, items2, output2, ItemStack.EMPTY, 0, time);
        MineTweakerAPI.apply(recipe);
        TweakerPluginImpl.actions.add(recipe);
    }

    @ZenMethod
    public static void remove(IIngredient output) {

        List<ChoppingBlockRecipe> toRemove = Lists.newArrayList();
        List<Integer> removeIndex = Lists.newArrayList();

        for (int i = 0; i < HPRecipes.instance().getGrindstoneRecipes().size(); i++) {
            ChoppingBlockRecipe recipe = HPRecipes.instance().getChoppingRecipes().get(i);
            if (OreDictionary.itemMatches(MineTweakerMC.getItemStack(output), recipe.getOutput(), false)) {
                toRemove.add(recipe);
                removeIndex.add(i);
            }
        }
        RemoveChoppingRecipe recipe = new RemoveChoppingRecipe(toRemove, removeIndex);
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

        public AddChoppingRecipe(IIngredient ingredient, ItemStack[] inputs, ItemStack output2, ItemStack secondary, int secondaryChance, int time) {
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
                ChoppingBlockRecipe recipe = new ChoppingBlockRecipe(stack, output, secondary, secondary.isEmpty() ? 0: secondaryChance, time);
                HPRecipes.instance().addChoppingRecipe(recipe);
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
                ChoppingBlockRecipe recipe = HPRecipes.instance().getChoppingBlockRecipe(stack);
                HPRecipes.instance().removeChoppingRecipe(recipe);
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

        private RemoveChoppingRecipe(List<ChoppingBlockRecipe> recipes, List<Integer> removingIndices) {
            this.recipes = recipes;
            this.removingIndices = removingIndices;
        }

        @Override
        public void apply() {
            for(int i = this.removingIndices.size() - 1; i >= 0; --i) {
                HPRecipes.instance().getChoppingRecipes().remove(removingIndices.get(i).intValue());
                MineTweakerAPI.getIjeiRecipeRegistry().removeRecipe(recipes.get(i), "horsepower.chopping");
            }
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void undo() {
            for(int i = 0; i < this.removingIndices.size(); ++i) {
                int index = Math.min(HPRecipes.instance().getGrindstoneRecipes().size(), removingIndices.get(i));
                HPRecipes.instance().getChoppingRecipes().add(index, recipes.get(i));
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
*/