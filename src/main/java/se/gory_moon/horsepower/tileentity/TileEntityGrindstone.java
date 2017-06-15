package se.gory_moon.horsepower.tileentity;

import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import se.gory_moon.horsepower.blocks.BlockGrindstone;
import se.gory_moon.horsepower.recipes.GrindstoneRecipes;

public class TileEntityGrindstone extends TileEntityHPBase {

    private static final int[] SLOTS_TOP = new int[] {0};
    private static final int[] SLOTS_BOTTOM = new int[] {1};

    private int currentItemMillTime;
    private int totalItemMillTime;

    public TileEntityGrindstone() {
        super(2);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger("millTime", currentItemMillTime);
        compound.setInteger("totalMillTime", totalItemMillTime);

        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        if (getStackInSlot(0).getCount() > 0) {
            currentItemMillTime = compound.getInteger("millTime");
            totalItemMillTime = compound.getInteger("totalMillTime");
        } else {
            currentItemMillTime = 0;
            totalItemMillTime = 1;
        }
    }

    @Override
    public void markDirty() {
        if (getStackInSlot(1).isEmpty())
            BlockGrindstone.setState(false, world, pos);

        super.markDirty();
        notifyUpdate();
    }

    @Override
    public boolean validateArea() {
        if (searchPos == null) {
            searchPos = Lists.newArrayList();

            for (int x = -3; x <= 3; x++) {
                for (int z = -3; z <= 3; z++) {
                    if (x == 0 && z == 0)
                        continue;
                    searchPos.add(getPos().add(x, 0, z));
                    searchPos.add(getPos().add(x, -1, z));
                }
            }
        }

        for (BlockPos pos: searchPos) {
            if (!getWorld().isAirBlock(pos))
                return false;
        }
        return true;
    }

    @Override
    boolean targetReached() {
        currentItemMillTime++;

        if (currentItemMillTime >= totalItemMillTime) {
            currentItemMillTime = 0;

            totalItemMillTime = GrindstoneRecipes.instance().getGrindstoneTime(getStackInSlot(0));
            millItem();
            return true;
        }
        return false;
    }

    @Override
    public boolean canWork() {
        if (getStackInSlot(0).isEmpty()) {
            return false;
        } else {
            ItemStack itemstack = GrindstoneRecipes.instance().getGrindstoneResult(getStackInSlot(0));

            if (itemstack.isEmpty()) {
                return false;
            } else {
                ItemStack output = getStackInSlot(1);
                if (output.isEmpty()) return true;
                if (!output.isItemEqual(itemstack)) return false;
                int result = output.getCount() + itemstack.getCount();
                return result <= getInventoryStackLimit() && result <= output.getMaxStackSize();
            }
        }
    }

    private void millItem() {
        if (canWork()) {
            ItemStack input = getStackInSlot(0);
            ItemStack result = GrindstoneRecipes.instance().getGrindstoneResult(getStackInSlot(0));
            ItemStack output = getStackInSlot(1);

            if (output.isEmpty()) {
                setInventorySlotContents(1, result.copy());
            } else if (output.getItem() == result.getItem()) {
                output.grow(result.getCount());
            }

            input.shrink(1);
            BlockGrindstone.setState(true, world, pos);
        }
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return side == EnumFacing.DOWN ? SLOTS_BOTTOM : (side == EnumFacing.UP ? SLOTS_TOP : new int[0]);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        super.setInventorySlotContents(index, stack);

        if (index == 1 && getStackInSlot(1).isEmpty()) {
            BlockGrindstone.setState(false, world, pos);
            markDirty();
        }

        ItemStack itemstack = getStackInSlot(index);
        boolean flag = !stack.isEmpty() && stack.isItemEqual(itemstack) && ItemStack.areItemStackTagsEqual(stack, itemstack);
        if (index == 0 && !flag) {
            totalItemMillTime = GrindstoneRecipes.instance().getGrindstoneTime(stack);
            currentItemMillTime = 0;
            markDirty();
        }
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return index != 1 && index == 0 && GrindstoneRecipes.instance().hasRecipe(stack);
    }

    @Override
    public int getField(int id) {
        switch (id) {
            case 0:
                return this.totalItemMillTime;
            case 1:
                return this.currentItemMillTime;
            default:
                return 0;
        }
    }

    @Override
    public void setField(int id, int value) {
        switch (id) {
            case 0:
                this.totalItemMillTime = value;
                break;
            case 1:
                this.currentItemMillTime = value;
        }
    }

    @Override
    public int getFieldCount() {
        return 2;
    }

    @Override
    public String getName() {
        return "container.mill";
    }
}
