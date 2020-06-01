package se.gory_moon.horsepower.blocks;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoAccessor;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.apiimpl.styles.ProgressStyle;
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
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockStateContainer;
import net.minecraftforge.common.ToolType;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.advancements.AdvancementManager;
import se.gory_moon.horsepower.client.model.modelvariants.ChopperModels;
import se.gory_moon.horsepower.client.model.modelvariants.PressModels;
import se.gory_moon.horsepower.tileentity.HPBaseTileEntity;
import se.gory_moon.horsepower.tileentity.ChopperTileEntity;
import se.gory_moon.horsepower.util.Constants;
import se.gory_moon.horsepower.util.Localization;
import se.gory_moon.horsepower.util.color.Colors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

//@Optional.Interface(iface = "mcjty.theoneprobe.api.IProbeInfoAccessor", modid = "theoneprobe")
public class ChopperBlock extends HPChopperBaseBlock{// implements IProbeInfoAccessor {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<ChopperModels> PART = EnumProperty.create("part", ChopperModels.class);

    private static final VoxelShape SHAPE = Block.makeCuboidShape(0, 0, 0, 16, 31, 16);
    
    public ChopperBlock(Properties properties) {
        super(properties.hardnessAndResistance(5F,5F).sound(SoundType.WOOD));
        setHarvestLevel(ToolType.AXE, 0);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }
    
    @Override
        public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos,
                ISelectionContext context) {
            return SHAPE;
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
        tooltip.add(new StringTextComponent(Localization.ITEM.HORSE_CHOPPING.SIZE.translate(Colors.WHITE.toString(), Colors.LIGHTGRAY.toString())));
        tooltip.add(new StringTextComponent(Localization.ITEM.HORSE_CHOPPING.LOCATION.translate()));
        tooltip.add(new StringTextComponent(Localization.ITEM.HORSE_CHOPPING.USE.translate()));
    }
    
    @Override
    public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {
        if (!((World) world).isRemote && pos.up().equals(neighbor) && !(world.getBlockState(neighbor).getBlock() instanceof FillerBlock)) {
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
    public boolean hasCustomBreakingProgress(BlockState state) {
        return true;
    }


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
    // The One Probe Integration
//    @Optional.Method(modid = "theoneprobe")
//    @Override
//    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
//        TileEntityChopper tileEntity = getTileEntity(world, data.getPos());
//        if (tileEntity != null) {
//            double totalWindup = Configs.general.pointsForWindup > 0 ? Configs.general.pointsForWindup: 1;
//            probeInfo.progress((long) ((((double) tileEntity.getField(2)) / totalWindup) * 100L), 100L, new ProgressStyle().prefix(Localization.TOP.WINDUP_PROGRESS.translate() + " ").suffix("%"));
//            if (tileEntity.getField(0) > 1)
//                probeInfo.progress((long) ((((double) tileEntity.getField(1)) / ((double) tileEntity.getField(0))) * 100L), 100L, new ProgressStyle().prefix(Localization.TOP.CHOPPING_PROGRESS.translate() + " ").suffix("%"));
//        }
//    }
}
