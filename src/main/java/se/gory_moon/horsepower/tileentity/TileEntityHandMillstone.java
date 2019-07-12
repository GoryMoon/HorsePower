package se.gory_moon.horsepower.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.blocks.ModBlocks;
import se.gory_moon.horsepower.recipes.HPRecipeBase;
import se.gory_moon.horsepower.recipes.HPRecipes;

public class TileEntityHandMillstone extends TileEntityHPBase implements ITickableTileEntity {

    private int currentItemMillTime;
    private int totalItemMillTime;

    private final int ticksPerRotation = 18;
    private float visibleRotation = 0;
    private int currentTicks = 0;
    private int rotation = 0;


    public TileEntityHandMillstone() {
        super(3, ModBlocks.handMillstoneTile);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putInt("millTime", currentItemMillTime);
        compound.putInt("totalMillTime", totalItemMillTime);
        compound.putInt("currentRotation", rotation);

        return super.write(compound);
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);

        if (getStackInSlot(0).getCount() > 0) {
            currentItemMillTime = compound.getInt("millTime");
            totalItemMillTime = compound.getInt("totalMillTime");
            rotation = compound.getInt("currentRotation");
        } else {
            currentItemMillTime = 0;
            totalItemMillTime = 1;
            rotation = 0;
        }
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return super.getRenderBoundingBox();
    }

    @Override
    public ItemStack getRecipeItemStack() {
        return HPRecipes.instance().getMillstoneResult(getStackInSlot(0), true);
    }

    @Override
    public HPRecipeBase getRecipe() {
        return HPRecipes.instance().getMillstoneRecipe(getStackInSlot(0), true);
    }

    private void millItem() {
        if (!world.isRemote && canWork()) {
            HPRecipeBase recipe = getRecipe();
            ItemStack result = recipe.getOutput();
            ItemStack secondary = recipe.getSecondary();

            ItemStack input = getStackInSlot(0);
            ItemStack output = getStackInSlot(1);
            ItemStack secondaryOutput = getStackInSlot(2);

            if (output.isEmpty()) {
                setInventorySlotContents(1, result.copy());
            } else if (output.isItemEqual(result)) {
                output.grow(result.getCount());
            }
            processSecondaries(getWorld(), secondary, secondaryOutput, recipe, this);

            input.shrink(1);
        }
    }

    public static void processSecondaries(World world, ItemStack secondary, ItemStack secondaryOutput, HPRecipeBase recipe, TileEntityHPBase teBase) {
        if (!secondary.isEmpty()) {
            int recipeChance = recipe.getSecondaryChance();
            if (recipeChance >= 100 || world.rand.nextInt(100) < recipeChance) {
                if (secondaryOutput.isEmpty()) {
                    teBase.setInventorySlotContents(2, secondary.copy());
                } else if (secondaryOutput.isItemEqual(secondary)) {
                    secondaryOutput.grow(secondary.getCount());
                }
            }
        }
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (getStackInSlot(0).isEmpty())
            currentItemMillTime = 0;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        ItemStack itemstack = getStackInSlot(index);
        super.setInventorySlotContents(index, stack);

        boolean flag = !stack.isEmpty() && stack.isItemEqual(itemstack) && ItemStack.areItemStackTagsEqual(stack, itemstack);
        if (index == 0 && !flag) {
            totalItemMillTime = HPRecipes.instance().getMillstoneTime(stack, true);
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
        return index == 0 && HPRecipes.instance().hasMillstoneRecipe(stack, true);
    }

    @Override
    public ITextComponent getName() {
        return new StringTextComponent("container.hand_mill");
    }

    @Override
    public int getOutputSlot() {
        return 2;
    }

    public boolean turn() {
        if (getWorld().isRemote)
            return false;

        if (rotation < 3 && canWork()) {
            rotation += ticksPerRotation;
            markDirty();
            return true;
        }
        return false;
    }

    @Override
    public void tick() {
        if (rotation > 0) {

            visibleRotation = (visibleRotation - 360 / (ticksPerRotation)) % -360;
            currentTicks++;
            if (currentTicks >= ticksPerRotation) {
                currentTicks -= ticksPerRotation;

                currentItemMillTime += Configs.general.pointsPerRotation;

                if (currentItemMillTime >= totalItemMillTime) {
                    currentItemMillTime = 0;

                    millItem();
                    totalItemMillTime = HPRecipes.instance().getMillstoneTime(getStackInSlot(0), true);
                }
                markDirty();
            }

            rotation--;
        } else {
            visibleRotation = 0;
        }
    }

    @Override
    public ITextComponent getDisplayName() {
        return null;
    }

    public float getVisibleRotation() {
        return visibleRotation;
    }

    @Override
    public boolean canBeRotated() {
        return true;
    }
}
