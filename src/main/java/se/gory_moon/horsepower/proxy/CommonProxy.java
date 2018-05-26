package se.gory_moon.horsepower.proxy;

import se.gory_moon.horsepower.advancements.Manager;

public class CommonProxy {

    public void preInit() {
        Manager.register();
    }

    public void init() {}

    public void loadComplete() {

    }
}
