package se.gory_moon.horsepower.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.client.model.modelvariants.HandGrindstoneModels;
import se.gory_moon.horsepower.lib.Constants;
import se.gory_moon.horsepower.lib.Reference;
import se.gory_moon.horsepower.tileentity.TileEntityHandGrindstone;
import se.gory_moon.horsepower.util.Localization;
import se.gory_moon.horsepower.util.color.Colors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

//@Optional.Interface(iface = "mcjty.theoneprobe.api.IProbeInfoAccessor", modid = "theoneprobe")
public class BlockHandGrindstone extends BlockHPBase {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<HandGrindstoneModels> PART = EnumProperty.create("part", HandGrindstoneModels.class);

    private static final VoxelShape COLLISION = Block.makeCuboidShape(1, 0, 1, 15, 10, 15);

    public BlockHandGrindstone() {
        super(Properties.create(Material.ROCK).hardnessAndResistance(1.5F, 10F));
        setRegistryName(Reference.MODID, Constants.HAND_GRINDSTONE_BLOCK);

        /*setHarvestLevel("pickaxe", 1);
        setSoundType(SoundType.STONE);
        setUnlocalizedName(Constants.HAND_GRINDSTONE_BLOCK);*/
        setDefaultState(getStateContainer().getBaseState().with(FACING, EnumFacing.NORTH).with(PART, HandGrindstoneModels.BASE));
    }

    @Override
    public VoxelShape getShape(IBlockState state, IBlockReader world, BlockPos pos) {
        return COLLISION;
    }

    @Override
    public int getSlot(IBlockState state, float hitX, float hitY, float hitZ) {
        EnumFacing f = state.get(FACING).getOpposite();
        if (hitX >= 0.3125 && hitX <= 0.6875 && hitY >= 0.52 && hitZ >= 0.625 && hitZ <= 0.9375)
            return f == EnumFacing.NORTH ? 2: f == EnumFacing.SOUTH ? -2: f == EnumFacing.EAST ? 1: 0;
        else if (hitX >= 0.3125 && hitX <= 0.6875 && hitY >= 0.52 && hitZ >= 0.0625 && hitZ <= 0.375)
            return f == EnumFacing.NORTH ? -2: f == EnumFacing.SOUTH ? 2: f == EnumFacing.EAST ? 0: 1;
        else if (hitX >= 0.0625 && hitX <= 0.375 && hitY >= 0.52 && hitZ >= 0.3125 && hitZ <= 0.6875)
            return f == EnumFacing.NORTH ? 0: f == EnumFacing.SOUTH ? 1: f == EnumFacing.EAST ? 2: -2;
        else if (hitX >= 0.625 && hitX <= 0.9375 && hitY >= 0.52 && hitZ >= 0.3125 && hitZ <= 0.6875)
            return f == EnumFacing.NORTH ? 1: f == EnumFacing.SOUTH ? 0: f == EnumFacing.EAST ? -2: 2;

        return -2;
    }

    @Nonnull
    @Override
    public Class<?> getTileClass() {
        return TileEntityHandGrindstone.class;
    }

    @Override
    public void onBlockAdded(IBlockState state, World worldIn, BlockPos pos, IBlockState oldState) {
        if (!worldIn.isRemote) {
            state = getExtendedState(state, worldIn, pos);
            EnumFacing enumfacing = state.get(FACING);
            worldIn.setBlockState(pos, state.with(FACING, enumfacing).with(PART, HandGrindstoneModels.BASE), 2);
        }
    }

    @Override
    public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (player instanceof FakePlayer || player == null)
            return true;

        TileEntityHandGrindstone tile = getTileEntity(worldIn, pos);
        if (tile != null && tile.canWork() && !player.isSneaking()) {
            if (!worldIn.isRemote) {
                if (tile.turn())
                    player.addExhaustion((float) Configs.general.grindstoneExhaustion);
                return true;
            } else
                return true;
        }

        return super.onBlockActivated(state, worldIn, pos, player, hand, side, hitX, hitY, hitZ);
    }

    @Override
    public void emptiedOutput(World world, BlockPos pos) {}

    @Override
    public boolean hasCustomBreakingProgress(IBlockState state) {
        return true;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
        builder.add(FACING, PART);
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockReader world, BlockPos pos) {
        TileEntityHandGrindstone tile = getTileEntity(world, pos);
        if (tile == null)
            return state;

        return state.with(FACING, tile.getForward()).with(PART, state.get(PART));
    }

    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.setBlockState(pos, state.with(FACING, placer.getHorizontalFacing().getOpposite()).with(PART, HandGrindstoneModels.BASE), 2);

        TileEntityHandGrindstone tile = getTileEntity(worldIn, pos);
        if (tile == null)
            return;
        tile.setForward(placer.getAdjustedHorizontalFacing().getOpposite());
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TextComponentString(Localization.ITEM.HAND_GRINDSTONE.INFO.translate("\n" + Colors.LIGHTGRAY.toString())));
    }

    // The One Probe Integration
    /*@Optional.Method(modid = "theoneprobe")
    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        TileEntityHandGrindstone tileEntity = (TileEntityHandGrindstone) world.getTileEntity(data.getPos());
        if (tileEntity != null) {
            probeInfo.progress((long) ((((double) tileEntity.getField(1)) / ((double) tileEntity.getField(0))) * 100L), 100L, new ProgressStyle().prefix(Localization.TOP.GRINDSTONE_PROGRESS.translate() + " ").suffix("%"));
        }
    }*/
}
