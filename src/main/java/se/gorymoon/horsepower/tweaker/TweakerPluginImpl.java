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
import se.gorymoon.horsepower.recipes.MillRecipes;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//TODO test recipe adding and removing mre thoroughly
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

    @ZenMethod
    public static void addMillRecipe(IIngredient output, IIngredient input, int time) {
        AddMillRecipe recipe = new AddMillRecipe(output, input, time);
        MineTweakerAPI.apply(recipe);
        actions.add(recipe);
    }

    @ZenMethod
    public static void removeMillReicpe(IIngredient output) {

        List<ItemStack> toRemove = new ArrayList();
        List<ItemStack> toRemoveValues = new ArrayList();
        List<Integer> timeToRemove = new ArrayList();

        for (Map.Entry<ItemStack, ItemStack> entry: MillRecipes.instance().getMillList().entrySet()) {
            if (OreDictionary.itemMatches(MineTweakerMC.getItemStack(output), entry.getKey(), false)) {
                toRemove.add(entry.getKey());
                toRemoveValues.add(entry.getValue());
                timeToRemove.add(MillRecipes.instance().getMillTime(entry.getKey()));
            }
        }
        RemoveMillRecipe recipe = new RemoveMillRecipe(toRemove, toRemoveValues, timeToRemove);
        MineTweakerAPI.apply(recipe);
        actions.add(recipe);
    }

    @Override
    public void handle(MineTweakerImplementationAPI.ReloadEvent reloadEvent) {
        actions.clear();
    }

    private static class AddMillRecipe implements IUndoableAction {

        private final IIngredient output;
        private final IIngredient input;
        private final int time;

        private AddMillRecipe(IIngredient output, IIngredient input, int time) {
            this.output = output;
            this.input = input;
            this.time = time;
        }

        @Override
        public void apply() {
            MillRecipes.instance().addMillRecipe(MineTweakerMC.getItemStack(input), MineTweakerMC.getItemStack(output), time);
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void undo() {
            MillRecipes.instance().removeMillRecipe(MineTweakerMC.getItemStack(output));
        }

        @Override
        public String describe() {
            return "Adding mill recipe for " + output;
        }

        @Override
        public String describeUndo() {
            return "Removing mill recipe for " + output;
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }

    private static class RemoveMillRecipe implements IUndoableAction {

        private final List<ItemStack> output;
        private final List<ItemStack> input;
        private final List<Integer> time;

        private RemoveMillRecipe(List<ItemStack> output, List<ItemStack> input, List<Integer> time) {
            this.output = output;
            this.input = input;
            this.time = time;
        }

        @Override
        public void apply() {
            for (ItemStack in: input)
                MillRecipes.instance().removeMillRecipe(in);
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void undo() {
            for (int i = 0; i < input.size(); i++)
                MillRecipes.instance().addMillRecipe(input.get(i), output.get(i), time.get(i));
        }

        @Override
        public String describe() {
            return "Removing mill recipe for " + output;
        }

        @Override
        public String describeUndo() {
            return "Adding mill recipe for " + output;
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }

}
