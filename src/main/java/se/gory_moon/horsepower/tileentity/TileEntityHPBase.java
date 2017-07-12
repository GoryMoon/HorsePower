package se.gory_moon.horsepower.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import se.gory_moon.horsepower.recipes.HPRecipeBase;

import javax.annotation.Nullable;

public abstract class TileEntityHPBase extends TileEntity implements ISidedInventory {

    protected NonNullList<ItemStack> itemStacks = NonNullList.withSize(3, ItemStack.EMPTY);

    private EnumFacing forward = null;

    public TileEntityHPBase(int inventorySize) {
        NonNullList.withSize(inventorySize, ItemStack.EMPTY);
    }

    public abstract HPRecipeBase getRecipe();

    public abstract ItemStack getRecipeItemStack();

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        itemStacks = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, itemStacks);

        if (canBeRotated()) {
            forward = EnumFacing.byName(compound.getString("forward"));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        ItemStackHelper.saveAllItems(compound, itemStacks);

        if (canBeRotated()) {
            compound.setString("forward", getForward().getName());
        }
        return compound;
    }

    @Override
    public void markDirty() {
        if (!getWorld().isRemote) {
            final IBlockState state = getWorld().getBlockState(getPos());
            getWorld().notifyBlockUpdate(getPos(), state, state, 8);
            super.markDirty();
        }
    }

    public boolean canWork() {
        if (getStackInSlot(0).isEmpty()) {
            return false;
        } else {
            HPRecipeBase recipeBase = getRecipe();
            if (recipeBase == null) return false;

            ItemStack itemstack = recipeBase.getOutput();
            ItemStack secondary = recipeBase.getSecondary();

            if (itemstack.isEmpty()) {
                return false;
            } else {
                ItemStack output = getStackInSlot(1);
                ItemStack outputSecondary = getStackInSlot(2);
                if (!secondary.isEmpty() && !outputSecondary.isEmpty()) {
                    if (!outputSecondary.isItemEqual(secondary)) return false;
                    if (outputSecondary.getCount() + secondary.getCount() > secondary.getMaxStackSize()) return false;
                }
                return output.isEmpty() || output.isItemEqual(itemstack) && output.getCount() + itemstack.getCount() <= output.getMaxStackSize();
            }
        }
    }

    public static boolean canCombine(ItemStack stack1, ItemStack stack2) {
        return stack1.getItem() == stack2.getItem() && (stack1.getMetadata() == stack2.getMetadata() && (stack1.getCount() <= stack1.getMaxStackSize() && ItemStack.areItemStackTagsEqual(stack1, stack2)));
    }

    public boolean canBeRotated() {
        return false;
    }

    public EnumFacing getForward() {
        if (forward == null)
            return EnumFacing.NORTH;
        return forward;
    }

    public void setForward(EnumFacing forward) {
        this.forward = forward;
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), -999, getUpdateTag());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        handleUpdateTag(pkt.getNbtCompound());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        readFromNBT(tag);
        markDirty();
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return direction == EnumFacing.DOWN && (index == 1 || index == 2);
    }

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
        itemStacks.set(index, stack);

        if (index == 0 && stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return this.world.getTileEntity(this.pos) == this && player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory(EntityPlayer player) {}

    @Override
    public void closeInventory(EntityPlayer player) {}

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {}

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        itemStacks.clear();
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    private IItemHandler handlerTop = new SidedInvWrapper(this, EnumFacing.UP);
    private IItemHandler handlerBottom = new SidedInvWrapper(this, EnumFacing.DOWN);

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @javax.annotation.Nullable net.minecraft.util.EnumFacing facing) {
        if (facing != null && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            if (facing == EnumFacing.DOWN)
                return (T) handlerBottom;
            else if (facing == EnumFacing.UP)
                return (T) handlerTop;
        return super.getCapability(capability, facing);
    }
}
