package se.gorymoon.horsepower;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import se.gorymoon.horsepower.lib.Reference;

@Config(modid = Reference.MODID)
public class Configs {

    @Comment({"Add recipes to the MillBlock here, the format of the recipes is:", "modid:input:meta-modid:output:meta-time", "The meta can be a '*' to be a wildcard", "The time for the horse increases for each point that it reaches, one lap is 8 points."})
    @Name("Mill Recipes")
    public static String[] millRecipes = {"minecraft:wheat-horsepower:flour-12"};

}
