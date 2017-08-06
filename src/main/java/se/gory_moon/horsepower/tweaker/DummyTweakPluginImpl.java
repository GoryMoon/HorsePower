package se.gory_moon.horsepower.tweaker;

import java.util.Collections;
import java.util.List;

public class DummyTweakPluginImpl implements ITweakerPlugin {

    @Override
    public void applyTweaker() {

    }

    @Override
    public void register() {

    }

    @Override
    public List<IHPAction> getAdd() {
        return Collections.emptyList();
    }

    @Override
    public List<IHPAction> getRemove() {
        return Collections.emptyList();
    }
}
