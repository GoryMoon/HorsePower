package se.gory_moon.horsepower.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Particles;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.*;
import se.gory_moon.horsepower.tileentity.TileEntityFiller;

import javax.annotation.Nullable;

//@Optional.Interface(iface = "mcjty.theoneprobe.api.IProbeInfoAccessor", modid = "theoneprobe")
public class BlockFiller extends BlockDirectional {

    private boolean useTileEntity;
    private boolean providePower;

    public BlockFiller(Properties builder, String name, boolean useTileEntity, boolean providePower) {
        super(builder);
        setRegistryName(name + "filler");
        this.useTileEntity = useTileEntity;
        this.providePower = providePower;
    }

    public BlockFiller(Properties builder, String name, boolean useTileEntity) {
        this(builder, name, useTileEntity, false);
    }

    private boolean validateFilled(IWorld world, IBlockState state, BlockPos pos) {
        if (state.getBlock() instanceof BlockHPBase) {
            return true;
        } else {
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
            return false;
        }
    }
/*
    public BlockFiller setHarvestLevel1(String tool, int level) {
        setHarvestLevel(tool, level);
        return this;
    }*/

    @Nullable
    @Override
    public TileEntity createTileEntity(IBlockState state, IBlockReader world) {
        return new TileEntityFiller();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return useTileEntity;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
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
    public void onNeighborChange(IBlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {
        if (world instanceof World && !((World) world).isRemote && pos.offset(state.get(FACING)).equals(neighbor)) {
            validateFilled((World) world, world.getBlockState(neighbor), pos);
        }
    }

    @Override
    public void onPlayerDestroy(IWorld world, BlockPos pos0, IBlockState state) {
        BlockPos pos = pos0.offset(state.get(FACING));
        IBlockState state1 = world.getBlockState(pos);
        if (validateFilled(world, state1, pos0)) {
            state1.getBlock().onPlayerDestroy(world, pos, world.getBlockState(pos));
            world.destroyBlock(pos, true);
        }
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos0, EntityPlayer player, boolean willHarvest, IFluidState fluid) {
        BlockPos pos = pos0.offset(state.get(FACING));
        IBlockState state1 = world.getBlockState(pos);
        if (validateFilled(world, state1, pos0))
            state1.getBlock().removedByPlayer(world.getBlockState(pos), world, pos, player, willHarvest, world.getFluidState(pos));
        return super.removedByPlayer(state, world, pos0, player, willHarvest, fluid);
    }

    @Override
    public VoxelShape getShape(IBlockState state, IBlockReader world, BlockPos pos0) {
        BlockPos pos = pos0.offset(state.get(FACING));
        IBlockState state1 = world.getBlockState(pos);
        if (world instanceof World && validateFilled((World) world, state1, pos0))
            return state1.getBlock().getShape(state1, world, pos);
        else
            return super.getShape(state, world, pos0);
    }

    @Override
    public VoxelShape getRenderShape(IBlockState state, IBlockReader world, BlockPos pos0) {
        BlockPos pos = pos0.offset(state.get(FACING));
        IBlockState state1 = world.getBlockState(pos);
        if (validateFilled((IWorld) world, state1, pos0))
            return state1.getBlock().getRenderShape(state1, world, pos);
        else
            return super.getRenderShape(state, world, pos0);
    }

    @Override
    public VoxelShape getCollisionShape(IBlockState state, IBlockReader world, BlockPos pos0) {
        BlockPos pos = pos0.offset(state.get(FACING));
        IBlockState state1 = world.getBlockState(pos);
        if (validateFilled((IWorld) world, state1, pos0))
            return state1.getBlock().getCollisionShape(state1, world, pos);
        else
            return super.getCollisionShape(state, world, pos0);
    }

    @Override
    public VoxelShape getRaytraceShape(IBlockState state, IBlockReader world, BlockPos pos0) {
        BlockPos pos = pos0.offset(state.get(FACING));
        IBlockState state1 = world.getBlockState(pos);
        if (validateFilled((IWorld) world, state1, pos0))
            return state1.getBlock().getRaytraceShape(state1, world, pos);
        else
            return super.getRaytraceShape(state, world, pos0);
    }

    @Override
    public boolean onBlockActivated(IBlockState state, World world, BlockPos pos0, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        BlockPos pos = pos0.offset(state.get(FACING));
        IBlockState state1 = world.getBlockState(pos);
        if (validateFilled(world, state1, pos0))
            return state1.getBlock().onBlockActivated(state1, world, pos, player, hand, side, hitX ,hitY, hitZ);
        else
            return super.onBlockActivated(state, world, pos0, player, hand, side, hitX, hitY, hitZ);
    }

    @Override
    public boolean shouldCheckWeakPower(IBlockState state, IWorldReader world, BlockPos pos0, EnumFacing side) {
        BlockPos pos = pos0.offset(state.get(FACING));
        IBlockState state1 = world.getBlockState(pos);
        if (world instanceof World && validateFilled((World) world, state1, pos0))
            return state1.getBlock().shouldCheckWeakPower(state1, world, pos, side);
        else
            return super.shouldCheckWeakPower(state, world, pos0, side);
    }

    @Override
    public int getStrongPower(IBlockState state, IBlockReader world, BlockPos pos0, EnumFacing side) {
        BlockPos pos = pos0.offset(state.get(FACING));
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
    public boolean canConnectRedstone(IBlockState state, IBlockReader world, BlockPos pos0, @Nullable EnumFacing side) {
        BlockPos pos = pos0.offset(state.get(FACING));
        IBlockState state1 = world.getBlockState(pos);
        if (world instanceof World && validateFilled((World) world, state1, pos0))
            return state1.getBlock().canConnectRedstone(state1, world, pos, side);
        else
            return super.canConnectRedstone(state, world, pos0, side);
    }

    @Override
    public int getWeakPower(IBlockState state, IBlockReader world, BlockPos pos0, EnumFacing side) {
        BlockPos pos = pos0.offset(state.get(FACING));
        IBlockState state1 = world.getBlockState(pos);
        if (world instanceof World && validateFilled((World) world, state1, pos0))
            return state1.getBlock().getWeakPower(state1, world, pos, side);
        else
            return super.getWeakPower(state, world, pos0, side);
    }

    @Override
    public void onEntityWalk(World world, BlockPos pos0, Entity entityIn) {
        BlockPos pos = pos0.offset(world.getBlockState(pos0).get(FACING));
        IBlockState state1 = world.getBlockState(pos);
        if (validateFilled(world, state1, pos0))
            state1.getBlock().onEntityWalk(world, pos, entityIn);
    }

    @Override
    public ItemStack getItem(IBlockReader world, BlockPos pos0, IBlockState state) {
        BlockPos pos = pos0.offset(state.get(FACING));
        IBlockState state1 = world.getBlockState(pos);
        if (validateFilled((IWorld) world, state1, pos0))
            return state1.getBlock().getItem(world, pos, state1);
        else
            return super.getItem(world, pos0, state);
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, IBlockReader world, BlockPos pos0, EntityPlayer player) {
        BlockPos pos = pos0.offset(state.get(FACING));
        IBlockState state1 = world.getBlockState(pos);
        if (validateFilled((IWorld) world, state1, pos0))
            return state1.getBlock().getPickBlock(state1, target, world, pos, player);
        else
            return super.getPickBlock(state, target, world, pos0, player);
    }

    @Override
    public boolean causesSuffocation(IBlockState state) {
        return true;
    }

    @Override
    public IItemProvider getItemDropped(IBlockState state, World worldIn, BlockPos pos, int fortune) {
        return null;
    }

    @Override
    public SoundType getSoundType(IBlockState state, IWorldReader world, BlockPos pos0, @Nullable Entity entity) {
        BlockPos pos = pos0.offset(state.get(FACING));
        IBlockState state1 = world.getBlockState(pos);
        if (validateFilled((IWorld) world, state1, pos0))
            return state1.getBlock().getSoundType(state1, world, pos, entity);
        else
            return super.getSoundType(state, world, pos, entity);
    }

    @Override
    public boolean addHitEffects(IBlockState state, World world, RayTraceResult target, ParticleManager manager) {
        BlockPos pos = target.getBlockPos().offset(state.get(FACING));
        IBlockState state1 = world.getBlockState(pos);
        if (!validateFilled(world, state1, target.getBlockPos()))
            return true;
        RayTraceResult target1 = new RayTraceResult(target.type, target.hitVec.subtract(0, 1, 0), target.sideHit, pos);
        boolean flag = state1.getBlock().addHitEffects(state1, world, target1, manager);
        if (!flag)
            Minecraft.getInstance().particles.addBlockHitEffects(pos, target.sideHit);
        return true;
    }


    @Override
    public boolean addDestroyEffects(IBlockState state, World world, BlockPos pos0, ParticleManager manager) {
        BlockPos pos = pos0.offset(world.getBlockState(pos0).get(FACING));
        IBlockState state1 = world.getBlockState(pos);
        if (!validateFilled(world, state1, pos0))
            return true;
        boolean flag = state1.getBlock().addDestroyEffects(world.getBlockState(pos), world, pos, manager);
        if (!flag)
            Minecraft.getInstance().particles.addBlockDestroyEffects(pos, state1);
        return true;
    }

    @Override
    public boolean addLandingEffects(IBlockState state, WorldServer world, BlockPos pos0, IBlockState iblockstate, EntityLivingBase entity, int numberOfParticles) {
        BlockPos pos = pos0.offset(state.get(FACING));
        IBlockState state1 = world.getBlockState(pos);
        if (!validateFilled(world, state1, pos0))
            return true;
        boolean flag = state1.getBlock().addLandingEffects(state1, world, pos, iblockstate, entity, numberOfParticles);
        if (!flag)
            world.spawnParticle(new BlockParticleData(Particles.BLOCK, state), pos0.getX(), pos0.getY(), pos0.getZ(), numberOfParticles, 0.0D, 0.0D, 0.0D, (double)0.15F);
        return true;
    }

    @Override
    public void dropBlockAsItemWithChance(IBlockState state, World worldIn, BlockPos pos, float chancePerItem, int fortune) {}

    // The One Probe Integration
    /*@Optional.Method(modid = "theoneprobe")
    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        BlockPos pos = data.getPos().offset(blockState.getValue(FACING));
        IBlockState state = world.getBlockState(pos);
        if (validateFilled(world, state, data.getPos()) && state.getBlock() instanceof IProbeInfoAccessor) {
            ((IProbeInfoAccessor) state.getBlock()).addProbeInfo(mode, probeInfo, player, world, state, new ProbeHitData(pos, data.getHitVec(), data.getSideHit(), data.getPickBlock()));
        }
    }*/
}
