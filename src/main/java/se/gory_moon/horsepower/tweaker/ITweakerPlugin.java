package se.gory_moon.horsepower.tweaker;

import java.util.List;

public interface ITweakerPlugin {

    void applyTweaker();

    void register();

    List<IHPAction> getAdd();

    List<IHPAction> getRemove();
}
