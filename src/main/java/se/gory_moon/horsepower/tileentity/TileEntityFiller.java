package se.gory_moon.horsepower.tileentity;

import net.minecraft.block.BlockDirectional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.INameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.OptionalCapabilityInstance;
import se.gory_moon.horsepower.blocks.BlockFiller;
import se.gory_moon.horsepower.blocks.BlockHPBase;
import se.gory_moon.horsepower.blocks.ModBlocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityFiller extends TileEntity implements INameable {

    public TileEntityFiller() {
        super(ModBlocks.FILLER_TILE);
    }

    public TileEntityHPBase getFilledTileEntity() {
        BlockPos pos = getFilledPos();
        TileEntity tileEntity = getWorld().getTileEntity(pos);
        if (tileEntity instanceof TileEntityHPBase) {
            return (TileEntityHPBase) tileEntity;
        }
        return null;
    }

    public BlockPos getFilledPos() {
        IBlockState state = getWorld().getBlockState(getPos());
        if (!(state.getBlock() instanceof BlockFiller)) return getPos();
        EnumFacing facing = state.get(BlockDirectional.FACING);
        IBlockState state1 = getWorld().getBlockState(pos.offset(facing));
        if (!(state1.getBlock() instanceof BlockHPBase)) return getPos();
        return pos.offset(facing);
    }

    @Override
    public void markDirty() {
        TileEntityHPBase te = getFilledTileEntity();
        if (te != null)
            te.markDirty();
        super.markDirty();
    }

    @Override
    public ITextComponent getName() {
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

    @Nullable
    @Override
    public ITextComponent getCustomName() {
        TileEntityHPBase te = getFilledTileEntity();
        if (te != null)
            return te.getCustomName();
        return null;
    }

    @Nonnull
    @Override
    public <T> OptionalCapabilityInstance<T> getCapability(@Nonnull Capability<T> cap, @Nullable EnumFacing side) {
        TileEntityHPBase te = getFilledTileEntity();
        if (te != null)
            return te.getCapability(cap, side);
        return super.getCapability(cap, side);
    }
}
