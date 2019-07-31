package se.gory_moon.horsepower.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.INameable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.RangedWrapper;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import se.gory_moon.horsepower.blocks.BlockHPBase;
import se.gory_moon.horsepower.recipes.AbstractHPRecipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class HPBaseTileEntity extends TileEntity implements INameable {

    protected NonNullList<ItemStack> itemStacks = NonNullList.withSize(3, ItemStack.EMPTY);
    protected IInventoryHP inventory;
    private RecipeWrapper recipeWrapperDummy = new RecipeWrapper(new ItemStackHandler());
    private LazyOptional<IItemHandler> handlerNull;
    private LazyOptional<IItemHandler> handlerBottom;
    private LazyOptional<IItemHandler> handlerIn;

    public HPBaseTileEntity(int inventorySize, TileEntityType type) {
        super(type);
        itemStacks = NonNullList.withSize(inventorySize, ItemStack.EMPTY);

        inventory = new IInventoryHP() {
            @Override
            public int getSizeInventory() {
                return itemStacks.size();
            }

            @Override
            public boolean isEmpty() {
                for (ItemStack itemstack : itemStacks) {
                    if (!itemstack.isEmpty()) {
                        return false;
                    }
                }

                return true;
            }

            @Override
            public ItemStack getStackInSlot(int index) {
                if (index >= itemStacks.size())
                    return ItemStack.EMPTY;
                return itemStacks.get(index);
            }

            @Override
            public ItemStack decrStackSize(int index, int count) {
                ItemStack stack = ItemStackHelper.getAndSplit(itemStacks, index, count);
                if (!stack.isEmpty())
                    markDirty();
                return stack;
            }

            @Override
            public ItemStack removeStackFromSlot(int index) {
                ItemStack stack = ItemStackHelper.getAndRemove(itemStacks, index);
                return stack;
            }

            @Override
            public void setInventorySlotContents(int index, ItemStack stack) {
                HPBaseTileEntity.this.setInventorySlotContents(index, stack);
            }

            @Override
            public int getInventoryStackLimit() {
                return HPBaseTileEntity.this.getInventoryStackLimit();
            }

            @Override
            public void markDirty() {
                HPBaseTileEntity.this.markDirty();
            }

            @Override
            public boolean isUsableByPlayer(PlayerEntity player) {
                return getWorld().getTileEntity(getPos()) == HPBaseTileEntity.this && player.getDistanceSq((double) getPos().getX() + 0.5D, (double) getPos().getY() + 0.5D, (double) getPos().getZ() + 0.5D) <= 64.0D;
            }

            @Override
            public boolean isItemValidForSlot(int index, ItemStack stack) {
                return HPBaseTileEntity.this.isItemValidForSlot(index, stack);
            }

            @Override
            public void setSlotContent(int index, ItemStack stack) {
                itemStacks.set(index, stack);

                if (index == 0 && stack.getCount() > this.getInventoryStackLimit(stack)) {
                    stack.setCount(this.getInventoryStackLimit(stack));
                }
            }

            public int getInventoryStackLimit(ItemStack stack) {
                return HPBaseTileEntity.this.getInventoryStackLimit(stack);
            }

            @Override
            public void clear() {
                itemStacks.clear();
            }
        };
        handlerIn = LazyOptional.of(() -> new RangedWrapper(new InvWrapper(inventory), 0, 1));
        handlerBottom = LazyOptional.of(() -> new RangedWrapper(new InvWrapper(inventory), 1, getOutputSlot() + 1));
        handlerNull = LazyOptional.of(() -> new InvWrapper(inventory));
    }

    public static boolean canCombine(ItemStack stack1, ItemStack stack2) {
        return stack1.getItem() == stack2.getItem() && (stack1.getCount() <= stack1.getMaxStackSize() && ItemStack.areItemStackTagsEqual(stack1, stack2));
    }

    public AbstractHPRecipe getRecipe() {
        return getRecipe(inventory);
    }

    public AbstractHPRecipe getRecipe(ItemStack stack) {
        recipeWrapperDummy.setInventorySlotContents(0, stack);
        return getRecipe(recipeWrapperDummy);
    }

    public AbstractHPRecipe getRecipe(IInventory inventory) {
        return validateRecipe((AbstractHPRecipe) this.world.getRecipeManager().getRecipe(getRecipeType(), inventory, this.world).orElse(null));
    }

    public AbstractHPRecipe validateRecipe(AbstractHPRecipe recipe) {
        return recipe;
    }

    public abstract IRecipeType<? extends IRecipe<IInventory>> getRecipeType();

    public abstract int getInventoryStackLimit();

    public abstract boolean isItemValidForSlot(int index, ItemStack stack);

    public abstract int getOutputSlot();

    public ItemStack getStackInSlot(int index) {
        return inventory.getStackInSlot(index);
    }

    public ItemStack removeStackFromSlot(int index) {
        return inventory.removeStackFromSlot(index);
    }

    public IInventoryHP getInventory() {
        return inventory;
    }

    public void setInventorySlotContents(int index, ItemStack stack) {
        inventory.setSlotContent(index, stack);
    }

    public int getInventoryStackLimit(ItemStack stack) {
        return getInventoryStackLimit();
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);

        itemStacks = NonNullList.withSize(inventory.getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, itemStacks);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        ItemStackHelper.saveAllItems(compound, itemStacks);

        return compound;
    }

    @Override
    public void markDirty() {
        final BlockState state = getWorld().getBlockState(getPos());
        getWorld().notifyBlockUpdate(getPos(), state, state, 2);
        super.markDirty();
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(getPos(), -999, getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return write(new CompoundNBT());
    }

    public boolean canWork() {
        if (getStackInSlot(0).isEmpty()) {
            return false;
        } else {
            AbstractHPRecipe recipe = getRecipe();
            if (recipe == null)
                return false;

            ItemStack itemstack = recipe.getRecipeOutput();
            ItemStack secondary = recipe.getSecondaryOutput();

            if (getStackInSlot(0).getCount() < 1) //TODO input.count()
                return false;
            if (itemstack.isEmpty())
                return false;

            ItemStack output = getStackInSlot(1);
            ItemStack outputSecondary = secondary.isEmpty() ? ItemStack.EMPTY: inventory.getStackInSlot(2);
            if (!secondary.isEmpty() && !outputSecondary.isEmpty()) {
                if (!outputSecondary.isItemEqual(secondary))
                    return false;
                if (outputSecondary.getCount() + secondary.getCount() > secondary.getMaxStackSize())
                    return false;
            }
            return output.isEmpty() || output.isItemEqual(itemstack) && output.getCount() + itemstack.getCount() <= output.getMaxStackSize();
        }
    }

    public boolean canBeRotated() {
        return false;
    }

    public Direction getForward() {
        return canBeRotated() ? getWorld().getBlockState(getPos()).getBlockState().get(BlockHPBase.FACING): Direction.NORTH;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        handleUpdateTag(pkt.getNbtCompound());
    }

    @Override
    public void handleUpdateTag(CompoundNBT tag) {
        read(tag);
        markDirty();
    }

    @Override
    protected void invalidateCaps() {
        handlerNull.invalidate();
        handlerBottom.invalidate();
        handlerIn.invalidate();
        super.invalidateCaps();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (side == null)
                return handlerNull.cast();
            else if (side == Direction.DOWN)
                return handlerBottom.cast();
            else
                return handlerIn.cast();
        }
        return super.getCapability(cap);
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Nullable
    @Override
    public ITextComponent getCustomName() {
        return null;
    }
}
