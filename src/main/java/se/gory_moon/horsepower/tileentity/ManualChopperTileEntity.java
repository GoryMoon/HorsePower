package se.gory_moon.horsepower.tileentity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.RangedWrapper;
import org.apache.commons.lang3.tuple.Pair;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.HPEventHandler;
import se.gory_moon.horsepower.HorsePower;
import se.gory_moon.horsepower.Registration;
import se.gory_moon.horsepower.recipes.AbstractHPRecipe;
import se.gory_moon.horsepower.recipes.ChoppingRecipe;
import se.gory_moon.horsepower.recipes.HPRecipeBase;
import se.gory_moon.horsepower.recipes.HPRecipes;
import se.gory_moon.horsepower.recipes.RecipeSerializers;

import javax.annotation.Nullable;
import java.util.Map;

public class ManualChopperTileEntity extends HPBaseTileEntity {

    private int currentItemChopAmount;
    private int totalItemChopAmount;

    public ManualChopperTileEntity() {
        super(2,Registration.MANUAL_CHOPPER_TILE.get());
        handlerSide = new RangedWrapper(new InvWrapper(inventory), 0, 1);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putInt("chopTime", currentItemChopAmount);
        compound.putInt("totalChopTime", totalItemChopAmount);

        return super.write(compound);
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);

        if (getStackInSlot(0).getCount() > 0) {
            currentItemChopAmount = compound.getInt("chopTime");
            totalItemChopAmount = compound.getInt("totalChopTime");
        } else {
            currentItemChopAmount = 0;
            totalItemChopAmount = 1;
        }
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return index != 1 && index == 0 && HPRecipes.hasTypeRecipe(getRecipe(stack), AbstractHPRecipe.Type.MANUAL) && getStackInSlot(1).isEmpty() && getStackInSlot(0).isEmpty();
    }

    public boolean chop(PlayerEntity player, ItemStack held) {
        if (canWork()) {
            currentItemChopAmount++;

            if (currentItemChopAmount >= totalItemChopAmount) {
                currentItemChopAmount = 0;

                totalItemChopAmount = HPRecipes.getTypeTime(getRecipe(), null);
                chopItem(player, held);
                return true;
            }
            markDirty();
        }
        return false;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        ItemStack itemstack = getStackInSlot(index);
        super.setInventorySlotContents(index, stack);

        if (index == 1 && getStackInSlot(1).isEmpty()) {
            markDirty();
        }

        boolean flag = !stack.isEmpty() && stack.isItemEqual(itemstack) && ItemStack.areItemStackTagsEqual(stack, itemstack);
        if (index == 0 && !flag) {
            totalItemChopAmount = HPRecipes.getTypeTime(getRecipe(), null);
            currentItemChopAmount = 0;
            markDirty();
        }
    }

    private void chopItem(PlayerEntity player, ItemStack held) {
        if (canWork()) {
            ItemStack input = getStackInSlot(0);
            if (!getWorld().isRemote) {
                ItemStack output = getStackInSlot(1); //current output slot
                ItemStack result =  getRecipe().getCraftingResult(inventory); //crafting recipe output
                                            
                double baseAmount = ((double) getBaseAmount(held, player)) / 100D;
                int chance = getChance(held, player);

                result.setCount((int) Math.ceil((double) result.getCount() * baseAmount));
                if (chance >= 100 || world.rand.nextInt(100) < chance)
                    result.grow(1);

                if (Boolean.TRUE) { //FIXME Config chopping block drop    -- Configs.general.choppingBlockDrop
                    InventoryHelper.spawnItemStack(getWorld(), getPos().getX(), getPos().getY() + 0.5, getPos().getZ(), result);
                } else {
                    if (output.isEmpty()) {
                        setInventorySlotContents(1, result.copy());
                    } else if (output.isItemEqual(result)) {
                        output.grow(result.getCount());
                    }
                }
            }
            getWorld().playSound(player, getPos(), SoundEvents.BLOCK_WOOD_BREAK, SoundCategory.BLOCKS, 1.0f, 1.0f);
            input.shrink(1);
            markDirty();
        }
    }

    private int getBaseAmount(ItemStack held, PlayerEntity player) {
        int baseAmount = 100;
        int harvestLevel = held.getItem().getHarvestLevel(held, ToolType.AXE, player, null);
        if (harvestLevel > -1 && HPEventHandler.harvestPercentages.get(harvestLevel) != null) {
            baseAmount = HPEventHandler.harvestPercentages.get(harvestLevel).getLeft();
        }
        for (Map.Entry<ItemStack, Pair<Integer, Integer>> entry: HPEventHandler.choppingAxes.entrySet()) {
            if (entry.getKey().isItemEqual(held)) {
                return entry.getValue().getLeft();
            }
        }
        return baseAmount;
    }

    private int getChance(ItemStack held, PlayerEntity player) {
        int chance = 0;
        int harvestLevel = held.getItem().getHarvestLevel(held, ToolType.AXE, player, null);
        if (harvestLevel > -1 && HPEventHandler.harvestPercentages.get(harvestLevel) != null) {
            chance = HPEventHandler.harvestPercentages.get(harvestLevel).getRight();
        }
        for (Map.Entry<ItemStack, Pair<Integer, Integer>> entry: HPEventHandler.choppingAxes.entrySet()) {
            if (entry.getKey().isItemEqual(held)) {
                return entry.getValue().getRight();
            }
        }
        return chance;
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public ITextComponent getName() {
        return new StringTextComponent("container.manual_chopper");
    }

    @Override
    public int getOutputSlot() {
        return 1;
    }

    @Override
    public AbstractHPRecipe validateRecipe(AbstractHPRecipe recipe) {
        return recipe instanceof ChoppingRecipe ? recipe : null;
    }

    @Override
    public IRecipeType<? extends IRecipe<IInventory>> getRecipeType() {
        return RecipeSerializers.CHOPPING_TYPE;
    }
    
    
    private IItemHandler handlerSide = null;
}