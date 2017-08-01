package se.gory_moon.horsepower.tweaker;

import com.google.common.collect.Lists;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import se.gory_moon.horsepower.tweaker.recipes.ChoppingRecipeTweaker;
import se.gory_moon.horsepower.tweaker.recipes.GrindstoneRecipeTweaker;
import se.gory_moon.horsepower.tweaker.recipes.PressRecipeTweaker;

import java.util.List;

public class TweakerPluginImpl implements ITweakerPlugin {

    public TweakerPluginImpl() {
    }

    public static List<IAction> actions = Lists.newArrayList();

    @Override
    public void applyTweaker() {
        for (IAction action: actions)
            action.apply();
    }

    @Override
    public void register() {
        CraftTweakerAPI.registerClass(GrindstoneRecipeTweaker.class);
        CraftTweakerAPI.registerClass(ChoppingRecipeTweaker.class);
        CraftTweakerAPI.registerClass(PressRecipeTweaker.class);
    }
}
