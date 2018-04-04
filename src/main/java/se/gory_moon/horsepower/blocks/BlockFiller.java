package se.gory_moon.horsepower.blocks;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoAccessor;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.apiimpl.ProbeHitData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import se.gory_moon.horsepower.tileentity.TileEntityFiller;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

@Optional.Interface(iface = "mcjty.theoneprobe.api.IProbeInfoAccessor", modid = "theoneprobe")
public class BlockFiller extends BlockDirectional implements IProbeInfoAccessor {

    private boolean useTileEntity;
    private boolean providePower;

    public BlockFiller(Material materialIn, String name, boolean useTileEntity, boolean providePower) {
        super(materialIn);
        setRegistryName(name + "filler");
        this.useTileEntity = useTileEntity;
        this.providePower = providePower;
    }

    public BlockFiller(Material materialIn, String name, boolean useTileEntity) {
        this(materialIn, name, useTileEntity, false);
    }

    private boolean validateFilled(World world, IBlockState state, BlockPos pos) {
        if (state.getBlock() instanceof BlockHPBase) {
            return true;
        } else {
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
            return false;
        }
    }

    public BlockFiller setHarvestLevel1(String tool, int level) {
        setHarvestLevel(tool, level);
        return this;
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

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
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
    public boolean isBlockNormalCube(IBlockState state) {
        return false;
    }

    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
        IBlockState state = world.getBlockState(pos);
        if (!((World) world).isRemote && pos.offset(state.getValue(FACING)).equals(neighbor)) {
            validateFilled((World) world, world.getBlockState(neighbor), pos);
        }
    }

    @Override
    public void onBlockDestroyedByPlayer(World world, BlockPos pos0, IBlockState state) {
        BlockPos pos = pos0.offset(state.getValue(FACING));
        IBlockState state1 = world.getBlockState(pos);
        if (validateFilled(world, state1, pos0)) {
            state1.getBlock().onBlockDestroyedByPlayer(world, pos, world.getBlockState(pos));
            world.destroyBlock(pos, true);
        }
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos0, EntityPlayer player, boolean willHarvest) {
        BlockPos pos = pos0.offset(state.getValue(FACING));
        IBlockState state1 = world.getBlockState(pos);
        if (validateFilled(world, state1, pos0))
            state1.getBlock().removedByPlayer(world.getBlockState(pos), world, pos, player, willHarvest);
        return super.removedByPlayer(state, world, pos0, player, willHarvest);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos0) {
        BlockPos pos = pos0.offset(state.getValue(FACING));
        IBlockState state1 = world.getBlockState(pos);
        if (world instanceof World && validateFilled((World) world, state1, pos0))
            return state1.getBlock().getBoundingBox(state1, world, pos);
        else
            return super.getBoundingBox(state, world, pos0);
    }

    @Override
    public boolean onBlockActivated(World w, BlockPos p0, IBlockState s, EntityPlayer e, EnumHand h, EnumFacing f, float x, float y, float z) {
        BlockPos p = p0.offset(s.getValue(FACING));
        IBlockState state1 = w.getBlockState(p);
        if (validateFilled(w, state1, p0))
            return state1.getBlock().onBlockActivated(w, p, state1, e, h ,f, x, y, z);
        else
            return super.onBlockActivated(w, p, s, e, h, f, x, y, z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public final AxisAlignedBB getSelectedBoundingBox(IBlockState s, World w, BlockPos p0) {
        BlockPos p = p0.offset(s.getValue(FACING));
        IBlockState state1 = w.getBlockState(p);
        if (validateFilled(w, state1, p0))
            return state1.getBlock().getSelectedBoundingBox(state1, w, p);
        else
            return super.getSelectedBoundingBox(s, w, p0);
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos0, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_) {
        BlockPos pos = pos0.offset(state.getValue(FACING));
        IBlockState state1 = world.getBlockState(pos);
        if (validateFilled(world, state1, pos0))
            state1.getBlock().addCollisionBoxToList(state1, world, pos, entityBox, collidingBoxes, entityIn, p_185477_7_);
    }

    @Override
    public final RayTraceResult collisionRayTrace(IBlockState s, World w, BlockPos p0, Vec3d start, Vec3d end) {
        BlockPos p = p0.offset(s.getValue(FACING));
        IBlockState state1 = w.getBlockState(p);
        if (validateFilled(w, state1, p0)) {
            RayTraceResult trace = state1.getBlock().collisionRayTrace(state1, w, p, start, end);
            return trace != null ? new RayTraceResult(trace.typeOfHit, trace.hitVec, trace.sideHit, p0) : trace;
        } else
            return super.collisionRayTrace(s, w, p0, start, end);
    }

    @Override
    public boolean shouldCheckWeakPower(IBlockState state, IBlockAccess world, BlockPos pos0, EnumFacing side) {
        BlockPos pos = pos0.offset(state.getValue(FACING));
        IBlockState state1 = world.getBlockState(pos);
        if (world instanceof World && validateFilled((World) world, state1, pos0))
            return state1.getBlock().shouldCheckWeakPower(state1, world, pos, side);
        else
            return super.shouldCheckWeakPower(state, world, pos0, side);
    }

    @Override
    public int getStrongPower(IBlockState state, IBlockAccess world, BlockPos pos0, EnumFacing side) {
        BlockPos pos = pos0.offset(state.getValue(FACING));
        IBlockState state1 = world.getBlockState(pos);
        if (world instanceof World && validateFilled((World) world, state1, pos0))
            return state1.getBlock().getStrongPower(state1, world, pos, side);
        else
            return super.getStrongPower(state, world, pos0, side);
    }

    @Override
    public boolean canProvidePower(IBlockState state) {
        return providePower;
    }

    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos0, @Nullable EnumFacing side) {
        BlockPos pos = pos0.offset(state.getValue(FACING));
        IBlockState state1 = world.getBlockState(pos);
        if (world instanceof World && validateFilled((World) world, state1, pos0))
            return state1.getBlock().canConnectRedstone(state1, world, pos, side);
        else
            return super.canConnectRedstone(state, world, pos0, side);
    }

    @Override
    public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos0, EnumFacing side) {
        BlockPos pos = pos0.offset(state.getValue(FACING));
        IBlockState state1 = world.getBlockState(pos);
        if (world instanceof World && validateFilled((World) world, state1, pos0))
            return state1.getBlock().getWeakPower(state1, world, pos, side);
        else
            return super.getWeakPower(state, world, pos0, side);
    }

    @Override
    public void onEntityWalk(World world, BlockPos pos0, Entity entityIn) {
        BlockPos pos = pos0.offset(world.getBlockState(pos0).getValue(FACING));
        IBlockState state1 = world.getBlockState(pos);
        if (validateFilled(world, state1, pos0))
            state1.getBlock().onEntityWalk(world, pos, entityIn);
    }

    @Override
    public ItemStack getItem(World world, BlockPos pos0, IBlockState state) {
        BlockPos pos = pos0.offset(state.getValue(FACING));
        IBlockState state1 = world.getBlockState(pos);
        if (validateFilled(world, state1, pos0))
            return state1.getBlock().getItem(world, pos, state1);
        else
            return super.getItem(world, pos0, state);
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos0, EntityPlayer player) {
        BlockPos pos = pos0.offset(state.getValue(FACING));
        IBlockState state1 = world.getBlockState(pos);
        if (validateFilled(world, state1, pos0))
            return state1.getBlock().getPickBlock(state1, target, world, pos, player);
        else
            return super.getPickBlock(state, target, world, pos0, player);
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
    public SoundType getSoundType(IBlockState state, World world, BlockPos pos0, @Nullable Entity entity) {
        BlockPos pos = pos0.offset(state.getValue(FACING));
        IBlockState state1 = world.getBlockState(pos);
        if (validateFilled(world, state1, pos0))
            return state1.getBlock().getSoundType(state1, world, pos, entity);
        else
            return super.getSoundType(state, world, pos, entity);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addHitEffects(IBlockState state, World world, RayTraceResult target, ParticleManager manager) {
        BlockPos pos = target.getBlockPos().offset(state.getValue(FACING));
        IBlockState state1 = world.getBlockState(pos);
        if (!validateFilled(world, state1, target.getBlockPos()))
            return true;
        RayTraceResult target1 = new RayTraceResult(target.typeOfHit, target.hitVec.subtract(0, 1, 0), target.sideHit, pos);
        boolean flag = state1.getBlock().addHitEffects(state1, world, target1, manager);
        if (!flag)
            Minecraft.getMinecraft().effectRenderer.addBlockHitEffects(pos, target.sideHit);
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(World world, BlockPos pos0, ParticleManager manager) {
        BlockPos pos = pos0.offset(world.getBlockState(pos0).getValue(FACING));
        IBlockState state1 = world.getBlockState(pos);
        if (!validateFilled(world, state1, pos0))
            return true;
        boolean flag = state1.getBlock().addDestroyEffects(world, pos, manager);
        if (!flag)
            Minecraft.getMinecraft().effectRenderer.addBlockDestroyEffects(pos, state1);
        return true;
    }

    @Override
    public boolean addLandingEffects(IBlockState state, WorldServer world, BlockPos pos0, IBlockState iblockstate, EntityLivingBase entity, int numberOfParticles) {
        BlockPos pos = pos0.offset(state.getValue(FACING));
        IBlockState state1 = world.getBlockState(pos);
        if (!validateFilled(world, state1, pos0))
            return true;
        boolean flag = state1.getBlock().addLandingEffects(state1, world, pos, iblockstate, entity, numberOfParticles);
        if (!flag)
            world.spawnParticle(EnumParticleTypes.BLOCK_DUST, pos0.getX() + 0.5, pos0.getY() + 1, pos0.getZ() + 0.5, numberOfParticles, 0.0D, 0.0D, 0.0D, 0.15000000596046448D, Block.getStateId(state1));
        return true;
    }

    @Override
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {}


    // The One Probe Integration
    @Optional.Method(modid = "theoneprobe")
    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        BlockPos pos = data.getPos().offset(blockState.getValue(FACING));
        IBlockState state = world.getBlockState(pos);
        if (validateFilled(world, state, data.getPos()) && state.getBlock() instanceof IProbeInfoAccessor) {
            ((IProbeInfoAccessor) state.getBlock()).addProbeInfo(mode, probeInfo, player, world, state, new ProbeHitData(pos, data.getHitVec(), data.getSideHit(), data.getPickBlock()));
        }
    }
}
