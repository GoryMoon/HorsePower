package se.gory_moon.horsepower.tileentity;

import com.google.common.collect.Lists;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import se.gory_moon.horsepower.blocks.BlockMillstone;
import se.gory_moon.horsepower.blocks.ModBlocks;
import se.gory_moon.horsepower.recipes.HPRecipes;
import se.gory_moon.horsepower.recipes.RecipeSerializers;
import se.gory_moon.horsepower.util.Localization;

import javax.annotation.Nullable;

public class MillstoneTileEntity extends HPHorseBaseTileEntity {

    private int currentItemMillTime;
    private int totalItemMillTime;

    public ItemStack renderStack = ItemStack.EMPTY;
    public int millColor = -1;

    public MillstoneTileEntity() {
        super(3, ModBlocks.MILLSTONE_TILE.orElseThrow(IllegalStateException::new));
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putInt("millTime", currentItemMillTime);
        compound.putInt("totalMillTime", totalItemMillTime);

        return super.write(compound);
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);

        if (getStackInSlot(0).getCount() > 0) {
            currentItemMillTime = compound.getInt("millTime");
            totalItemMillTime = compound.getInt("totalMillTime");
        } else {
            currentItemMillTime = 0;
            totalItemMillTime = 1;
        }
    }

    @Override
    public void markDirty() {
        if (getStackInSlot(1).isEmpty() && getStackInSlot(2).isEmpty())
            BlockMillstone.setState(false, world, pos);

        if (getStackInSlot(0).isEmpty())
            currentItemMillTime = 0;

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
                    searchPos.add(getPos().add(x, -1, z));
                }
            }
        }

        for (BlockPos pos: searchPos) {
            if (!getWorld().getBlockState(pos).getMaterial().isReplaceable())
                return false;
        }
        return true;
    }

    @Override
    public boolean targetReached() {
        currentItemMillTime++;

        if (currentItemMillTime >= totalItemMillTime) {
            currentItemMillTime = 0;

            totalItemMillTime = HPRecipes.instance().getMillstoneTime(getStackInSlot(0), false);
            millItem();
            return true;
        }
        return false;
    }

    @Override
    public IRecipeType<? extends IRecipe<IInventory>> getRecipeType() {
        return RecipeSerializers.MILLING_TYPE;
    }

    @Override
    public int getPositionOffset() {
        return -1;
    }

    private void millItem() {
        if (canWork()) {
            HandMillstoneTileEntity.millItem(inventory, this);
            BlockMillstone.setState(true, world, pos);
        }
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        ItemStack itemstack = getStackInSlot(index);
        super.setInventorySlotContents(index, stack);

        if ((index == 1 || index == 2) && getStackInSlot(1).isEmpty() && getStackInSlot(2).isEmpty())
            BlockMillstone.setState(false, world, pos);

        boolean flag = !stack.isEmpty() && stack.isItemEqual(itemstack) && ItemStack.areItemStackTagsEqual(stack, itemstack);
        if (index == 0 && !flag) {
            totalItemMillTime = HPRecipes.instance().getMillstoneTime(stack, false);
            currentItemMillTime = 0;
        }
        markDirty();
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return index == 0 && HPRecipes.instance().hasMillstoneRecipe(stack, false);
    }

    @Override
    public ITextComponent getName() {
        return new StringTextComponent("container.mill");
    }

    @Override
    public int getOutputSlot() {
        return 2;
    }

    @Nullable
    @Override
    public ITextComponent getDisplayName() {
        if (valid)
            return null;
        else
            return new TranslationTextComponent(Localization.INFO.MILLSTONE_INVALID.key()).setStyle(new Style().setColor(TextFormatting.RED));
    }
}
