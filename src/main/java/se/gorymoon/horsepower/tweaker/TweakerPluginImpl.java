package se.gorymoon.horsepower.tweaker;

import com.google.common.collect.Lists;
import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.MineTweakerImplementationAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.minecraft.MineTweakerMC;
import minetweaker.util.IEventHandler;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import se.gorymoon.horsepower.recipes.GrindstoneRecipes;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ZenClass("mods.horsepower")
public class TweakerPluginImpl implements ITweakerPlugin, IEventHandler<MineTweakerImplementationAPI.ReloadEvent> {

    public TweakerPluginImpl() {
        MineTweakerImplementationAPI.onReloadEvent(this);
    }

    private static List<IUndoableAction> actions = Lists.newArrayList();

    @Override
    public void applyTweaker() {
        for (IUndoableAction action: actions)
            action.apply();
    }

    @Override
    public void register() {
        MineTweakerAPI.registerClass(TweakerPluginImpl.class);
    }

    @ZenMethod
    public static void addGrindstoneRecipe(IIngredient input, IIngredient output, int time) {
        AddGrindstoneRecipe recipe = new AddGrindstoneRecipe(input, output, time);
        MineTweakerAPI.apply(recipe);
        actions.add(recipe);
    }

    @ZenMethod
    public static void removeGrindstoneReicpe(IIngredient output) {

        List<ItemStack> toRemove = new ArrayList();
        List<ItemStack> toRemoveValues = new ArrayList();
        List<Integer> timeToRemove = new ArrayList();

        for (Map.Entry<ItemStack, ItemStack> entry: GrindstoneRecipes.instance().getGrindstoneList().entrySet()) {
            if (OreDictionary.itemMatches(MineTweakerMC.getItemStack(output), entry.getKey(), false)) {
                toRemove.add(entry.getKey());
                toRemoveValues.add(entry.getValue());
                timeToRemove.add(GrindstoneRecipes.instance().getGrindstoneTime(entry.getKey()));
            }
        }
        RemoveGrindstoneRecipe recipe = new RemoveGrindstoneRecipe(toRemove, toRemoveValues, timeToRemove);
        MineTweakerAPI.apply(recipe);
        actions.add(recipe);
    }

    @Override
    public void handle(MineTweakerImplementationAPI.ReloadEvent reloadEvent) {
        actions.clear();
    }

    private static class AddGrindstoneRecipe implements IUndoableAction {

        private final IIngredient output;
        private final IIngredient input;
        private final int time;

        private AddGrindstoneRecipe(IIngredient input, IIngredient output, int time) {
            this.output = output;
            this.input = input;
            this.time = time;
        }

        @Override
        public void apply() {
            GrindstoneRecipes.instance().addGrindstoneRecipe(MineTweakerMC.getItemStack(input), MineTweakerMC.getItemStack(output), time);
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void undo() {
            GrindstoneRecipes.instance().removeGrindstoneRecipe(MineTweakerMC.getItemStack(input));
        }

        @Override
        public String describe() {
            return "Adding grindstone recipe for " + output;
        }

        @Override
        public String describeUndo() {
            return "Removing grindstone recipe for " + output;
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }

    private static class RemoveGrindstoneRecipe implements IUndoableAction {

        private final List<ItemStack> output;
        private final List<ItemStack> input;
        private final List<Integer> time;

        private RemoveGrindstoneRecipe(List<ItemStack> output, List<ItemStack> input, List<Integer> time) {
            this.output = output;
            this.input = input;
            this.time = time;
        }

        @Override
        public void apply() {
            for (ItemStack in: input)
                GrindstoneRecipes.instance().removeGrindstoneRecipe(in);
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void undo() {
            for (int i = 0; i < input.size(); i++)
                GrindstoneRecipes.instance().addGrindstoneRecipe(input.get(i), output.get(i), time.get(i));
        }

        @Override
        public String describe() {
            return "Removing grindstone recipe for " + output;
        }

        @Override
        public String describeUndo() {
            return "Adding grindstone recipe for " + output;
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }

}
