package se.gory_moon.horsepower.tileentity;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.Registration;
import se.gory_moon.horsepower.recipes.AbstractHPRecipe;
import se.gory_moon.horsepower.recipes.HPRecipes;
import se.gory_moon.horsepower.recipes.RecipeSerializers;

public class ManualMillstoneTileEntity extends HPBaseTileEntity implements ITickableTileEntity {

    private final int ticksPerRotation = 18;
    private int currentItemMillTime;
    private int totalItemMillTime;
    private float visibleRotation = 0;
    private int currentTicks = 0;
    private int rotation = 0;


    public ManualMillstoneTileEntity() {
        super(3, Registration.MANUAL_MILLSTONE_TILE.get());
    }

    public static void millItem(IInventoryHP inventory, HPBaseTileEntity te) {
        AbstractHPRecipe recipe = te.getRecipe();
        ItemStack result = recipe.getCraftingResult(inventory);
        ItemStack secondary = recipe.getCraftingSecondary();

        ItemStack input = te.getStackInSlot(0);
        ItemStack output = te.getStackInSlot(1);
        ItemStack secondaryOutput = te.getStackInSlot(2);

        if (output.isEmpty()) {
            te.setInventorySlotContents(1, result.copy());
        } else if (output.isItemEqual(result)) {
            output.grow(result.getCount());
        }
        processSecondaries(te.getWorld(), secondary, secondaryOutput, recipe, te);

        input.shrink(1);
    }

    public static void processSecondaries(World world, ItemStack secondary, ItemStack secondaryOutput, AbstractHPRecipe recipe, HPBaseTileEntity teBase) {
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
    public AbstractHPRecipe validateRecipe(AbstractHPRecipe recipe) {
        return HPRecipes.checkTypeRecipe(recipe, AbstractHPRecipe.Type.MANUAL);
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
        return index == 0 && HPRecipes.hasTypeRecipe(getRecipe(stack), AbstractHPRecipe.Type.MANUAL);
    }

    @Override
    public int getOutputSlot() {
        return 2;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        ItemStack itemstack = getStackInSlot(index);
        super.setInventorySlotContents(index, stack);

        boolean flag = !stack.isEmpty() && stack.isItemEqual(itemstack) && ItemStack.areItemStackTagsEqual(stack, itemstack);
        if (index == 0 && !flag) {
            totalItemMillTime = HPRecipes.getTypeTime(getRecipe(), AbstractHPRecipe.Type.MANUAL);
            currentItemMillTime = 0;
        }
        markDirty();
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
    public CompoundNBT write(CompoundNBT compound) {
        compound.putInt("millTime", currentItemMillTime);
        compound.putInt("totalMillTime", totalItemMillTime);
        compound.putInt("currentRotation", rotation);

        return super.write(compound);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (getStackInSlot(0).isEmpty())
            currentItemMillTime = 0;
    }

    @Override
    public boolean canBeRotated() {
        return true;
    }

    private void millItem() {
        if (!world.isRemote && canWork()) {
            millItem(inventory, this);
        }
    }

    @Override
    public ITextComponent getName() {
        return new StringTextComponent("container.hand_mill");
    }

    @Override
    public ITextComponent getDisplayName() {
        return null;
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

                currentItemMillTime += Configs.SERVER.pointsPerRotation.get();

                if (currentItemMillTime >= totalItemMillTime) {
                    currentItemMillTime = 0;

                    millItem();
                    totalItemMillTime = HPRecipes.getTypeTime(getRecipe(), AbstractHPRecipe.Type.MANUAL);
                }
                markDirty();
            }

            rotation--;
        } else {
            visibleRotation = 0;
        }
    }

    public int getCurrentItemMillTime() {
        return currentItemMillTime;
    }

    public int getTotalItemMillTime() {
        return totalItemMillTime;
    }

    public float getVisibleRotation() {
        return visibleRotation;
    }
}
