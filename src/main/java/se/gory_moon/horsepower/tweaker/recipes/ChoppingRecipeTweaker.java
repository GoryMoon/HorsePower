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
import se.gory_moon.horsepower.recipes.ChoppingBlockRecipe;
import se.gory_moon.horsepower.recipes.HPRecipes;
import se.gory_moon.horsepower.tweaker.TweakerPluginImpl;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.List;

import static crafttweaker.api.minecraft.CraftTweakerMC.getItemStack;
import static crafttweaker.api.minecraft.CraftTweakerMC.getItemStacks;

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
        CraftTweakerAPI.apply(recipe);
        TweakerPluginImpl.actions.add(recipe);
    }

    @ZenMethod
    public static void remove(IIngredient output) {

        List<ChoppingBlockRecipe> toRemove = Lists.newArrayList();
        List<Integer> removeIndex = Lists.newArrayList();

        for (int i = 0; i < HPRecipes.instance().getGrindstoneRecipes().size(); i++) {
            ChoppingBlockRecipe recipe = HPRecipes.instance().getChoppingRecipes().get(i);
            if (OreDictionary.itemMatches(CraftTweakerMC.getItemStack(output), recipe.getOutput(), false)) {
                toRemove.add(recipe);
                removeIndex.add(i);
            }
        }
        RemoveChoppingRecipe recipe = new RemoveChoppingRecipe(toRemove, removeIndex);
        CraftTweakerAPI.apply(recipe);
        TweakerPluginImpl.actions.add(recipe);
    }



    private static class AddChoppingRecipe implements IAction {

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
            }
        }

        @Override
        public String describe() {
            return "Adding chopping recipe for " + ingredient;
        }
    }

    private static class RemoveChoppingRecipe implements IAction {
        private final List<Integer> removingIndices;
        private final List<ChoppingBlockRecipe> recipes;

        private RemoveChoppingRecipe(List<ChoppingBlockRecipe> recipes, List<Integer> removingIndices) {
            this.recipes = recipes;
            this.removingIndices = removingIndices;
        }

        @Override
        public void apply() {
            for (int i = this.removingIndices.size() - 1; i >= 0; --i) {
                HPRecipes.instance().getChoppingRecipes().remove(removingIndices.get(i).intValue());
            }
        }

        @Override
        public String describe() {
            return "Removing " + recipes.size() + " chopping recipes";
        }

    }
}
