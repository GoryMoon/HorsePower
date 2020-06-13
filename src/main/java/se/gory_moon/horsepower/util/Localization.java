package se.gory_moon.horsepower.util;


import net.minecraft.client.resources.I18n;

public final class Localization {

    private static String translateString(String key, String... vars) {
        String result = translateToLocal(key);

        for (int i = 0; i < vars.length; i++) {
            String optionCheck = "[$" + (i + 1) + "->";
            int pos = result.indexOf(optionCheck);

            if (pos != -1) {
                int endPos = result.indexOf("]");
                if (endPos != -1) {
                    String[] options = result.substring(pos + optionCheck.length(), endPos).split("\\|");
                    int pickedOption = vars[i].equals("true") ? 1: 0;
                    if (options.length > pickedOption) {
                        String opt = options[pickedOption];
                        result = result.substring(0, pos) + opt + result.substring(endPos + 1);

                        i--;
                    }
                }
            } else {
                result = result.replace("[$" + (i + 1) + "]", vars[i]);
            }
        }

        return result;
    }

    private static String translateToLocal(String key) {
        return I18n.format(key);
    }

    public enum INFO {
        MILLSTONE_INVALID,
        CHOPPING_INVALID,
        PRESS_INVALID,
        ITEM_REVEAL;

        public String translate(String... vars) {
            return Localization.translateString(key(), vars);
        }

        public String key() {
            return "info.horsepower." + toString().toLowerCase().replaceAll("_", ".");
        }
    }

    public enum JEI {
        CATEGORY$MILLING,
        CATEGORY$MANUAL_MILLING,
        CATEGORY$CHOPPING,
        CATEGORY$MANUAL_CHOPPING,
        CATEGORY$MANUAL_CHOPPING_AXES,
        CATEGORY$MANUAL_CHOPPING_AXES_BASE_CHANCE,
        CATEGORY$MANUAL_CHOPPING_AXES_OTHER_CHANCE,
        CATEGORY$PRESS_ITEM,
        CATEGORY$PRESS_FLUID;

        public String translate(String... vars) {
            return Localization.translateString(key(), vars);
        }

        public String key() {
            return "gui.horsepower.jei." + toString().toLowerCase().replaceAll("\\$", ".");
        }

    }

    public enum WAILA {
        MILLSTONE_PROGRESS,
        WINDUP_PROGRESS,
        CHOPPING_PROGRESS,
        PRESS_PROGRESS;

        public String translate(String... vars) {
            return Localization.translateString(key(), vars);
        }

        public String key() {
            return "gui.horsepower.waila." + toString().toLowerCase().replaceAll("_", ".");
        }
    }

    public enum TOP {
        MILLSTONE_PROGRESS,
        WINDUP_PROGRESS,
        CHOPPING_PROGRESS,
        PRESS_PROGRESS;

        public String translate(String... vars) {
            return Localization.translateString(key(), vars);
        }

        public String key() {
            return "gui.horsepower.top." + toString().toLowerCase().replaceAll("_", ".");
        }
    }

    public static class ITEM {

        public enum HORSE_MILLSTONE {
            SIZE,
            LOCATION,
            USE;

            public String translate(String... vars) {
                return Localization.translateString(toString(), vars);
            }

            public String toString() {
                return "item.horsepower.millstone.description." + super.toString().toLowerCase();
            }
        }

        public enum MANUAL_MILLSTONE {
            INFO;

            public String translate(String... vars) {
                return Localization.translateString(toString(), vars);
            }

            @Override
            public String toString() {
                return "item.horsepower.manual_millstone.description." + super.toString().toLowerCase();
            }
        }

        public enum HORSE_CHOPPING {
            SIZE,
            LOCATION,
            USE;

            public String translate(String... vars) {
                return Localization.translateString(toString(), vars);
            }

            @Override
            public String toString() {
                return "item.horsepower.chopping.description." + super.toString().toLowerCase();
            }
        }

        public enum HORSE_PRESS {
            SIZE,
            LOCATION,
            USE;

            public String translate(String... vars) {
                return Localization.translateString(toString(), vars);
            }

            @Override
            public String toString() {
                return "item.horsepower.press.description." + super.toString().toLowerCase();
            }
        }

    }

    public static class CONFIG {
        public enum CLIENT {
            RENDER_ITEM_AMOUNT,
            MUST_LOOK_AT_BLOCK,
            SHOW_OBSTRUCTED_PLACE,

            MISC$TAGS,
            MISC$HARVEST_LEVEL,
            MISC$HARVEST_TYPES;

            public String key() {
                return "config.horsepower.client." + toString().toLowerCase().replaceAll("\\$", ".");
            }
        }

        public enum SERVER {
            USE_HORSE_INTERFACE,
            MOB_LIST,
            MILLSTONE_EXHAUSTION,
            SHOULD_DAMAGE_AXE,
            CHOPPING_BLOCK_AXES,
            HARVESTABLE_PERCENTAGE,
            CHOPPING_BLOCK_DROP,
            CHOPPING_EXHAUSTION,
            CHOPPING_MULTIPLIER,
            POINTS_FOR_WINDUP,
            POINTS_PER_ROTATION,
            POINTS_FOR_PRESS,
            PRESS_FLUID_TANK_SIZE, 
            PLANK_DATA_PACK_GENERATION, 
            PLANK_DATA_PACK_GENERATION_MANUAL_COUNT,
            PLANK_DATA_PACK_GENERATION_HORSE_COUNT, 
            PLANK_DATA_PACK_GENERATION_MANUAL_CHOP_COUNT,  //amount of axe chops
            PLANK_DATA_PACK_GENERATION_HORSE_CHOP_COUNT, //amount of horse chopper chops
            ;

            public String key() {
                return "config.horsepower.server." + toString().toLowerCase().replaceAll("\\$", ".");
            }
        }
    }

}