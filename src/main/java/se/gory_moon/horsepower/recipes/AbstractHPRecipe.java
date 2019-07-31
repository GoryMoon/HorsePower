package se.gory_moon.horsepower.recipes;

import com.google.gson.JsonSyntaxException;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractHPRecipe implements IRecipe<IInventory> {

    protected final IRecipeType<?> type;
    protected final ResourceLocation id;

    protected final Ingredient input;
    protected final ItemStack result;
    protected final FluidStack outputFluid;
    protected final int time;

    protected final ItemStack secondary;
    protected final int secondaryChance;

    protected Type recipeType;

    protected AbstractHPRecipe(IRecipeType<?> type, ResourceLocation id, Ingredient input, ItemStack result, FluidStack outputFluid, int time, ItemStack secondary, int secondaryChance, Type recipeType) {
        this.type = type;
        this.id = id;
        this.input = input;
        this.result = result;
        this.outputFluid = outputFluid;
        this.time = time;
        this.secondary = secondary;
        this.secondaryChance = secondaryChance;
        this.recipeType = recipeType;
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
        return input.test(inv.getStackInSlot(0));
    }

    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        return result.copy();
    }

    @Override
    public ItemStack getRecipeOutput() {
        return result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return Stream.of(input).collect(Collectors.toCollection(NonNullList::create));
    }

    @Override
    public String getGroup() {
        return "";
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public IRecipeType<?> getType() {
        return type;
    }

    public FluidStack getCraftingFluid() {
        return outputFluid.copy();
    }

    public ItemStack getCraftingSecondary() {
        return secondary.copy();
    }

    public FluidStack getFluidOutput() {
        return outputFluid;
    }

    public boolean isFluidRecipe() {
        return outputFluid != null;
    }

    public int getTime() {
        return time;
    }

    public ItemStack getSecondaryOutput() {
        return secondary;
    }

    public int getSecondaryChance() {
        return secondaryChance;
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

        public static Type fromId(int id) {
            return id == 1 ? HAND: id == 2 ? HORSE: BOTH;
        }

        public static Type fromName(@Nonnull String name) {
            String s = name.toLowerCase();
            switch (s) {
                case "hand":
                    return HAND;
                case "horse":
                    return HORSE;
                case "both":
                    return BOTH;
            }
            throw new JsonSyntaxException("Invalid recipe type \"" + name + "\", expected either of: " + Arrays.toString(Type.values()));
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public boolean is(Type type) {
            return this == BOTH || this == type;
        }
    }
}
