package se.gory_moon.horsepower.recipes;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractHPRecipe implements IRecipe<IInventory> {

    protected final IRecipeType<?> type;
    protected final ResourceLocation id;
    protected final String group;

    protected final Ingredient input;
    protected final ItemStack result;
    protected final FluidStack outputFluid;
    protected final int time;

    protected final ItemStack secondary;
    protected final int secondaryChance;

    protected AbstractHPRecipe(IRecipeType<?> type, ResourceLocation id, String group, Ingredient input, ItemStack result, FluidStack outputFluid, int time, ItemStack secondary, int secondaryChance) {
        this.type = type;
        this.id = id;
        this.group = group;
        this.input = input;
        this.result = result;
        this.outputFluid = outputFluid;
        this.time = time;
        this.secondary = secondary;
        this.secondaryChance = secondaryChance;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return Stream.of(input).collect(Collectors.toCollection(NonNullList::create));
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
        return input.test(inv.getStackInSlot(0));
    }

    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        return result.copy();
    }

    public FluidStack getCraftingFluid() {
        return outputFluid.copy();
    }

    public ItemStack getCraftingSecondary() {
        return secondary.copy();
    }

    @Override
    public ItemStack getRecipeOutput() {
        return result;
    }

    public FluidStack getFluidOutput() {
        return outputFluid;
    }

    public ItemStack getSecondaryOutput() {
        return secondary;
    }

    public int getSecondaryChance() {
        return secondaryChance;
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public IRecipeType<?> getType() {
        return type;
    }
}
