package se.gory_moon.horsepower.tweaker.recipes;
/*
import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.mc1120.recipes.MCRecipeManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.HorsePowerMod;
import se.gory_moon.horsepower.recipes.MillstoneRecipe;
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


@ZenClass("mods.horsepower.Grindstone")
public class GrindstoneRecipeTweaker {

    @ZenMethod
    public static void add(IIngredient input, IItemStack output, int time, @Optional boolean hand, @Optional IItemStack secondary, @Optional int secondaryChance) {
        AddGrindstoneRecipe recipe = new AddGrindstoneRecipe(input, output, secondary, secondaryChance, time, hand);
        TweakerPluginImpl.toAdd.add(recipe);
        TweakerPluginImpl.actions.add(recipe);
    }

    @ZenMethod
    public static void remove(IIngredient output, @Optional boolean hand) {
        RemoveGrindstoneRecipe recipe = new RemoveGrindstoneRecipe(output, hand);
        TweakerPluginImpl.toRemove.add(recipe);
        TweakerPluginImpl.actions.add(recipe);
    }

    private static class AddGrindstoneRecipe extends BaseHPAction {

        private final IIngredient input;
        private final IItemStack output;
        private final IItemStack secondary;
        private final int secondaryChance;
        private final int time;
        private final boolean hand;

        public AddGrindstoneRecipe(IIngredient input, IItemStack output2, IItemStack secondary, int secondaryChance, int time, boolean hand) {
            this.input = input;
            this.output = output2;
            this.secondary = secondary;
            this.secondaryChance = secondaryChance;
            this.time = time;
            this.hand = hand;
        }

        @Override
        public void apply() {
            if (input == null) {
                CraftTweakerAPI.logError("Input item is null");
                return;
            }
            List<IItemStack> inputs = input.getItems();
            if(inputs == null) {
                HorsePowerMod.LOGGER.error("Cannot turn " + input.toString() + " into a grinding recipe");
                return;
            }

            ItemStack[] items = getItemStacks(inputs);
            ItemStack output2 = getItemStack(output);
            ItemStack secondary2 = getItemStack(secondary);

            for (ItemStack stack: items) {
                MillstoneRecipe recipe = new MillstoneRecipe(stack, output2, secondary2, secondary2.isEmpty() ? 0: secondaryChance, time);
                HPRecipes.instance().addMillstoneRecipe(recipe, hand);
            }
        }

        @Override
        public String describe() {
            return "Adding grindstone recipe for " + input;
        }
    }

    private static class RemoveGrindstoneRecipe extends BaseHPAction {

        private final IIngredient output;
        private final boolean hand;

        public RemoveGrindstoneRecipe(IIngredient output, boolean hand) {
            this.output = output;
            this.hand = hand;
        }

        @Override
        public void apply() {
            ArrayList<MillstoneRecipe> toRemove = new ArrayList<>();
            Collection<MillstoneRecipe> recipeList = hand && Configs.recipes.useSeperateMillstoneRecipes ? HPRecipes.instance().getHandMillstoneRecipes(): HPRecipes.instance().getMillstoneRecipes();

            for (MillstoneRecipe recipe: recipeList) {
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
            return "Removing grindstone recipes for " + MCRecipeManager.saveToString(output);
        }
    }

}
*/