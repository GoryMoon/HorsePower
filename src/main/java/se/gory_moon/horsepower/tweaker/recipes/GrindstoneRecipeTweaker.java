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
import se.gory_moon.horsepower.recipes.GrindstoneRecipe;
import se.gory_moon.horsepower.recipes.HPRecipes;
import se.gory_moon.horsepower.tweaker.TweakerPluginImpl;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.List;

import static minetweaker.api.minecraft.MineTweakerMC.getItemStack;
import static minetweaker.api.minecraft.MineTweakerMC.getItemStacks;

@ZenClass("mods.horsepower.Grindstone")
public class GrindstoneRecipeTweaker {

    @ZenMethod
    public static void add(IIngredient input, IItemStack output, int time) {
        List<IItemStack> items = input.getItems();
        if(items == null) {
            HorsePowerMod.logger.error("Cannot turn " + input.toString() + " into a grinding recipe");
        }

        ItemStack[] items2 = getItemStacks(items);
        ItemStack output2 = getItemStack(output);

        AddGrindstoneRecipe recipe = new AddGrindstoneRecipe(input, items2, output2, time);
        MineTweakerAPI.apply(recipe);
        TweakerPluginImpl.actions.add(recipe);
    }

    @ZenMethod
    public static void remove(IIngredient output) {

        List<GrindstoneRecipe> toRemove = Lists.newArrayList();
        List<Integer> removeIndex = Lists.newArrayList();

        for (int i = 0; i < HPRecipes.instance().getGrindstoneRecipes().size(); i++) {
            GrindstoneRecipe recipe = HPRecipes.instance().getGrindstoneRecipes().get(i);
            if (OreDictionary.itemMatches(MineTweakerMC.getItemStack(output), recipe.getOutput(), false)) {
                toRemove.add(recipe);
                removeIndex.add(i);
            }
        }
        RemoveGrindstoneRecipe recipe = new RemoveGrindstoneRecipe(toRemove, removeIndex);
        MineTweakerAPI.apply(recipe);
        TweakerPluginImpl.actions.add(recipe);
    }

    private static class AddGrindstoneRecipe implements IUndoableAction {

        private final IIngredient ingredient;
        private final ItemStack[] input;
        private final ItemStack output;
        private final int time;

        public AddGrindstoneRecipe(IIngredient ingredient, ItemStack[] inputs, ItemStack output2, int time) {
            this.ingredient = ingredient;
            this.input = inputs;
            this.output = output2;
            this.time = time;
        }

        @Override
        public void apply() {
            for (ItemStack stack: input) {
                GrindstoneRecipe recipe = new GrindstoneRecipe(stack, output, time);
                HPRecipes.instance().addGrindstoneRecipe(recipe);
                MineTweakerAPI.getIjeiRecipeRegistry().addRecipe(recipe, "horsepower.grinding");
            }
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void undo() {
            for (ItemStack stack: input) {
                GrindstoneRecipe recipe = new GrindstoneRecipe(stack, output, time);
                HPRecipes.instance().removeGrindstoneRecipe(recipe);
                MineTweakerAPI.getIjeiRecipeRegistry().removeRecipe(recipe, "horsepower.grinding");
            }
            TweakerPluginImpl.actions.remove(this);
        }

        @Override
        public String describe() {
            return "Adding grindstone recipe for " + ingredient;
        }

        @Override
        public String describeUndo() {
            return "Removing grindstone recipe for " + ingredient;
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }

    private static class RemoveGrindstoneRecipe implements IUndoableAction {
        private final List<Integer> removingIndices;
        private final List<GrindstoneRecipe> recipes;

        private RemoveGrindstoneRecipe(List<GrindstoneRecipe> recipes, List<Integer> removingIndices) {
            this.recipes = recipes;
            this.removingIndices = removingIndices;
        }

        @Override
        public void apply() {
            for(int i = this.removingIndices.size() - 1; i >= 0; --i) {
                HPRecipes.instance().getGrindstoneRecipes().remove(removingIndices.get(i).intValue());
                MineTweakerAPI.getIjeiRecipeRegistry().removeRecipe(recipes.get(i), "horsepower.grinding");
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
                HPRecipes.instance().getGrindstoneRecipes().add(index, recipes.get(i));
                MineTweakerAPI.getIjeiRecipeRegistry().addRecipe(recipes.get(i), "horsepower.grinding");
            }
            TweakerPluginImpl.actions.remove(this);
        }

        @Override
        public String describe() {
            return "Removing " + recipes.size() + " grindstone recipes";
        }

        @Override
        public String describeUndo() {
            return "Restoring " + recipes.size() + " grindstone recipes";
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }

}
*/