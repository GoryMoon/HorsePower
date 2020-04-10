package se.gory_moon.horsepower.tileentity;

import com.google.common.collect.Lists;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.blocks.ModBlocks;
import se.gory_moon.horsepower.recipes.AbstractHPRecipe;
import se.gory_moon.horsepower.recipes.HPRecipes;
import se.gory_moon.horsepower.recipes.RecipeSerializers;
import se.gory_moon.horsepower.util.Localization;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PressTileEntity extends HPHorseBaseTileEntity {

    private FluidTank tank = new FluidTank(Configs.SERVER.pressTankSize.get());
    private int currentPressStatus;
    private LazyOptional<IFluidHandler> tankCap = LazyOptional.of(() -> tank);

    public PressTileEntity() {
        super(2, ModBlocks.PRESS_TILE.get());
        //        tank.setCanFill(false);
    }

    @Override
    public boolean validateArea() {
        if (searchPos == null) {
            searchPos = Lists.newArrayList();

            for (int x = -3; x <= 3; x++) {
                for (int z = -3; z <= 3; z++) {
                    if ((x <= 1 && x >= -1) && (z <= 1 && z >= -1))
                        continue;
                    searchPos.add(getPos().add(x, 0, z));
                    searchPos.add(getPos().add(x, 1, z));
                }
            }
        }

        for (BlockPos pos : searchPos) {
            if (!getWorld().getBlockState(pos).getMaterial().isReplaceable())
                return false;
        }
        return true;
    }

    @Override
    public boolean targetReached() {
        currentPressStatus++;

        int totalPress = Configs.SERVER.pointsPerPress.get();
        if (currentPressStatus >= (totalPress <= 0 ? 1: totalPress)) {
            currentPressStatus = 0;

            pressItem();
            return true;
        }
        markDirty();
        return false;
    }

    @Override
    public int getPositionOffset() {
        return 0;
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        tank.readFromNBT(compound.getCompound("fluid"));

        if (getStackInSlot(0).getCount() > 0) {
            currentPressStatus = compound.getInt("currentPressStatus");
        } else {
            currentPressStatus = 0;
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putInt("currentPressStatus", currentPressStatus);
        compound.put("fluid", tank.writeToNBT(new CompoundNBT()));
        return super.write(compound);
    }

    @Override
    public IRecipeType<? extends IRecipe<IInventory>> getRecipeType() {
        return RecipeSerializers.PRESSING_TYPE;
    }

    @Override
    public int getInventoryStackLimit() {
        AbstractHPRecipe recipe = getRecipe();
        if (recipe == null)
            return 64;
        return recipe.getInputCount();
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return index == 0 && HPRecipes.hasTypeRecipe(getRecipe(stack), null) && currentPressStatus == 0 && getStackInSlot(1).isEmpty();
    }

    @Override
    public int getOutputSlot() {
        return 1;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return currentPressStatus == 0 ? super.removeStackFromSlot(index): ItemStack.EMPTY;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        ItemStack itemstack = getStackInSlot(index);
        super.setInventorySlotContents(index, stack);

        boolean flag = !stack.isEmpty() && stack.isItemEqual(itemstack) && ItemStack.areItemStackTagsEqual(stack, itemstack);
        if (index == 0 && !flag)
            currentPressStatus = 0;

        markDirty();
    }

    @Override
    public int getInventoryStackLimit(ItemStack stack) {
        AbstractHPRecipe recipe = getRecipe(stack);
        if (recipe == null)
            return getInventoryStackLimit();
        return recipe.getInputCount();
    }

    @Override
    public void markDirty() {
        if (getStackInSlot(0).isEmpty())
            currentPressStatus = 0;

        super.markDirty();
    }

    @Override
    public boolean canWork() {
        if (getStackInSlot(0).isEmpty()) {
            return false;
        } else {
            AbstractHPRecipe recipe = getRecipe();
            if (recipe == null)
                return false;

            Ingredient input = recipe.getIngredients().get(0);
            ItemStack itemstack = recipe.getCraftingResult(inventory);
            FluidStack fluidOutput = recipe.getFluidOutput();

            if (getStackInSlot(0).getCount() < 1 || getStackInSlot(0).getCount() < recipe.getInputCount())
                return false;
            if (itemstack.isEmpty() && !recipe.isFluidRecipe())
                return false;

            ItemStack output = getStackInSlot(1);
            if (recipe.isFluidRecipe()) {
                return output.isEmpty() && (tank.getFluidAmount() == 0 || tank.fill(fluidOutput, IFluidHandler.FluidAction.SIMULATE) >= fluidOutput.getAmount());
            } else {
                return tank.getFluidAmount() == 0 && (output.isEmpty() || output.isItemEqual(itemstack) && output.getCount() + itemstack.getCount() <= output.getMaxStackSize());
            }
        }
    }

    @Override
    protected void invalidateCaps() {
        tankCap.invalidate();
        super.invalidateCaps();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            if (side == null || side == Direction.DOWN)
                return tankCap.cast();
        }
        return super.getCapability(cap, side);
    }

    private void pressItem() {
        if (canWork()) {
            AbstractHPRecipe recipe = getRecipe();
            ItemStack result = recipe.getCraftingResult(inventory);
            FluidStack fluidResult = recipe.getFluidOutput();

            ItemStack input = getStackInSlot(0);
            ItemStack output = getStackInSlot(1);

            if (recipe.isFluidRecipe()) {
                tank.fill(fluidResult, IFluidHandler.FluidAction.EXECUTE);
            } else {
                if (output.isEmpty()) {
                    setInventorySlotContents(1, result.copy());
                } else if (output.isItemEqual(result)) {
                    output.grow(result.getCount());
                }
            }

            input.shrink(input.getCount());
            markDirty();
        }
    }

    public int getCurrentPressStatus() {
        return currentPressStatus;
    }

    public FluidTank getTank() {
        return tank;
    }

    @Override
    public ITextComponent getName() {
        return new StringTextComponent("container.press");
    }

    @Nullable
    @Override
    public ITextComponent getDisplayName() {
        if (valid)
            return null;
        else
            return new TranslationTextComponent(Localization.INFO.PRESS_INVALID.key()).setStyle(new Style().setColor(TextFormatting.RED));
    }
}