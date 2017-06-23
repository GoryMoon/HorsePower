package se.gory_moon.horsepower;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import se.gory_moon.horsepower.lib.Reference;

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

    @Comment({"Removes the vanilla crafting recipes that grinds or uses grinded resources", "Removes Reeds -> Sugar, Bone -> Bonemeal, Wheat -> Bread, Flowers -> Dye"})
    @RequiresMcRestart
    @Name("Remove Vanilla Recipes")
    public static boolean removeVanillaRecipes = false;

    @Comment({"Use the base definition of a horse, in vanilla it includes Horse, Donkey, Mule & Mule", "If false only entries in the list are valid",
            "Can be reloaded with /horsepower reload, but must be edited with in-game editor for that to work."})
    @Name("Use AbstractHorse")
    public static boolean useHorseInterface = true;

    @Comment({"That amount of \"points\" for the chopper to do windup and do a chop", "One lap around the chopping block is 8 \"points\""})
    @Name("Windup time for chop")
    @Config.RangeInt(min = 1)
    public static int pointsForWindup = 8;

    @Comment({"The amount of points per rotation with a hand grindstone", "The points correspond to the recipes requirement of time"})
    @Name("Points Per Rotation")
    public static int pointsPerRotation = 2;

    @Comment({"Add mobs that can use the grindstone", "Only mobs that can be leashed can use the grindstone", "Add the full path to the mob class, can be found with CraftTweaker /mt entities",
            "Can be reloaded with /horsepower reload, but must be edited with in-game editor for that to work."})
    @Name("Grindstone Mob List")
    public static String[] grindstoneModList = {};

    @Comment({"Add recipes to the Grindstone Block here, the format of the recipes are: modid:input:meta-modid:output:meta@amount-time",
            "The meta can be a '*' to be a wildcard", "The amount is optional, if not set 1 is default",
            "The input item can be an item from the ore dictionary, use it as 'ore:name', the other rules don't applies",
            "The time for the horse increases for each point that it reaches, one lap is 8 points.",
            "Can be reloaded with /horsepower reload, but must be edited with in-game editor for that to work."})
    @Name("Grindstone Recipes")
    public static String[] grindstoneRecipes = {
            "minecraft:wheat-horsepower:flour-12",
            "minecraft:reeds-minecraft:sugar-12",
            "minecraft:red_flower-minecraft:dye:1-12",
            "minecraft:red_flower:4-minecraft:dye:1-12",
            "minecraft:double_plant:4-minecraft:dye:1@2-12",
            "minecraft:beetroot-minecraft:dye:1-12",
            "minecraft:red_flower:3-minecraft:dye:7-12",
            "minecraft:red_flower:6-minecraft:dye:7-12",
            "minecraft:red_flower:8-minecraft:dye:7-12",
            "minecraft:red_flower:7-minecraft:dye:9-12",
            "minecraft:double_plant:5-minecraft:dye:9@2-12",
            "minecraft:yellow_flower-minecraft:dye:11-12",
            "minecraft:double_plant-minecraft:dye:11@2-12",
            "minecraft:red_flower:1-minecraft:dye:12-12",
            "minecraft:red_flower:2-minecraft:dye:13-12",
            "minecraft:double_plant:1-minecraft:dye:13@2-12",
            "minecraft:red_flower:5-minecraft:dye:14-12",
            "minecraft:bone-minecraft:dye:15@3-12",
            "minecraft:bone_block-minecraft:dye:15@9-12",
    };

    @Comment({"Add recipes to the Grindstone Block here, the format of the recipes are: modid:input:meta-modid:output:meta@amount-time",
            "The meta can be a '*' to be a wildcard", "The amount is optional, if not set 1 is default",
            "The input item can be an item from the ore dictionary, use it as 'ore:name', the other rules don't applies",
            "The time is the amount of chops for it to process, the time for one chop is determined by the \"Windup time for chop\" config",
            "Can be reloaded with /horsepower reload, but must be edited with in-game editor for that to work."})
    @Name("Chopping Recipes")
    public static String[] choppingRecipes = {
            "minecraft:log:0-minecraft:planks:0@4-1",
            "minecraft:log:1-minecraft:planks:1@4-1",
            "minecraft:log:2-minecraft:planks:2@4-1",
            "minecraft:log:3-minecraft:planks:3@4-1",
            "minecraft:log2:0-minecraft:planks:4@4-1",
            "minecraft:log2:1-minecraft:planks:5@4-1"};

}
