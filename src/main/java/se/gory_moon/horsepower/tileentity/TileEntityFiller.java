package se.gory_moon.horsepower.tileentity;

import net.minecraft.block.BlockDirectional;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;

public class TileEntityFiller extends TileEntity implements ISidedInventory {

    private TileEntityHPBase getFilledTileEntity() {
        EnumFacing facing = getWorld().getBlockState(getPos()).getValue(BlockDirectional.FACING);
        TileEntity tileEntity = getWorld().getTileEntity(pos.offset(facing));
        if (tileEntity instanceof TileEntityHPBase) {
            return (TileEntityHPBase) tileEntity;
        }
        return null;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        TileEntityHPBase te = getFilledTileEntity();
        if (te != null)
            return te.getSlotsForFace(side);
        return new int[0];
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        TileEntityHPBase te = getFilledTileEntity();
        if (te != null)
            return te.canInsertItem(index, itemStackIn, direction);
        return false;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        TileEntityHPBase te = getFilledTileEntity();
        if (te != null)
            return te.canExtractItem(index, stack, direction);
        return false;
    }

    @Override
    public int getSizeInventory() {
        TileEntityHPBase te = getFilledTileEntity();
        if (te != null)
            return te.getSizeInventory();
        return 0;
    }

    @Override
    public boolean isEmpty() {
        TileEntityHPBase te = getFilledTileEntity();
        if (te != null)
            return te.isEmpty();
        return false;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        TileEntityHPBase te = getFilledTileEntity();
        if (te != null)
            return te.getStackInSlot(index);
        return null;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        TileEntityHPBase te = getFilledTileEntity();
        if (te != null)
            return te.decrStackSize(index, count);
        return null;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        TileEntityHPBase te = getFilledTileEntity();
        if (te != null)
            return te.removeStackFromSlot(index);
        return null;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        TileEntityHPBase te = getFilledTileEntity();
        if (te != null)
            te.setInventorySlotContents(index, stack);
    }

    @Override
    public int getInventoryStackLimit() {
        TileEntityHPBase te = getFilledTileEntity();
        if (te != null)
            return te.getInventoryStackLimit();
        return 0;
    }

    @Override
    public void markDirty() {
        TileEntityHPBase te = getFilledTileEntity();
        if (te != null)
            te.markDirty();
        super.markDirty();
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        TileEntityHPBase te = getFilledTileEntity();
        if (te != null)
            te.isUsableByPlayer(player);
        return false;
    }

    @Override
    public void openInventory(EntityPlayer player) {
        TileEntityHPBase te = getFilledTileEntity();
        if (te != null)
            te.openInventory(player);
    }

    @Override
    public void closeInventory(EntityPlayer player) {
        TileEntityHPBase te = getFilledTileEntity();
        if (te != null)
            te.closeInventory(player);
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        TileEntityHPBase te = getFilledTileEntity();
        if (te != null)
            return te.isItemValidForSlot(index, stack);
        return false;
    }

    @Override
    public int getField(int id) {
        TileEntityHPBase te = getFilledTileEntity();
        if (te != null)
            return getField(id);
        return 0;
    }

    @Override
    public void setField(int id, int value) {
        TileEntityHPBase te = getFilledTileEntity();
        if (te != null)
            te.setField(id, value);
    }

    @Override
    public int getFieldCount() {
        TileEntityHPBase te = getFilledTileEntity();
        if (te != null)
            return te.getFieldCount();
        return 0;
    }

    @Override
    public void clear() {
        TileEntityHPBase te = getFilledTileEntity();
        if (te != null)
            te.clear();
    }

    @Override
    public String getName() {
        TileEntityHPBase te = getFilledTileEntity();
        if (te != null)
            return te.getName();
        return null;
    }

    @Override
    public boolean hasCustomName() {
        TileEntityHPBase te = getFilledTileEntity();
        if (te != null)
            return te.hasCustomName();
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        TileEntityHPBase te = getFilledTileEntity();
        if (te != null)
            return te.getDisplayName();
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @javax.annotation.Nullable net.minecraft.util.EnumFacing facing) {
        TileEntityHPBase te = getFilledTileEntity();
        if (te != null)
            return te.getCapability(capability, facing);
        return super.getCapability(capability, facing);
    }
}
