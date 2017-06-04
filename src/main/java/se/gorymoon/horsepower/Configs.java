package se.gorymoon.horsepower;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import se.gorymoon.horsepower.lib.Reference;

@Config(modid = Reference.MODID)
public class Configs {

    @Comment({"Enables the flour item", "If disabled all related recipes will be disabled", "Requires minecraft restart"})
    @RequiresMcRestart
    @Name("Enable Flour")
    public static boolean enableFlour = true;

    @Comment({"Enables the dough item", "If disabled all related recipes will be disabled", "Requires minecraft restart"})
    @RequiresMcRestart
    @Name("Enable Dough")
    public static boolean enableDough = true;

    @Comment({"Add recipes to the Grindstone Block here, the format of the recipes is:", "modid:input:meta-modid:output:meta-time", "The meta can be a '*' to be a wildcard", "The time for the horse increases for each point that it reaches, one lap is 8 points.", "The speed of the horse can make it go slower or faster to grind."})
    @Name("Grindstone Recipes")
    public static String[] grindstoneRecipes = {"minecraft:wheat-horsepower:flour-12"};

}
