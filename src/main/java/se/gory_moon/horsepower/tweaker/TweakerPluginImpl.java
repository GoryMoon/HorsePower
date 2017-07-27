package se.gory_moon.horsepower.tweaker;

import com.google.common.collect.Lists;
import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.MineTweakerImplementationAPI;
import minetweaker.util.IEventHandler;
import se.gory_moon.horsepower.tweaker.recipes.ChoppingRecipeTweaker;
import se.gory_moon.horsepower.tweaker.recipes.GrindstoneRecipeTweaker;
import se.gory_moon.horsepower.tweaker.recipes.PressRecipeTweaker;

import java.util.List;

public class TweakerPluginImpl implements ITweakerPlugin, IEventHandler<MineTweakerImplementationAPI.ReloadEvent> {

    public TweakerPluginImpl() {
        MineTweakerImplementationAPI.onReloadEvent(this);
    }

    public static List<IUndoableAction> actions = Lists.newArrayList();

    @Override
    public void applyTweaker() {
        for (IUndoableAction action: actions)
            action.apply();
    }

    @Override
    public void handle(MineTweakerImplementationAPI.ReloadEvent reloadEvent) {
        actions.clear();
    }

    @Override
    public void register() {
        MineTweakerAPI.registerClass(GrindstoneRecipeTweaker.class);
        MineTweakerAPI.registerClass(ChoppingRecipeTweaker.class);
        MineTweakerAPI.registerClass(PressRecipeTweaker.class);
    }
}
