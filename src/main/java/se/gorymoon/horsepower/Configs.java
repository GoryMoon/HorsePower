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

    @Comment({"Add recipes to the Grindstone Block here, the format of the recipes are: modid:input:meta-modid:output:meta@amount-time",
            "The meta can be a '*' to be a wildcard", "The amount is optional, if not set 1 is default",
            "The input item can be an item from the ore dictionary, use it as 'ore:name', the other rules don't applies",
            "The time for the horse increases for each point that it reaches, one lap is 8 points.",
            "Can be reloaded with /horsepower reload, but must be edited with in-game editor for that to work."})
    @Name("Grindstone Recipes")
    public static String[] grindstoneRecipes = {"minecraft:wheat-horsepower:flour-12"};

}
