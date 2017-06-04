package se.gorymoon.horsepower.util;

import net.minecraft.util.text.translation.I18n;

public final class Localization {

    private static String translateString(String key, String... vars) {
        String result = I18n.translateToLocal(key);

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

    public static class ITEM {

        public enum MILL {
            SIZE, LOCATION, USE;

            public String translate(String... vars) {
                return Localization.translateString("item.horsepower:grindstone.description." + toString().toLowerCase(), vars);
            }
        }

    }

    public static enum  INFO {

        GRINDSTONE_INVALID;

        public String translate(String... vars) {
            return Localization.translateString(key(), vars);
        }

        public String key() {
            return "info.horsepower:" + toString().toLowerCase().replaceAll("_", ".");
        }
    }

}