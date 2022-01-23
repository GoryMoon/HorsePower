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
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import se.gory_moon.horsepower.Registration;
import se.gory_moon.horsepower.advancements.AdvancementManager;
import se.gory_moon.horsepower.client.model.modelvariants.ChopperModels;
import se.gory_moon.horsepower.client.utils.color.Colors;
import se.gory_moon.horsepower.tileentity.ChopperTileEntity;
import se.gory_moon.horsepower.util.Localization;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ChopperBlock extends HPChopperBlock {

    public static final EnumProperty<ChopperModels> PART = EnumProperty.create("part", ChopperModels.class);

    private static final VoxelShape PART_BASE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D);
    private static final VoxelShape PART_POST_TOP = Block.makeCuboidShape(7.0D, 28.0D, 7.0D, 9.0D, 32.0D, 9.0D);

    private static final VoxelShape PART_POST_X = Block.makeCuboidShape(6.0D, 6.0D, 0.0D, 10.0D, 28.0D, 2.0D);
    private static final VoxelShape PART_POST_X2 = Block.makeCuboidShape(6.0D, 6.0D, 14.0D, 10.0D, 28.0D, 16.0D);

    private static final VoxelShape PART_POST_Z = Block.makeCuboidShape(0.0D, 6.0D, 6.0D, 2.0D, 28.0D, 10.0D);
    private static final VoxelShape PART_POST_Z2 = Block.makeCuboidShape(14.0D, 6.0D, 6.0D, 16.0D, 28.0D, 10.0D);
    
    private static final VoxelShape PART_TOP_BASE_X = Block.makeCuboidShape(6.0D, 26.0D, 0.0D, 10.0D, 28.0D, 16.0D);
    private static final VoxelShape PART_TOP_BASE_Z = Block.makeCuboidShape(0.0D, 26.0D, 6.0D, 16.0D, 28.0D, 10.0D);
    
    private static final VoxelShape SHAPE_X = VoxelShapes.or(PART_BASE, PART_POST_TOP, PART_POST_X, PART_POST_X2, PART_TOP_BASE_X);
    private static final VoxelShape SHAPE_Z = VoxelShapes.or(PART_BASE, PART_POST_TOP, PART_POST_Z, PART_POST_Z2, PART_TOP_BASE_Z);
    
    public ChopperBlock(Properties properties) {
        super(properties.hardnessAndResistance(5F, 5F).sound(SoundType.WOOD));
        setHarvestLevel(ToolType.AXE, 0);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return state.get(FACING).getAxis() == Direction.Axis.X ? SHAPE_X : SHAPE_Z;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos,
            ISelectionContext context) {
        return state.get(FACING).getAxis() == Direction.Axis.X ? SHAPE_X : SHAPE_Z;
    }

    @Override
    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!worldIn.isRemote) {
            Direction facing = state.get(FACING);
            worldIn.setBlockState(pos, state.with(FACING, facing).with(PART, ChopperModels.BASE), 2);
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return getDefaultState().with(FACING, context.getPlacementHorizontalFacing()).with(PART, ChopperModels.BASE);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        worldIn.setBlockState(pos, state.with(FACING, placer.getHorizontalFacing().getOpposite()), 2);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART);
    }

    @Override
    public void addInformation(ItemStack stack, IBlockReader worldIn, List<ITextComponent> tooltip,
            ITooltipFlag flagIn) {
        tooltip.add(new StringTextComponent(
                Localization.ITEM.HORSE_CHOPPING.SIZE.translate(Colors.WHITE.toString(), Colors.LIGHTGRAY.toString())));
        tooltip.add(new StringTextComponent(Localization.ITEM.HORSE_CHOPPING.LOCATION.translate()));
        tooltip.add(new StringTextComponent(Localization.ITEM.HORSE_CHOPPING.USE.translate()));
    }

    @Override
    public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {
        if (!((World) world).isRemote && pos.up().equals(neighbor)
                && !(world.getBlockState(neighbor).getBlock() instanceof FillerBlock)) {
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
            AdvancementManager.USE_CHOPPER.trigger((ServerPlayerEntity) playerIn);
    }

    @Nonnull
    @Override
    public Class<?> getTileClass() {
        return ChopperTileEntity.class;
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader iBlockReader) {
        return new ChopperTileEntity(Registration.CHOPPER_TILE.get());
    }
    
 // TODO  isEmissiveRendering or isViewBlocking ?
//  @Override
//  public boolean hasCustomBreakingProgress(BlockState state) {
//      return true;
//  }

//  @Override
//  public EnumBlockRenderType getRenderType(BlockState state) {
//      return EnumBlockRenderType.MODEL;
//  }
//    @Override
//    protected BlockStateContainer createBlockState() {
//        return new ExtendedBlockState(this, new IProperty[] {PART, FACING}, new IUnlistedProperty[]{SIDE_TEXTURE, TOP_TEXTURE});
//    }
//    @Override
//    public int getMetaFromState(BlockState state) {
//        return state.getValue(FACING).getIndex();
//    }
//
//    @Override
//    public BlockState getStateFromMeta(int meta) {
//        EnumFacing enumfacing = EnumFacing.getFront(meta);
//
//        if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
//            enumfacing = EnumFacing.NORTH;
//        }
//
//        return getDefaultState().withProperty(FACING, enumfacing).withProperty(PART, ChopperModels.BASE);
//    }
}
