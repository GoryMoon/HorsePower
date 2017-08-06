package se.gory_moon.horsepower.tweaker;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;

public abstract class BaseHPAction implements IHPAction, IAction {

    @Override
    public void run() {
        CraftTweakerAPI.apply(this);
    }
}
