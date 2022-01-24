package se.gory_moon.horsepower.util;

import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;

public class HPTags {

    public static class Items {
        public static final ITag.INamedTag<Item> DOUGH = forgeTag("food/dough");
        public static final ITag.INamedTag<Item> FLOUR = forgeTag("food/flour");
        public static final ITag.INamedTag<Item> FOOD = forgeTag("food");

        private static ITag.INamedTag<Item> forgeTag(String name) {
            return ItemTags.makeWrapperTag("forge:" + name);
        }

        private static ITag.INamedTag<Item> tag(String name) {
            return ItemTags.makeWrapperTag("horsepower:" + name);
        }
    }

    public static class Entities {

        public static final ITag.INamedTag<EntityType<?>> WORKER_ENTITIES = tag("workers");

        private static ITag.INamedTag<EntityType<?>> tag(String name) {
            return EntityTypeTags.getTagById("horsepower:" + name);
        }
    }
}
