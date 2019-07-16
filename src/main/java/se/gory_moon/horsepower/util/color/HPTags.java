package se.gory_moon.horsepower.util.color;

import net.minecraft.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

public class HPTags {

    public static class Items {
        public static final Tag<Item> DOUGH = forgeTag("food/dough");
        public static final Tag<Item> FLOUR = forgeTag("food/flour");
        public static final Tag<Item> FOOD = forgeTag("food");

        private static Tag<Item> forgeTag(String name) {
            return new ItemTags.Wrapper(new ResourceLocation("forge", name));
        }

        private static Tag<Item> tag(String name) {
            return new ItemTags.Wrapper(new ResourceLocation("horsepower", name));
        }
    }

}
