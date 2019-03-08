package se.gory_moon.horsepower.tweaker.recipes;

import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.HorsePowerMod;
import se.gory_moon.horsepower.recipes.ChoppingBlockRecipe;
import se.gory_moon.horsepower.recipes.HPRecipes;
import se.gory_moon.horsepower.tweaker.BaseHPAction;
import se.gory_moon.horsepower.tweaker.TweakerPluginImpl;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static crafttweaker.api.minecraft.CraftTweakerMC.getItemStack;
import static crafttweaker.api.minecraft.CraftTweakerMC.getItemStacks;

@ZenClass("mods.horsepower.ChoppingBlock")
public class ChoppingRecipeTweaker {

    @ZenMethod
    public static void add(IIngredient input, IItemStack output, int time, @Optional boolean hand) {
        AddChoppingRecipe recipe = new AddChoppingRecipe(input, output, ItemStack.EMPTY, 0, time, hand);
        TweakerPluginImpl.toAdd.add(recipe);
        TweakerPluginImpl.actions.add(recipe);
    }

    @ZenMethod
    public static void remove(IIngredient output, @Optional boolean hand) {
        RemoveChoppingRecipe recipe = new RemoveChoppingRecipe(output, hand);
        TweakerPluginImpl.toRemove.add(recipe);
        TweakerPluginImpl.actions.add(recipe);
    }

    private static class AddChoppingRecipe extends BaseHPAction {

        private final IIngredient input;
        private final IItemStack output;
        private final ItemStack secondary;
        private final int secondaryChance;
        private final int time;
        private final boolean hand;

        public AddChoppingRecipe(IIngredient input, IItemStack output, ItemStack secondary, int secondaryChance, int time, boolean hand) {
            this.input = input;
            this.output = output;
            this.secondary = secondary;
            this.secondaryChance = secondaryChance;
            this.time = time;
            this.hand = hand;
        }

        @Override
        public void apply() {
            List<IItemStack> items = input.getItems();
            if(items == null) {
                HorsePowerMod.logger.error("Cannot turn " + input.toString() + " into a chopping recipe");
                return;
            }

            ItemStack[] items2 = getItemStacks(items);
            ItemStack output2 = getItemStack(output);

            for (ItemStack stack: items2) {
                ChoppingBlockRecipe recipe = new ChoppingBlockRecipe(stack, output2, secondary, secondary.isEmpty() ? 0: secondaryChance, time);
                HPRecipes.instance().addChoppingRecipe(recipe, hand);
            }
        }

        @Override
        public String describe() {
            return "Adding chopping recipe for " + input;
        }
    }

    private static class RemoveChoppingRecipe extends BaseHPAction {

        private final IIngredient output;
        private final boolean hand;

        private RemoveChoppingRecipe(IIngredient output, boolean hand) {
            this.output = output;
            this.hand = hand;
        }

        @Override
        public void apply() {
            ArrayList<ChoppingBlockRecipe> toRemove = new ArrayList<>();

            Collection<ChoppingBlockRecipe> recipeList = hand && Configs.recipes.useSeperateChoppingRecipes ? HPRecipes.instance().getManualChoppingRecipes(): HPRecipes.instance().getChoppingRecipes();

            for (ChoppingBlockRecipe recipe: recipeList) {
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
            return "Removing chopping recipes for " + output;
        }

    }
}
