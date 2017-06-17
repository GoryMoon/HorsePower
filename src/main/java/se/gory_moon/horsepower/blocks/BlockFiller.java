package se.gory_moon.horsepower.blocks;

import net.minecraft.block.BlockDirectional;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import se.gory_moon.horsepower.tileentity.TileEntityFiller;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BlockFiller extends BlockDirectional {

    public static final IUnlistedProperty<Integer> MISC_DATA = Properties.toUnlisted(PropertyInteger.create("misc_data", 0, 32));
    public boolean useTileEntity;

    public BlockFiller(Material materialIn, String name, boolean useTileEntity) {
        super(materialIn);
        setRegistryName(name + "filler");
        this.useTileEntity = useTileEntity;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityFiller();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return useTileEntity;
    }

    private IBlockState getFillerState(IBlockAccess world, BlockPos pos) {
        return world.getBlockState(pos);
    }

    private int getValue(IExtendedBlockState state, int location, int size) {
        return (state.getValue(MISC_DATA) >> location) & ((1 << size) - 1);
    }

    private int setValue(int meta, int location, int value) {
        return meta | (value << location);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new ExtendedBlockState(this, new IProperty[]{ FACING }, new IUnlistedProperty[]{ MISC_DATA });
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, EnumFacing.getFront(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }

    @Override
    public IBlockState getActualState(IBlockState baseState, IBlockAccess worldIn, BlockPos pos) {
        if (baseState instanceof IExtendedBlockState) {
            IExtendedBlockState extState = (IExtendedBlockState) baseState;
            pos = pos.offset(extState.getValue(FACING));
            IBlockState state1 = worldIn.getBlockState(pos);
            int meta = setValue(state1.getBlock().canProvidePower(state1)? 1: 0, 0, 1);


            return ((IExtendedBlockState) baseState).withProperty(MISC_DATA, meta);
        }
        return super.getActualState(baseState, worldIn, pos);
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullyOpaque(IBlockState state) {
        return false;
    }

    @Override
    public boolean isBlockNormalCube(IBlockState state) {
        return false;
    }

    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
        IBlockState state = world.getBlockState(pos);
        if (!((World) world).isRemote && pos.offset(state.getValue(FACING)).equals(neighbor) && world.isAirBlock(neighbor)) {
            ((World) world).setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
        }
    }

    @Override
    public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state) {
        pos = pos.offset(state.getValue(FACING));
        worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        pos = pos.offset(state.getValue(FACING));
        IBlockState state1 = world.getBlockState(pos);
        return state1.getBlock().getBoundingBox(state1, world, pos);
    }

    @Override
    public boolean onBlockActivated(World w, BlockPos p, IBlockState s, EntityPlayer e, EnumHand h, EnumFacing f, float x, float y, float z) {
        p = p.offset(s.getValue(FACING));
        IBlockState state1 = w.getBlockState(p);
        return state1.getBlock().onBlockActivated(w, p, state1, e, h ,f, x, y, z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public final AxisAlignedBB getSelectedBoundingBox(IBlockState s, World w, BlockPos p) {
        p = p.offset(s.getValue(FACING));
        IBlockState state1 = w.getBlockState(p);
        return state1.getBlock().getSelectedBoundingBox(state1, w, p);
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_) {
        pos = pos.offset(state.getValue(FACING));
        IBlockState state1 = worldIn.getBlockState(pos);
        state1.getBlock().addCollisionBoxToList(state1, worldIn, pos, entityBox, collidingBoxes, entityIn, p_185477_7_);
    }

    @Override
    public final RayTraceResult collisionRayTrace(IBlockState s, World w, BlockPos p, Vec3d start, Vec3d end) {
        BlockPos p2 = p.offset(s.getValue(FACING));
        IBlockState state1 = w.getBlockState(p2);
        RayTraceResult trace = state1.getBlock().collisionRayTrace(state1, w, p2, start, end);
        return trace != null ? new RayTraceResult(trace.typeOfHit, trace.hitVec, trace.sideHit, p): trace;
    }

    @Override
    public boolean shouldCheckWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        pos = pos.offset(state.getValue(FACING));
        IBlockState state1 = world.getBlockState(pos);
        return state1.getBlock().shouldCheckWeakPower(state1, world, pos, side);
    }

    @Override
    public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        pos = pos.offset(blockState.getValue(FACING));
        IBlockState state1 = blockAccess.getBlockState(pos);
        return state1.getBlock().getStrongPower(state1, blockAccess, pos, side);
    }

    @Override
    public boolean canProvidePower(IBlockState state) {
        if (state instanceof IExtendedBlockState) {
            return getValue((IExtendedBlockState) state, 0, 1) == 1;
        }
        return super.canProvidePower(state);
    }

    @Override
    public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        pos = pos.offset(blockState.getValue(FACING));
        IBlockState state1 = blockAccess.getBlockState(pos);
        return state1.getBlock().getWeakPower(state1, blockAccess, pos, side);
    }

    @Override
    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
        pos = pos.offset(worldIn.getBlockState(pos).getValue(FACING));
        IBlockState state1 = worldIn.getBlockState(pos);
        state1.getBlock().onEntityWalk(worldIn, pos, entityIn);
    }

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        pos = pos.offset(state.getValue(FACING));
        IBlockState state1 = worldIn.getBlockState(pos);
        return state1.getBlock().getItem(worldIn, pos, state1);
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        pos = pos.offset(state.getValue(FACING));
        IBlockState state1 = world.getBlockState(pos);
        return state1.getBlock().getPickBlock(state, target, world, pos, player);
    }

    @Override
    public boolean causesSuffocation(IBlockState state) {
        return true;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return null;
    }

    @Override
    public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
        pos = pos.offset(state.getValue(FACING));
        IBlockState state1 = world.getBlockState(pos);
        return state1.getBlock().getSoundType(state1, world, pos, entity);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addHitEffects(IBlockState state, World worldObj, RayTraceResult target, ParticleManager manager) {
        BlockPos pos = target.getBlockPos().offset(state.getValue(FACING));
        IBlockState state1 = worldObj.getBlockState(pos);
        return state1.getBlock().addHitEffects(state1, worldObj, target, manager);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
        pos = pos.offset(world.getBlockState(pos).getValue(FACING));
        IBlockState state1 = world.getBlockState(pos);
        return state1.getBlock().addDestroyEffects(world, pos, manager);
    }

    @Override
    public boolean addLandingEffects(IBlockState state, WorldServer worldObj, BlockPos blockPosition, IBlockState iblockstate, EntityLivingBase entity, int numberOfParticles) {
        BlockPos pos = blockPosition.offset(state.getValue(FACING));
        IBlockState state1 = worldObj.getBlockState(pos);
        return state1.getBlock().addLandingEffects(state1, worldObj, pos, iblockstate, entity, numberOfParticles);
    }

    @Override
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {}
}
