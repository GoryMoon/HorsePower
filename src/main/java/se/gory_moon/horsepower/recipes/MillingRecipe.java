package se.gory_moon.horsepower.recipes;

import com.google.gson.JsonSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import se.gory_moon.horsepower.blocks.ModBlocks;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class MillingRecipe extends AbstractHPRecipe {

    private Type recipeType;

    public MillingRecipe(Type recipeType, ResourceLocation id, Ingredient input, ItemStack result, ItemStack secondary, int time, int secondaryChance) {
        super(RecipeSerializers.MILLING_TYPE, id, input, result, null, time, secondary, secondaryChance);
        this.recipeType = recipeType;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(recipeType == Type.HAND ? ModBlocks.BLOCK_HAND_MILLSTONE.orElseThrow(IllegalStateException::new): ModBlocks.BLOCK_MILLSTONE.orElseThrow(IllegalStateException::new));
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RecipeSerializers.MILLING_SERIALIZER;
    }

    public Type getRecipeType() {
        return recipeType;
    }

    public enum Type {
        BOTH(0, "both"),
        HAND(1, "hand"),
        HORSE(2, "horse");

        private final int id;
        private final String name;

        Type(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public static Type fromId(int id) {
            return id == 1 ? HAND: id == 2 ? HORSE: BOTH;
        }

        public static Type fromName(@Nonnull String name) {
            String s = name.toLowerCase();
            if (s.equals("hand")) {
                return HAND;
            } else if (s.equals("horse")) {
                return HORSE;
            } else if (s.equals("both")) {
                return BOTH;
            }
            throw new JsonSyntaxException("Invalid recipe type \"" + name + "\", expected either of: " + Arrays.toString(Type.values()));
        }
    }
}
