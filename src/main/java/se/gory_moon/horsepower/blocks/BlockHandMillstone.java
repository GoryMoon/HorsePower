package se.gory_moon.horsepower.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.util.FakePlayer;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.client.model.modelvariants.HandMillstoneModels;
import se.gory_moon.horsepower.tileentity.HandMillstoneTileEntity;
import se.gory_moon.horsepower.util.Localization;
import se.gory_moon.horsepower.util.color.Colors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

//@Optional.Interface(iface = "mcjty.theoneprobe.api.IProbeInfoAccessor", modid = "theoneprobe")
public class BlockHandMillstone extends BlockHPBase {
    public static final EnumProperty<HandMillstoneModels> PART = EnumProperty.create("part", HandMillstoneModels.class);

    private static final VoxelShape COLLISION = Block.makeCuboidShape(1, 0, 1, 15, 10, 15);

    public BlockHandMillstone() {
        super(Properties.create(Material.ROCK).hardnessAndResistance(1.5F, 10F).sound(SoundType.STONE));

        setHarvestLevel(ToolType.PICKAXE, 1);
        setDefaultState(getStateContainer().getBaseState().with(FACING, Direction.NORTH).with(PART, HandMillstoneModels.BASE));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return COLLISION;
    }

    @Override
    public int getSlot(BlockState state, BlockRayTraceResult hit) {
        Direction f = state.get(FACING).getOpposite();
        //TODO separate shapes per slot
/*        if (hitX >= 0.3125 && hitX <= 0.6875 && hitY >= 0.52 && hitZ >= 0.625 && hitZ <= 0.9375)
            return f == Direction.NORTH ? 2: f == Direction.SOUTH ? -2: f == Direction.EAST ? 1: 0;
        else if (hitX >= 0.3125 && hitX <= 0.6875 && hitY >= 0.52 && hitZ >= 0.0625 && hitZ <= 0.375)
            return f == Direction.NORTH ? -2: f == Direction.SOUTH ? 2: f == Direction.EAST ? 0: 1;
        else if (hitX >= 0.0625 && hitX <= 0.375 && hitY >= 0.52 && hitZ >= 0.3125 && hitZ <= 0.6875)
            return f == Direction.NORTH ? 0: f == Direction.SOUTH ? 1: f == Direction.EAST ? 2: -2;
        else if (hitX >= 0.625 && hitX <= 0.9375 && hitY >= 0.52 && hitZ >= 0.3125 && hitZ <= 0.6875)
            return f == Direction.NORTH ? 1: f == Direction.SOUTH ? 0: f == Direction.EAST ? -2: 2;
*/
        return -2;
    }

    @Nonnull
    @Override
    public Class<?> getTileClass() {
        return HandMillstoneTileEntity.class;
    }

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (player instanceof FakePlayer || player == null)
            return true;

        HandMillstoneTileEntity tile = getTileEntity(worldIn, pos);
        if (tile != null && tile.canWork() && !player.isSneaking()) {
            if (!worldIn.isRemote) {
                if (tile.turn())
                    player.addExhaustion(Configs.SERVER.millstoneExhaustion.get().floatValue());
                return true;
            } else
                return true;
        }

        return super.onBlockActivated(state, worldIn, pos, player, hand, hit);
    }

    @Override
    public void emptiedOutput(World world, BlockPos pos) {}

    @Override
    public boolean hasCustomBreakingProgress(BlockState state) {
        return true;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        worldIn.setBlockState(pos, state.with(FACING, placer.getHorizontalFacing().getOpposite()).with(PART, HandMillstoneModels.BASE), 2);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new StringTextComponent(Localization.ITEM.HAND_MILLSTONE.INFO.translate("\n" + Colors.LIGHTGRAY.toString())));
    }

    // The One Probe Integration
    /*@Optional.Method(modid = "theoneprobe")
    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        TileEntityHandMillstone tileEntity = (TileEntityHandMillstone) world.getTileEntity(data.getPos());
        if (tileEntity != null) {
            probeInfo.progress((long) ((((double) tileEntity.getField(1)) / ((double) tileEntity.getField(0))) * 100L), 100L, new ProgressStyle().prefix(Localization.TOP.MILLSTONE_PROGRESS.translate() + " ").suffix("%"));
        }
    }*/
}
