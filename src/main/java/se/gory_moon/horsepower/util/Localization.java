package se.gory_moon.horsepower.util;

import net.minecraft.util.text.translation.I18n;

public final class Localization {

    private static String translateString(String key, String... vars) {
        String result = translateToLocal(key);

        for (int i = 0; i < vars.length; i++) {
            String optionCheck = "[%" + (i + 1) + "->";
            int pos = result.indexOf(optionCheck);

            if (pos != -1) {
                int endPos = result.indexOf("]");
                if (endPos != -1) {
                    String[] options = result.substring(pos + optionCheck.length(), endPos).split("\\|");
                    int pickedOption = vars[i].equals("1") ? 1 : 0;
                    if (options.length > pickedOption) {
                        String opt = options[pickedOption];
                        result = result.substring(0, pos) + opt + result.substring(endPos + 1);

                        i--;
                    }
                }
            } else {
                result = result.replace("[%" + (i + 1) + "]", vars[i]);
            }
        }

        return result;
    }

    private static String translateToLocal(String key) {
        if (I18n.canTranslate(key)) {
            return I18n.translateToLocal(key);
        } else {
            return I18n.translateToFallback(key);
        }
    }

    public static class ITEM {

        public enum HORSE_GRINDSTONE {
            SIZE, LOCATION, USE;

            public String translate(String... vars) {
                return Localization.translateString("item.horsepower:grindstone.description." + toString().toLowerCase(), vars);
            }
        }

        public enum HAND_GRINDSTONE {
            INFO;

            public String translate(String... vars) {
                return Localization.translateString("item.horsepower:hand_grindstone.description." + toString().toLowerCase(), vars);
            }
        }

        public enum HORSE_CHOPPING {
            SIZE, LOCATION, USE;

            public String translate(String... vars) {
                return Localization.translateString("item.horsepower:chopping.description." + toString().toLowerCase(), vars);
            }
        }

    }

    public enum  INFO {

        GRINDSTONE_INVALID,
        CHOPPING_INVALID;

        public String translate(String... vars) {
            return Localization.translateString(key(), vars);
        }

        public String key() {
            return "info.horsepower:" + toString().toLowerCase().replaceAll("_", ".");
        }
    }

    public enum GUI {
        CATEGORY_GRINDING,
        CATEGORY_HAND_GRINDING,
        CATEGORY_CHOPPING,
        CATEGORY_MANUAL_CHOPPING;

        public String translate(String... vars) {
            return Localization.translateString(key(), vars);
        }

        public String key() {
            return "gui.horsepower.jei." + toString().toLowerCase().replaceAll("_", ".");
        }

    }

    public enum WAILA {
        GRINDSTONE_PROGRESS,
        WINDUP_PROGRESS,
        CHOPPING_PROGRESS,
        SHOW_ITEMS;

        public String translate(String... vars) {
            return Localization.translateString(key(), vars);
        }

        public String key() {
            return "gui.horsepower.waila." + toString().toLowerCase().replaceAll("_", ".");
        }
    }

    public enum TOP {
        GRINDSTONE_PROGRESS,
        WINDUP_PROGRESS,
        CHOPPING_PROGRESS;

        public String translate(String... vars) {
            return Localization.translateString(key(), vars);
        }

        public String key() {
            return "gui.horsepower.top." + toString().toLowerCase().replaceAll("_", ".");
        }
    }

}