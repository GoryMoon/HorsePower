package se.gory_moon.horsepower.tileentity;

import com.google.common.collect.Lists;
import com.tterrag.registrate.util.nullness.NonnullType;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import se.gory_moon.horsepower.blocks.MillstoneBlock;
import se.gory_moon.horsepower.recipes.AbstractHPRecipe;
import se.gory_moon.horsepower.recipes.AbstractHPRecipe.Type;
import se.gory_moon.horsepower.recipes.HPRecipes;
import se.gory_moon.horsepower.recipes.RecipeSerializers;
import se.gory_moon.horsepower.util.Localization;

import javax.annotation.Nullable;

public class MillstoneTileEntity extends HPHorseBaseTileEntity {

    public ItemStack renderStack = ItemStack.EMPTY;
    public int millColor = -1;
    private int currentItemMillTime;
    private int totalItemMillTime;

    public MillstoneTileEntity(@NonnullType TileEntityType<MillstoneTileEntity> tileEntityType) {
        super(3, tileEntityType);
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

        for (BlockPos pos : searchPos) {
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

            totalItemMillTime = HPRecipes.getTypeTime(getRecipe(), AbstractHPRecipe.Type.HORSE);
            millItem();
            return true;
        }
        return false;
    }

    @Override
    public int getPositionOffset() {
        return -1;
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        super.read(state, compound);

        if (getStackInSlot(0).getCount() > 0) {
            currentItemMillTime = compound.getInt("millTime");
            totalItemMillTime = compound.getInt("totalMillTime");
        } else {
            currentItemMillTime = 0;
            totalItemMillTime = 1;
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putInt("millTime", currentItemMillTime);
        compound.putInt("totalMillTime", totalItemMillTime);

        return super.write(compound);
    }

    @Override
    protected Type getHPRecipeType() {
        return Type.HORSE;
    }

    @Override
    public IRecipeType<? extends IRecipe<IInventory>> getRecipeType() {
        return RecipeSerializers.MILLING_TYPE;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return index == 0 && getRecipe(stack) != null;
    }

    @Override
    public int getOutputSlot() {
        return 2;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        ItemStack itemstack = getStackInSlot(index);
        super.setInventorySlotContents(index, stack);

        if ((index == 1 || index == 2) && getStackInSlot(1).isEmpty() && getStackInSlot(2).isEmpty())
            MillstoneBlock.setState(false, world, pos);

        boolean flag = !stack.isEmpty() && stack.isItemEqual(itemstack) && ItemStack.areItemStackTagsEqual(stack, itemstack);
        if (index == 0 && !flag) {
            totalItemMillTime = HPRecipes.getTypeTime(getRecipe(), AbstractHPRecipe.Type.HORSE);
            currentItemMillTime = 0;
        }
        markDirty();
    }

    @Override
    public void markDirty() {
        if (getStackInSlot(1).isEmpty() && getStackInSlot(2).isEmpty())
            MillstoneBlock.setState(false, world, pos);

        if (getStackInSlot(0).isEmpty())
            currentItemMillTime = 0;

        super.markDirty();
    }

    private void millItem() {
        if (canWork()) {
            ManualMillstoneTileEntity.millItem(inventory, this);
            MillstoneBlock.setState(true, world, pos);
        }
    }

    public int getCurrentItemMillTime() {
        return currentItemMillTime;
    }

    public int getTotalItemMillTime() {
        return totalItemMillTime;
    }

    @Override
    public ITextComponent getName() {
        return new StringTextComponent("container.mill");
    }

    @Nullable
    @Override
    public ITextComponent getDisplayName() {
        if (valid)
            return null;
        else
            return new TranslationTextComponent(Localization.INFO.MILLSTONE_INVALID.key()).setStyle(Style.EMPTY.applyFormatting(TextFormatting.RED));
    }
}
