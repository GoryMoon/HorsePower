package se.gory_moon.horsepower.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import se.gory_moon.horsepower.Registration;
import se.gory_moon.horsepower.advancements.AdvancementManager;
import se.gory_moon.horsepower.client.model.modelvariants.PressModels;
import se.gory_moon.horsepower.client.utils.color.Colors;
import se.gory_moon.horsepower.tileentity.PressTileEntity;
import se.gory_moon.horsepower.util.Localization;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class PressBlock extends HorizontalHPBlock {

    public static final EnumProperty<PressModels> PART = EnumProperty.create("part", PressModels.class);

    private static final VoxelShape BASE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D);

    private static final VoxelShape TOP_X = Block.makeCuboidShape(7.0D, 14.0D, 0.0D, 9.0D, 19.0D, 16.0D);
    private static final VoxelShape TOP_Z = Block.makeCuboidShape(0.0D, 14.0D, 7.0D, 16.0D, 19.0D, 9.0D);
    private static final VoxelShape TOP_POLE = Block.makeCuboidShape(7.0D, 19.0D, 7.0D, 9.0D, 28.0D, 9.0D);


    private static final VoxelShape BOUND_X = VoxelShapes.or(BASE, TOP_X, TOP_POLE);
    private static final VoxelShape BOUND_Z = VoxelShapes.or(BASE, TOP_Z, TOP_POLE);

    public PressBlock(Properties properties) {
        super(properties.hardnessAndResistance(5.0F, 5.0F).sound(SoundType.WOOD));
        setHarvestLevel(ToolType.AXE, 1);
    }

    @Override
    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!worldIn.isRemote) {
            Direction filled = state.get(HORIZONTAL_FACING);
            worldIn.setBlockState(pos, state.with(HORIZONTAL_FACING, filled).with(PART, PressModels.BASE), 2);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return state.get(HORIZONTAL_FACING).getAxis() == Direction.Axis.X ? BOUND_X: BOUND_Z;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return state.get(HORIZONTAL_FACING).getAxis() == Direction.Axis.X ? BOUND_X: BOUND_Z;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return getDefaultState().with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing()).with(PART, PressModels.BASE);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        worldIn.setBlockState(pos, state.with(HORIZONTAL_FACING, placer.getHorizontalFacing().getOpposite()), 2);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING, PART);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new StringTextComponent(Localization.ITEM.HORSE_PRESS.SIZE.translate(Colors.WHITE.toString(), Colors.LIGHTGRAY.toString())));
        tooltip.add(new StringTextComponent(Localization.ITEM.HORSE_PRESS.LOCATION.translate()));
        tooltip.add(new StringTextComponent(Localization.ITEM.HORSE_PRESS.USE.translate()));
    }

    @Override
    public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {
        if (!(world).isRemote() && pos.up().equals(neighbor) && !(world.getBlockState(neighbor).getBlock() instanceof FillerBlock)) {
            ((World) world).setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
        }
    }

    @Override
    public void emptiedOutput(World world, BlockPos pos) {
        //
    }

    @Override
    public void onWorkerAttached(PlayerEntity playerIn, CreatureEntity creature) {
        if (playerIn instanceof ServerPlayerEntity)
            AdvancementManager.USE_PRESS.trigger((ServerPlayerEntity) playerIn);
    }

    @Nonnull
    @Override
    public Class<?> getTileClass() {
        return PressTileEntity.class;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader iBlockReader) {
        return new PressTileEntity(Registration.PRESS_TILE.get());
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (world.isRemote)
            return ActionResultType.SUCCESS;

        TileEntity tileentity = world.getTileEntity(pos);
        if (tileentity != null) {
            final IFluidHandler fluidHandler = tileentity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null).orElse(null);
            if (fluidHandler != null && FluidUtil.interactWithFluidHandler(player, hand, fluidHandler)) {
                tileentity.markDirty();
                return ActionResultType.SUCCESS;
            }
        }
        return super.onBlockActivated(state, world, pos, player, hand, hit);
    }
}
