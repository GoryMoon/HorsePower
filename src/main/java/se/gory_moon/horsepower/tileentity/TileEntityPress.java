package se.gory_moon.horsepower.tileentity;

import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.recipes.HPRecipeBase;
import se.gory_moon.horsepower.recipes.HPRecipes;
import se.gory_moon.horsepower.recipes.PressRecipe;
import se.gory_moon.horsepower.util.Localization;

import javax.annotation.Nullable;

public class TileEntityPress extends TileEntityHPHorseBase {

    private int currentPressStatus;

    public TileEntityPress() {
        super(2);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger("currentPressStatus", currentPressStatus);

        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        if (getStackInSlot(0).getCount() > 0) {
            currentPressStatus = compound.getInteger("currentPressStatus");
        } else {
            currentPressStatus = 0;
        }
    }

    @Override
    public void markDirty() {
        //if (getStackInSlot(1).isEmpty() && getStackInSlot(2).isEmpty())
            //BlockGrindstone.setState(false, world, pos);

        if (getStackInSlot(0).isEmpty())
            currentPressStatus = 0;

        super.markDirty();
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

        for (BlockPos pos: searchPos) {
            if (!getWorld().getBlockState(pos).getBlock().isReplaceable(world, pos))
                return false;
        }
        return true;
    }

    @Override
    public boolean targetReached() {
        currentPressStatus++;

        int totalPress = Configs.general.pointsForPress;
        if (currentPressStatus >= (totalPress <= 0 ? 1: totalPress)) {
            currentPressStatus = 0;

            pressItem();
            return true;
        }
        markDirty();
        return false;
    }

    @Override
    public HPRecipeBase getRecipe() {
        return HPRecipes.instance().getPressRecipe(getStackInSlot(0));
    }

    @Override
    public ItemStack getRecipeItemStack() {
        return HPRecipes.instance().getPressResult(getStackInSlot(0));
    }

    @Override
    public int getPositionOffset() {
        return 0;
    }

    private void pressItem() {
        if (canWork()) {
            HPRecipeBase recipe = getRecipe();
            ItemStack result = recipe.getOutput();

            ItemStack input = getStackInSlot(0);
            ItemStack output = getStackInSlot(1);

            if (output.isEmpty()) {
                setInventorySlotContents(1, result.copy());
            } else if (output.isItemEqual(result)) {
                output.grow(result.getCount());
            }

            input.shrink(input.getCount());
            //BlockGrindstone.setState(true, world, pos);
            markDirty();
        }
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        ItemStack itemstack = getStackInSlot(index);
        super.setInventorySlotContents(index, stack);

        //if ((index == 1 || index == 2) && getStackInSlot(1).isEmpty() && getStackInSlot(2).isEmpty())
            //BlockGrindstone.setState(false, world, pos);

        boolean flag = !stack.isEmpty() && stack.isItemEqual(itemstack) && ItemStack.areItemStackTagsEqual(stack, itemstack);
        if (index == 0 && !flag)
            currentPressStatus = 0;

        markDirty();
    }

    @Override
    public int getInventoryStackLimit(ItemStack stack) {
        PressRecipe recipe = HPRecipes.instance().getPressRecipe(stack);
        if (recipe == null)
            return getInventoryStackLimit();
        return recipe.getInput().getCount();
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return currentPressStatus == 0 ? super.removeStackFromSlot(index): ItemStack.EMPTY;
    }

    @Override
    public int getInventoryStackLimit() {
        PressRecipe recipe = HPRecipes.instance().getPressRecipe(getStackInSlot(0));
        if (recipe == null)
            return 64;
        return recipe.getInput().getCount();
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return index == 0 && HPRecipes.instance().hasPressRecipe(stack) && currentPressStatus == 0 && getStackInSlot(1).isEmpty();
    }

    @Override
    public int getField(int id) {
        switch (id) {
            case 0:
                return currentPressStatus;
            default:
                return 0;
        }
    }

    @Override
    public void setField(int id, int value) {
        switch (id) {
            case 0:
                currentPressStatus = value;
                break;
        }
    }

    @Override
    public int getFieldCount() {
        return 1;
    }

    @Override
    public String getName() {
        return "container.press";
    }

    @Override
    public int getOutputSlot() {
        return 1;
    }

    @Nullable
    @Override
    public ITextComponent getDisplayName() {
        if (valid)
            return super.getDisplayName();
        else
            return new TextComponentTranslation(Localization.INFO.PRESS_INVALID.key()).setStyle(new Style().setColor(TextFormatting.RED));
    }
}
