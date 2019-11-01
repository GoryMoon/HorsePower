package se.gory_moon.horsepower;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;
import se.gory_moon.horsepower.util.Constants;
import se.gory_moon.horsepower.util.Localization;

import java.util.ArrayList;

public class Configs {

    public static final Client CLIENT;
    public static final Server SERVER;
    static final ForgeConfigSpec clientSpec;
    static final ForgeConfigSpec serverSpec;
    public static Recipes recipes = new Recipes();

    static {
        final Pair<Client, ForgeConfigSpec> clientSpecPair = new ForgeConfigSpec.Builder().configure(Client::new);
        clientSpec = clientSpecPair.getRight();
        CLIENT = clientSpecPair.getLeft();

        final Pair<Server, ForgeConfigSpec> serverSpecPair = new ForgeConfigSpec.Builder().configure(Server::new);
        serverSpec = serverSpecPair.getRight();
        SERVER = serverSpecPair.getLeft();
    }

    @SubscribeEvent
    public static void onLoad(final ModConfig.Loading configEvent) {
        if (configEvent.getConfig().getType() == ModConfig.Type.SERVER) {
            HPEventHandler.reloadConfig();
        }
    }

    @SubscribeEvent
    public static void onFileChange(final ModConfig.ConfigReloading configEvent) {
        ((CommentedFileConfig) configEvent.getConfig().getConfigData()).load();
        if (configEvent.getConfig().getType() == ModConfig.Type.SERVER) {
            HPEventHandler.reloadConfig();
        }
    }

    /**
     * Called from in-game config
     *
     * @param event The event
     */
    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(Constants.MOD_ID)) {
            HPEventHandler.reloadConfig();
        }
    }

    public static class Client {

        public final BooleanValue renderItemAmount;
        public final BooleanValue mustLookAtBlock;
        public final BooleanValue showObstructedPlace;
        public final BooleanValue showTags;
        public final BooleanValue showHarvestLevel;
        public final ConfigValue<ArrayList<String>> harvestTypes;
        //        @Comment({"If true it will show all chopping block types in the creative tab and JEI", "If false only the vanilla wood variants will show", "JEI needs a resource reload for this to update"})
        //        @Name("Use Dynamic Chopping Display")
        public boolean useDynamicDisplay = true;

        Client(ForgeConfigSpec.Builder builder) {
            builder.comment("Client only configs")
                    .push("client");

            renderItemAmount = builder
                    .comment("Render the amount text on how many items is in a stack in a HP block")
                    .translation(Localization.CONFIG.CLIENT.RENDER_ITEM_AMOUNT.key())
                    .define("render_item_amount", true);

            mustLookAtBlock = builder
                    .comment("If player must look at the block to show the amount in it")
                    .translation(Localization.CONFIG.CLIENT.MUST_LOOK_AT_BLOCK.key())
                    .define("must_look_at_block", true);

            showObstructedPlace = builder
                    .comment("Should show the area needed when placing a HP block")
                    .translation(Localization.CONFIG.CLIENT.SHOW_OBSTRUCTED_PLACE.key())
                    .define("show_obstructed_place", true);

            //Misc is only client stuff
            builder.comment("Contains misc configs, mostly for debugging and dev")
                    .push("misc");

            showTags = builder
                    .comment("Will show a items all tags in the tooltip")
                    .translation(Localization.CONFIG.CLIENT.MISC$TAGS.key())
                    .define("show_tags", false);

            showHarvestLevel = builder
                    .comment("Will show the harvest level of items in the tooltip")
                    .translation(Localization.CONFIG.CLIENT.MISC$HARVEST_LEVEL.key())
                    .define("show_hartvest_level", false);

            harvestTypes = builder
                    .comment("What harvest types to show the harvest level for")
                    .translation(Localization.CONFIG.CLIENT.MISC$HARVEST_TYPES.key())
                    .define("harvest_types", () -> Lists.newArrayList("axe"), o -> o instanceof ArrayList);

            builder.pop(2);
        }
    }

    public static class Server {

        public final ConfigValue<ArrayList<String>> mobList;
        public final BooleanValue useHorseInterface;

        public final DoubleValue millstoneExhaustion;

        public final BooleanValue shouldDamageAxe;
        public final ConfigValue<ArrayList<String>> choppingBlockAxes;
        public final ConfigValue<ArrayList<String>> harvestablePercentage;
        public final BooleanValue choppingBlockDrop;
        public final DoubleValue choppingExhaustion;
        public final IntValue choppingMultiplier;
        public final IntValue pointsForWindup;
        public final IntValue pointsPerRotation;

        public final IntValue pointsPerPress;
        public final IntValue pressTankSize;

        //        @Comment({"If true the chopping blocks will use all logs types in the game when crafted", "If false the chopping blocks will only use the vanilla logs", "If only vanilla logs are used other logs in recipe will yield oak texture"})
        //        @Name("Use Dynamic Chopping Crafting")
        public boolean useDynamicCrafting = true;

        Server(ForgeConfigSpec.Builder builder) {
            builder.comment("Server only configs")
                    .push("server");

            useHorseInterface = builder
                    .comment("Use the base definition of a horse, in vanilla it includes Horse, Donkey & Mule", "If false only entries in the list are valid")
                    .translation(Localization.CONFIG.SERVER.USE_HORSE_INTERFACE.key())
                    .define("use_horse_interface", true);

            mobList = builder
                    .comment("Add mobs that can use the horse powered blocks", "Only mobs that can be leashed are valid", "Add the full path to the mob class, can be found with /horsepower entity or /hp entity")
                    .translation(Localization.CONFIG.SERVER.MOB_LIST.key())
                    .define("mod_list", ArrayList::new, o -> o != null && ArrayList.class.isAssignableFrom(o.getClass()) && ((ArrayList<String>) o).stream().allMatch(s -> {
                        try {
                            Class.forName(s);
                            return true;
                        } catch (ClassNotFoundException e) {
                            HorsePower.LOGGER.error("Error in config, could not find (" + s + ") mob class, mod for entity might not be installed");
                            return false;
                        }
                    }));

            builder.comment("Configs related to the Millstone")
                    .push("milling");

            millstoneExhaustion = builder
                    .comment("The exhaustion amount that will be added to the player when using the grindstone", "If set to 0 this is disabled")
                    .translation(Localization.CONFIG.SERVER.MILLSTONE_EXHAUSTION.key())
                    .defineInRange("millstone_exhaustion", 0.1D, 0D, Double.MAX_VALUE);

            pointsPerRotation = builder
                    .comment("The amount of points per rotation with a hand millstone", "The points correspond to the recipes requirement of time")
                    .translation(Localization.CONFIG.SERVER.POINTS_PER_ROTATION.key())
                    .defineInRange("points_per_rotation", 2, 1, Integer.MAX_VALUE);

            builder.pop()
                    .comment("Configs related to the chopping blocks")
                    .push("chopping");

            shouldDamageAxe = builder
                    .comment("If the item used as an axe for the manual chopping block should be damaged")
                    .translation(Localization.CONFIG.SERVER.SHOULD_DAMAGE_AXE.key())
                    .define("should_damage_axe", true);

            choppingBlockAxes = builder
                    .comment("The items to use with the manual chopping block, syntax is:", "   modid:input${nbt}=base_amount-chance", "meta is optional and ${nbt} is also optional and follows vanilla tag syntax", "The base_amount is the percentage of returned items, the chance is for getting one more result")
                    .translation(Localization.CONFIG.SERVER.CHOPPING_BLOCK_AXES.key())
                    .define("chopping_block_axes", new ArrayList<>());

            harvestablePercentage = builder
                    .comment("The percentage amount for the different materials", "The syntax is harvest_level=base_amount-chance", "The base_amount is the percentage of returned items, the chance is for getting one more result")
                    .translation(Localization.CONFIG.SERVER.HARVESTABLE_PERCENTAGE.key())
                    .define("harvestable_percentage", Lists.newArrayList("0=25-25", "1=50-25", "2=75-25", "3=100-25", "4=125-50"));

            choppingBlockDrop = builder
                    .comment("If true the manual chopping block will drop the result items", "If false the manual chopping block will put the result items in it's internal inventory")
                    .translation(Localization.CONFIG.SERVER.CHOPPING_BLOCK_DROP.key())
                    .define("chopping_block_drop", true);

            choppingExhaustion = builder
                    .comment("The exhaustion amount that will be added to the player when using the chopping block", "If set to 0 this is disabled")
                    .translation(Localization.CONFIG.SERVER.CHOPPING_EXHAUSTION.key())
                    .defineInRange("chopping_exhaustion", 0.1D, 0D, Double.MAX_VALUE);

            choppingMultiplier = builder
                    .comment("The amount of chopps the time value in the horse chopping recipes should be multiplied with if recipes isn't separated", "If the recipes are separate this isn't used, instead the recipe value is used")
                    .translation(Localization.CONFIG.SERVER.CHOPPING_MULTIPLIER.key())
                    .defineInRange("chopping_multiplier", 4, 1, Integer.MAX_VALUE);

            pointsForWindup = builder
                    .comment("That amount of \"points\" for the chopper to do windup and do a chop", "One lap around the chopping block is 8 \"points\"")
                    .translation(Localization.CONFIG.SERVER.POINTS_FOR_WINDUP.key())
                    .defineInRange("points_for_windup", 8, 1, Integer.MAX_VALUE);

            builder.pop()
                    .comment("Configs related to the Press")
                    .push("pressing");

            pointsPerPress = builder
                    .comment("The amount of points that is needed for a full press")
                    .translation(Localization.CONFIG.SERVER.POINTS_FOR_PRESS.key())
                    .defineInRange("points_per_press", 16, 1, Integer.MAX_VALUE);

            pressTankSize = builder
                    .comment("The tank size of the press in mb, 1000mb = 1 bucket")
                    .worldRestart()
                    .translation(Localization.CONFIG.SERVER.PRESS_FLUID_TANK_SIZE.key())
                    .defineInRange("press_tank_size", FluidAttributes.BUCKET_VOLUME * 3, FluidAttributes.BUCKET_VOLUME, FluidAttributes.BUCKET_VOLUME * 100);

            builder.pop(2);
        }
    }

    //TODO make recipes
    public static class Recipes {

        //        @Comment("If the separate list of recipes should be used for the chopping block")
        //        @Name("Separate Chopping Recipes")
        //        @Config.RequiresMcRestart
        public boolean useSeperateChoppingRecipes = false;

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
    }
}
