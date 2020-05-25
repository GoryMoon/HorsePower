package se.gory_moon.horsepower.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.INameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import se.gory_moon.horsepower.Registration;
import se.gory_moon.horsepower.blocks.FillerBlock;
import se.gory_moon.horsepower.blocks.HPBaseBlock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FillerTileEntity extends TileEntity implements INameable {

    public FillerTileEntity() {
        super(Registration.FILLER_TILE.get());
    }

    public HPBaseTileEntity getFilledTileEntity() {
        BlockPos pos = getFilledPos();
        TileEntity tileEntity = getWorld().getTileEntity(pos);
        if (tileEntity instanceof HPBaseTileEntity) {
            return (HPBaseTileEntity) tileEntity;
        }
        return null;
    }

    public BlockPos getFilledPos() {
        BlockState state = getWorld().getBlockState(getPos());
        if (!(state.getBlock() instanceof FillerBlock))
            return getPos();
        Direction facing = state.get(DirectionalBlock.FACING);
        BlockState state1 = getWorld().getBlockState(pos.offset(facing));
        if (!(state1.getBlock() instanceof HPBaseBlock))
            return getPos();
        return pos.offset(facing);
    }

    @Override
    public void markDirty() {
        HPBaseTileEntity te = getFilledTileEntity();
        if (te != null)
            te.markDirty();
        super.markDirty();
    }

    @Override
    public ITextComponent getName() {
        HPBaseTileEntity te = getFilledTileEntity();
        if (te != null)
            return te.getName();
        return null;
    }

    @Override
    public boolean hasCustomName() {
        HPBaseTileEntity te = getFilledTileEntity();
        if (te != null)
            return te.hasCustomName();
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        HPBaseTileEntity te = getFilledTileEntity();
        if (te != null)
            return te.getDisplayName();
        return null;
    }

    @Nullable
    @Override
    public ITextComponent getCustomName() {
        HPBaseTileEntity te = getFilledTileEntity();
        if (te != null)
            return te.getCustomName();
        return null;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        HPBaseTileEntity te = getFilledTileEntity();
        if (te != null)
            return te.getCapability(cap, side);
        return super.getCapability(cap, side);
    }
}
