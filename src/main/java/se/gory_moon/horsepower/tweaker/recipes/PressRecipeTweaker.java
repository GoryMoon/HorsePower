package se.gory_moon.horsepower.tweaker.recipes;

import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
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

import static crafttweaker.api.minecraft.CraftTweakerMC.getItemStack;
import static crafttweaker.api.minecraft.CraftTweakerMC.getItemStacks;

@ZenClass("mods.horsepower.Press")
public class PressRecipeTweaker {

    @ZenMethod
    public static void add(IIngredient input, IItemStack output) {
        AddPressRecipe recipe = new AddPressRecipe(input, output, ItemStack.EMPTY, 0, 0);
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
        private final ItemStack secondary;
        private final int secondaryChance;
        private final int time;

        public AddPressRecipe(IIngredient input, IItemStack output2, ItemStack secondary, int secondaryChance, int time) {
            this.input = input;
            this.output = output2;
            this.secondary = secondary;
            this.secondaryChance = secondaryChance;
            this.time = time;
        }

        @Override
        public void apply() {
            List<IItemStack> items = input.getItems();
            if(items == null) {
                HorsePowerMod.logger.error("Cannot turn " + input.toString() + " into a press recipe");
            }

            ItemStack[] items2 = getItemStacks(items);
            ItemStack output2 = getItemStack(output);

            for (ItemStack stack: items2) {
                PressRecipe recipe = new PressRecipe(stack, output2, secondary, secondary.isEmpty() ? 0: secondaryChance, time);
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
