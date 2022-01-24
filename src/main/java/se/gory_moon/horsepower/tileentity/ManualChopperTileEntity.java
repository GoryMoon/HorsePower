package se.gory_moon.horsepower.tileentity;

import com.tterrag.registrate.util.nullness.NonnullType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.ToolType;
import org.apache.commons.lang3.tuple.Pair;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.HPEventHandler;
import se.gory_moon.horsepower.recipes.AbstractHPRecipe.Type;
import se.gory_moon.horsepower.recipes.HPRecipes;
import se.gory_moon.horsepower.recipes.serializers.RecipeSerializers;

import java.util.Map;

public class ManualChopperTileEntity extends HPBaseTileEntity {

    private int currentItemChopAmount;
    private int totalItemChopAmount;

    public ManualChopperTileEntity(@NonnullType TileEntityType<ManualChopperTileEntity> tileEntityType) {
        super(2, tileEntityType);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putInt("chopTime", currentItemChopAmount);
        compound.putInt("totalChopTime", totalItemChopAmount);

        return super.write(compound);
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        super.read(state, compound);

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
        return index != 1 && index == 0 && getStackInSlot(1).isEmpty() && getStackInSlot(0).isEmpty()  && getRecipe(stack) != null;
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
                                            
                double baseAmount = getBaseAmount(held, player) / 100D;
                int chance = getChance(held, player);

                result.setCount((int) Math.ceil(result.getCount() * baseAmount));
                if (chance >= 100 || world.rand.nextInt(100) < chance)
                    result.grow(1);

                if (Configs.SERVER.choppingBlockDrop.get().booleanValue()) {
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

    public static int getBaseAmount(ItemStack held, PlayerEntity player) {
        int baseAmount = 100;
        int harvestLevel = held.getItem().getHarvestLevel(held, ToolType.AXE, player, null);
        if (harvestLevel > -1 && HPEventHandler.harvestPercentages.get(Integer.valueOf(harvestLevel)) != null) {
            baseAmount = HPEventHandler.harvestPercentages.get(Integer.valueOf(harvestLevel)).getLeft().intValue();
        }
        for (Map.Entry<ItemStack, Pair<Integer, Integer>> entry: HPEventHandler.choppingAxes.entrySet()) {
            if (entry.getKey().isItemEqual(held)) {
                return entry.getValue().getLeft().intValue();
            }
        }
        return baseAmount;
    }

    public static int getChance(ItemStack held, PlayerEntity player) {
        int chance = 0;
        int harvestLevel = held.getItem().getHarvestLevel(held, ToolType.AXE, player, null);
        if (harvestLevel > -1 && HPEventHandler.harvestPercentages.get(Integer.valueOf(harvestLevel)) != null) {
            chance = HPEventHandler.harvestPercentages.get(Integer.valueOf(harvestLevel)).getRight().intValue();
        }
        for (Map.Entry<ItemStack, Pair<Integer, Integer>> entry: HPEventHandler.choppingAxes.entrySet()) {
            if (entry.getKey().isItemEqual(held)) {
                return entry.getValue().getRight().intValue();
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
    public ITextComponent getDisplayName() {
        return null;
    }

    @Override
    public int getOutputSlot() {
        return 1;
    }

    @Override
    public IRecipeType<? extends IRecipe<IInventory>> getRecipeType() {
        return RecipeSerializers.CHOPPING_TYPE;
    }

    @Override
    protected Type getHPRecipeType() {
        return Type.MANUAL;
    }

    public long getCurrentProgress() {
       return  totalItemChopAmount > 0 ? ((currentItemChopAmount * 100) / totalItemChopAmount)  : 0; //we do not need to cast do float or double here, 0.6 does not matter at this point
    }
}