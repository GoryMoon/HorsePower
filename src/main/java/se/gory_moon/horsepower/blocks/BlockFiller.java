package se.gory_moon.horsepower.blocks;

import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.*;
import net.minecraftforge.common.ToolType;
import se.gory_moon.horsepower.tileentity.TileEntityFiller;

import javax.annotation.Nullable;

//@Optional.Interface(iface = "mcjty.theoneprobe.api.IProbeInfoAccessor", modid = "theoneprobe")
public class BlockFiller extends DirectionalBlock {

    private boolean useTileEntity;
    private boolean providePower;
    private ToolType type;
    private int level;

    public BlockFiller(Properties builder, boolean useTileEntity, boolean providePower) {
        super(builder);
        this.useTileEntity = useTileEntity;
        this.providePower = providePower;
    }

    public BlockFiller(Properties builder, boolean useTileEntity) {
        this(builder, useTileEntity, false);
    }

    private boolean validateFilled(IBlockReader world, BlockState state, BlockPos pos) {
        if (state.getBlock() instanceof BlockHPBase) {
            return true;
        } else {
            if (world instanceof IWorldWriter) {
                ((IWorldWriter)world).setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
            }
            return false;
        }
    }

    public BlockFiller setHarvestLevel(ToolType type, int level) {
        this.type = type;
        this.level = level;
        return this;
    }

    @Override
    public int getHarvestLevel(BlockState state) {
        return this.level;
    }

    @Nullable
    @Override
    public ToolType getHarvestTool(BlockState state) {
        return this.type;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileEntityFiller();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return useTileEntity;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {
        if (world instanceof World && !((World) world).isRemote && pos.offset(state.get(FACING)).equals(neighbor)) {
            validateFilled(world, world.getBlockState(neighbor), pos);
        }
    }

    @Override
    public void onPlayerDestroy(IWorld world, BlockPos pos0, BlockState state) {
        BlockPos pos = pos0.offset(state.get(FACING));
        BlockState state1 = world.getBlockState(pos);
        if (validateFilled(world, state1, pos0)) {
            state1.getBlock().onPlayerDestroy(world, pos, world.getBlockState(pos));
            world.destroyBlock(pos, true);
        }
    }

    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos0, PlayerEntity player, boolean willHarvest, IFluidState fluid) {
        BlockPos pos = pos0.offset(state.get(FACING));
        BlockState state1 = world.getBlockState(pos);
        if (validateFilled(world, state1, pos0))
            state1.getBlock().removedByPlayer(world.getBlockState(pos), world, pos, player, willHarvest, world.getFluidState(pos));
        return super.removedByPlayer(state, world, pos0, player, willHarvest, fluid);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos0, ISelectionContext context) {
        Direction direction = state.get(FACING);
        BlockPos pos = pos0.offset(direction);
        BlockState state1 = world.getBlockState(pos);
        if (validateFilled(world, state1, pos0))
            return state1.getBlock().getShape(state1, world, pos, context).withOffset(direction.getXOffset(), direction.getYOffset(), direction.getZOffset());
        else
            return super.getShape(state, world, pos0, context);
    }

    @Override
    public VoxelShape getRenderShape(BlockState state, IBlockReader world, BlockPos pos0) {
        BlockPos pos = pos0.offset(state.get(FACING));
        BlockState state1 = world.getBlockState(pos);
        if (validateFilled(world, state1, pos0))
            return state1.getBlock().getRenderShape(state1, world, pos);
        else
            return super.getRenderShape(state, world, pos0);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos0, ISelectionContext context) {
        BlockPos pos = pos0.offset(state.get(FACING));
        BlockState state1 = world.getBlockState(pos);
        if (validateFilled(world, state1, pos0))
            return state1.getBlock().getCollisionShape(state1, world, pos, context);
        else
            return super.getCollisionShape(state, world, pos0, context);
    }

    @Override
    public VoxelShape getRaytraceShape(BlockState state, IBlockReader world, BlockPos pos0) {
        BlockPos pos = pos0.offset(state.get(FACING));
        BlockState state1 = world.getBlockState(pos);
        if (validateFilled(world, state1, pos0))
            return state1.getBlock().getRaytraceShape(state1, world, pos);
        else
            return super.getRaytraceShape(state, world, pos0);
    }

    @Override
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos0, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        BlockPos pos = pos0.offset(state.get(FACING));
        BlockState state1 = world.getBlockState(pos);
        if (validateFilled(world, state1, pos0))
            return state1.getBlock().onBlockActivated(state1, world, pos, player, hand, hit);
        else
            return super.onBlockActivated(state, world, pos0, player, hand, hit);
    }

    @Override
    public boolean shouldCheckWeakPower(BlockState state, IWorldReader world, BlockPos pos0, Direction side) {
        BlockPos pos = pos0.offset(state.get(FACING));
        BlockState state1 = world.getBlockState(pos);
        if (world instanceof World && validateFilled(world, state1, pos0))
            return state1.getBlock().shouldCheckWeakPower(state1, world, pos, side);
        else
            return super.shouldCheckWeakPower(state, world, pos0, side);
    }

    @Override
    public int getStrongPower(BlockState state, IBlockReader world, BlockPos pos0, Direction side) {
        BlockPos pos = pos0.offset(state.get(FACING));
        BlockState state1 = world.getBlockState(pos);
        if (world instanceof World && validateFilled(world, state1, pos0))
            return state1.getBlock().getStrongPower(state1, world, pos, side);
        else
            return super.getStrongPower(state, world, pos0, side);
    }

    @Override
    public boolean canProvidePower(BlockState state) {
        return providePower;
    }

    @Override
    public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos0, @Nullable Direction side) {
        BlockPos pos = pos0.offset(state.get(FACING));
        BlockState state1 = world.getBlockState(pos);
        if (world instanceof World && validateFilled(world, state1, pos0))
            return state1.getBlock().canConnectRedstone(state1, world, pos, side);
        else
            return super.canConnectRedstone(state, world, pos0, side);
    }

    @Override
    public int getWeakPower(BlockState state, IBlockReader world, BlockPos pos0, Direction side) {
        BlockPos pos = pos0.offset(state.get(FACING));
        BlockState state1 = world.getBlockState(pos);
        if (world instanceof World && validateFilled(world, state1, pos0))
            return state1.getBlock().getWeakPower(state1, world, pos, side);
        else
            return super.getWeakPower(state, world, pos0, side);
    }

    @Override
    public void onEntityWalk(World world, BlockPos pos0, Entity entityIn) {
        BlockPos pos = pos0.offset(world.getBlockState(pos0).get(FACING));
        BlockState state1 = world.getBlockState(pos);
        if (validateFilled(world, state1, pos0))
            state1.getBlock().onEntityWalk(world, pos, entityIn);
    }

    @Override
    public ItemStack getItem(IBlockReader world, BlockPos pos0, BlockState state) {
        BlockPos pos = pos0.offset(state.get(FACING));
        BlockState state1 = world.getBlockState(pos);
        if (validateFilled(world, state1, pos0))
            return state1.getBlock().getItem(world, pos, state1);
        else
            return super.getItem(world, pos0, state);
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos0, PlayerEntity player) {
        BlockPos pos = pos0.offset(state.get(FACING));
        BlockState state1 = world.getBlockState(pos);
        if (validateFilled(world, state1, pos0))
            return state1.getBlock().getPickBlock(state1, target, world, pos, player);
        else
            return super.getPickBlock(state, target, world, pos0, player);
    }

    @Override
    public boolean causesSuffocation(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return true;
    }

    @Override
    public SoundType getSoundType(BlockState state, IWorldReader world, BlockPos pos0, @Nullable Entity entity) {
        BlockPos pos = pos0.offset(state.get(FACING));
        BlockState state1 = world.getBlockState(pos);
        if (validateFilled(world, state1, pos0))
            return state1.getBlock().getSoundType(state1, world, pos, entity);
        else
            return super.getSoundType(state, world, pos, entity);
    }

    @Override
    public boolean addHitEffects(BlockState state, World world, RayTraceResult target, ParticleManager manager) {
        BlockRayTraceResult result = (BlockRayTraceResult) target;
        BlockPos pos = result.getPos().offset(state.get(FACING));
        BlockState state1 = world.getBlockState(pos);
        if (!validateFilled(world, state1, result.getPos()))
            return true;
        RayTraceResult target1 = new BlockRayTraceResult(result.getHitVec().subtract(0, 1, 0), result.getFace(), pos, result.isInside());
        boolean flag = state1.getBlock().addHitEffects(state1, world, target1, manager);
        if (!flag)
            Minecraft.getInstance().particles.addBlockHitEffects(pos, result.getFace());
        return true;
    }


    @Override
    public boolean addDestroyEffects(BlockState state, World world, BlockPos pos0, ParticleManager manager) {
        BlockPos pos = pos0.offset(world.getBlockState(pos0).get(FACING));
        BlockState state1 = world.getBlockState(pos);
        if (!validateFilled(world, state1, pos0))
            return true;
        boolean flag = state1.getBlock().addDestroyEffects(world.getBlockState(pos), world, pos, manager);
        if (!flag)
            Minecraft.getInstance().particles.addBlockDestroyEffects(pos, state1);
        return true;
    }

    @Override
    public boolean addLandingEffects(BlockState state, ServerWorld world, BlockPos pos0, BlockState iblockstate, LivingEntity entity, int numberOfParticles) {
        BlockPos pos = pos0.offset(state.get(FACING));
        BlockState state1 = world.getBlockState(pos);
        if (!validateFilled(world, state1, pos0))
            return true;
        boolean flag = state1.getBlock().addLandingEffects(state1, world, pos, iblockstate, entity, numberOfParticles);
        if (!flag)
            world.spawnParticle(new BlockParticleData(ParticleTypes.BLOCK, state), pos0.getX(), pos0.getY(), pos0.getZ(), numberOfParticles, 0.0D, 0.0D, 0.0D, (double)0.15F);
        return true;
    }

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
