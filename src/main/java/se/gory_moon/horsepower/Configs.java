package se.gory_moon.horsepower;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;

//TODO update to new system
public class Configs {

//    @Comment("General configs")
//    @Config.LangKey("config.general")
    public static General general = new General();

//    @Comment({"Contains the customizable recipes", "For the recipes to show in JEI the resources needs to be reloaded, F3+T"})
//    @Config.LangKey("config.recipes")
    public static Recipes recipes = new Recipes();

    public static class Client {

        public BooleanValue renderItemAmount;
        public BooleanValue mustLookAtBlock;
        public BooleanValue showObstructedPlace;
        public BooleanValue showTags;
        public BooleanValue showHarvestLevel;
        public ConfigValue<ArrayList<String>> harvestTypes;

        Client(ForgeConfigSpec.Builder builder) {
            builder.comment("Client only configs")
                    .push("client");

            renderItemAmount = builder
                    .comment("Render the amount text on how many items is in a stack in a HP block")
                    .translation("config.horsepower.client.render_item_amount")
                    .define("renderItemAmount", true);

            mustLookAtBlock = builder
                    .comment("If player must look at the block to show the amount in it")
                    .translation("config.horsepower.client.must_look_at_block")
                    .define("mustLookAtBlock", true);

            showObstructedPlace = builder
                    .comment("Should show the area needed when placing a HP block")
                    .translation("config.horsepower.client.show_obstructed_place")
                    .define("showObstructedPlace", true);

            //Misc is only client stuff
            builder.comment("Contains misc configs, mostly for debugging and dev")
                    .push("misc");

            showTags = builder
                    .comment("Will show a items all ore dictionaries in the tooltip")
                    .translation("config.horsepower.misc.tags")
                    .define("showTags", false);

            showHarvestLevel = builder
                    .comment("Will show the harvest level of items in the tooltip")
                    .translation("config.horsepower.gui.misc.harvest_level")
                    .define("showHartvestLevel", false);

            harvestTypes = builder
                    .comment("What harvest types to show the harvest level for")
                    .translation("config.horsepower.gui.misc.harvest_types")
                    .define("harvestTypes", Lists.newArrayList("axe"));

            builder.pop(2);
        }
    }

    public static class Recipes {

//        @Comment("If the separate list of recipes should be used for the hand grindstone")
//        @Name("Separate Grindstone Recipes")
//        @Config.RequiresMcRestart
        public boolean useSeperateMillstoneRecipes = false;

//        @Comment("If the separate list of recipes should be used for the chopping block")
//        @Name("Separate Chopping Recipes")
//        @Config.RequiresMcRestart
        public boolean useSeperateChoppingRecipes = false;

/*        @Comment({"Add recipes to the Grindstone Block here, the format of the recipes is: ", "modid:input:tag${nbt}-modid:result@amount${nbt}-time[-modid:secondary@amount${nbt}-chance]",
                "The amount is optional, if not set 1 is default, ${nbt} is optional and follows vanilla tag syntax",
                "The part in [] is optional, the chance can be 0-100",
                "The input item can be an item from the tag system, add a '#' at the beginning of the input, the other rules don't applies",
                "The time for the horse increases for each point that it reaches, one lap is 8 points.",
                "Must be edited with in-game editor for live changes."})
        @Config.LangKey("config.horsepower.recipes.grindstone")
        @Name("Grindstone Recipes")
*/        public String[] millstoneRecipes = {
                "minecraft:wheat-horsepower:flour-12",
                "minecraft:sugar_cane-minecraft:sugar-12",
                "minecraft:poppy-minecraft:rose_red-12",
                "minecraft:red_tulip-minecraft:rose_red-12",
                "minecraft:rose_bush-minecraft:rose_red@2-12",
                "minecraft:beetroot-minecraft:rose_red-12",
                "minecraft:azura_bluet-minecraft:light_gray_dye-12",
                "minecraft:white_tulip-minecraft:light_gray_dye-12",
                "minecraft:oxeye_daisy-minecraft:light_gray_dye-12",
                "minecraft:pink_tulip-minecraft:pink_dye-12",
                "minecraft:peony-minecraft:pink_dye@2-12",
                "minecraft:dandelion-minecraft:dandelion_yellow-12",
                "minecraft:sunflower-minecraft:dandelion_yellow@2-12",
                "minecraft:blue_orchid-minecraft:light_blue_dye-12",
                "minecraft:allium-minecraft:magenta_dye-12",
                "minecraft:lilac-minecraft:magenta_dye@2-12",
                "minecraft:orange_tulip-minecraft:orange_dye-12",
                "minecraft:bone-minecraft:bone_meal@3-12",
                "minecraft:bone_block-minecraft:bone_meal@9-12"
        };

//        @Comment({"Uses the same syntax as the regular grindstone recipes", "These recipes are only used when the config to separate them is enabled"})
//        @Config.LangKey("config.horsepower.recipes.hand_grindstone")
//        @Name("Hand Grindstone Recipes")
        public String[] handMillstoneRecipes = {};

/*        @Comment({"Add recipes to the Chopping Block here, the format of the recipes are: modid:input:tag${nbt}-modid:result@amount${nbt}-time",
                "The meta can be a '*' to be a wildcard", "The amount is optional, if not set 1 is default", "${nbt} is optional and follows vanilla tag syntax",
                "The input item can be an item from the tag system, add a '#' at the beginning of the input, the other rules don't applies",
                "The time is the amount of chops for it to process, the time for one chop is determined by the \"Windup time for chop\" config",
                "Must be edited with in-game editor for live changes."})
        @Config.LangKey("config.horsepower.recipes.chopping")
        @Name("Chopping Recipes")
*/        public String[] choppingRecipes = {
                "#minecraft:oak_logs-minecraft:oak_planks@4-1",
                "#minecraft:spruce_logs-minecraft:spruce_planks@4-1",
                "#minecraft:birch_logs-minecraft:birch_planks@4-1",
                "#minecraft:jungle_logs-minecraft:jungle_planks@4-1",
                "#minecraft:acacia_logs-minecraft:acacia_planks@4-1",
                "#minecraft:dark_oak_logs-minecraft:dark_oak_planks@4-1"
        };

//        @Comment({"Uses the same syntax as the regular chopping recipes, the only difference is that the time is the amount of chopps", "These recipes are only used when the config to separate them is enabled"})
//        @Config.LangKey("config.horsepower.recipes.manual_chopping")
//        @Name("Manual Chopping Block Recipes")
        public String[] manualChoppingRecipes = {
                "#minecraft:oak_logs-minecraft:oak_planks@4-4",
                "#minecraft:spruce_logs-minecraft:spruce_planks@4-4",
                "#minecraft:birch_logs-minecraft:birch_planks@4-4",
                "#minecraft:jungle_logs-minecraft:jungle_planks@4-4",
                "#minecraft:acacia_logs-minecraft:acacia_planks@4-4",
                "#minecraft:dark_oak_logs-minecraft:dark_oak_planks@4-4"
        };

/*        @Comment({"Add recipes to the Press Block here, the format of the recipe is: modid:input:tag@amount${nbt}-modid:result@amount${nbt}",
                "The meta can be a '*' to be a wildcard", "The amount is optional, if not set 1 is default", "${nbt} is optional and follows vanilla tag syntax",
                "The input item can be an item from the tag system, add ':tag' to input, the other rules don't applies",
                "The 'modid' for the result can be 'fluid' for fluid outputs",
                "The time is same for all recipes, it uses the \"Points For Press\"",
                "Must be edited with in-game editor for live changes."})
        @Name("Press Recipes")
*/        public String[] pressRecipes = {
                "minecraft:wheat_seeds@12-minecraft:dirt",
                //"ore:treeLeaves@8-fluid:water@1000"
        };
    }

    public static class General {

//        @Comment({"Removes the vanilla crafting recipes that grinds or uses grinded resources", "Removes Reeds -> Sugar, Bone -> Bonemeal, Wheat -> Bread, Flowers -> Dye"})
//        @Config.RequiresMcRestart
//        @Name("Remove Vanilla Recipes")
        public boolean removeVanillaRecipes = false;

//        @Comment({"Use the base definition of a horse, in vanilla it includes Horse, Donkey & Mule", "If false only entries in the list are valid",
//                "Must be edited with in-game editor for live changes."})
//        @Name("Use AbstractHorse")
        public boolean useHorseInterface = true;

//        @Comment("If the item used as an axe for the manual chopping block should be damaged")
//        @Name("Should Damage Axe")
        public boolean shouldDamageAxe = true;

//        @Comment({"The items to use with the manual chopping block, syntax is: ", "modid:input${nbt}=baseAmount-chance", "meta is optional and ${nbt} is also optional and follows vanilla tag syntax", "The baseAmount is the percentage of returned items, the chance is for getting one more result"})
//        @Config.LangKey("config.horsepower.chopping_axes")
//        @Name("Chopping Block Axes")
        public String[] choppingBlockAxes = {};

//        @Comment({"The percentage amount for the different materials", "The syntax is harvestLevel=baseAmount-chance",
//                "The baseAmount is the percentage of returned items, the chance is for getting one more result"})
//        @Name("Harvestable Percentages")
//        @Config.LangKey("config.horsepower.harvest")
        public String[] harvestable_percentage = {
            "0=25-25",
            "1=50-25",
            "2=75-25",
            "3=100-25",
            "4=125-50"
        };

//        @Comment({"If true the manual chopping block will drop the result items", "If false the manual chopping block will put the result items in it's internal inventory"})
//        @Name("Manual Chopping Block result")
        public boolean choppingBlockDrop = true;

//        @Comment({"That amount of \"points\" for the chopper to do windup and do a chop", "One lap around the chopping block is 8 \"points\""})
//        @Name("Windup time for chop")
//        @Config.RangeInt(min = 1)
        public int pointsForWindup = 8;

//        @Comment({"The amount of points per rotation with a hand grindstone", "The points correspond to the recipes requirement of time"})
//        @Name("Points Per Rotation")
//        @Config.RangeInt(min = 1)
        public int pointsPerRotation = 2;

//        @Comment({"The exhaustion amount that will be added to the player when using the grindstone", "If set to 0 this is disabled"})
//        @Name("Grindstone Exhaustion")
        public double millstoneExhaustion = 0.1D;

//        @Comment({"The exhaustion amount that will be added to the player when using the chopping block", "If set to 0 this is disabled"})
//        @Name("Chopping Block Exhaustion")
        public double choppingblockExhaustion = 0.1D;

//        @Comment({"The amount of chopps the time value in the horse chopping recipes should be multiplied with if recipes isn't separated",
//                "If the recipes are separate this isn't used, instead the recipe value is used"})
//        @Name("Chopping Multiplier")
        public int choppMultiplier = 4;

//        @Comment({"Add mobs that can use the horse powered blocks", "Only mobs that can be leashed are valid", "Add the full path to the mob class, can be found with /horsepower entity or /hp entity",
//                "Must be edited with in-game editor for live changes."})
//        @Config.LangKey("config.horsepower.mobs")
//        @Name("Mob List")
        public String[] millstoneMobList = {};

//        @Comment("The amount of points that is needed for a full press")
//        @Name("Points For Press")
        public int pointsForPress = 16;

//        @Comment("The tank size of the press in mb, 1000mb = 1 bucket")
//        @Config.RequiresMcRestart
//        @Name("Press Tank Size")
        public int pressFluidTankSize = Fluid.BUCKET_VOLUME * 3;

//        @Comment({"If true the chopping blocks will use all logs types in the game when crafted", "If false the chopping blocks will only use the vanilla logs", "If only vanilla logs are used other logs in recipe will yield oak texture"})
//        @Name("Use Dynamic Chopping Crafting")
        public boolean useDynamicCrafting = true;

//        @Comment({"If true it will show all chopping block types in the creative tab and JEI", "If false only the vanilla wood variants will show", "JEI needs a resource reload for this to update"})
//        @Name("Use Dynamic Chopping Display")
        public boolean useDynamicDisplay = true;
    }

    static final ForgeConfigSpec clientSpec;
    public static final Client CLIENT;
    static {
        final Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Client::new);
        clientSpec = specPair.getRight();
        CLIENT = specPair.getLeft();
    }

    @SubscribeEvent
    public static void onLoad(final ModConfig.Loading configEvent) {
        HorsePowerMod.LOGGER.debug("Loaded HP config file {}", configEvent.getConfig().getFileName());
    }

    @SubscribeEvent
    public static void onFileChange(final ModConfig.ConfigReloading configEvent) {
        HorsePowerMod.LOGGER.debug("HP config just got changed on the file system!");
    }
}
