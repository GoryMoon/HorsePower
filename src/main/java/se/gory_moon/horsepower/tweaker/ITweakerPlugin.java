package se.gory_moon.horsepower.tweaker;


public interface ITweakerPlugin {

    void applyTweaker();

    void register();

    void run();
}
