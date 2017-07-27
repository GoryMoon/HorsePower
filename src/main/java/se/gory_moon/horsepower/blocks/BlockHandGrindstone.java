package se.gory_moon.horsepower.blocks;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoAccessor;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.apiimpl.styles.ProgressStyle;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.Optional;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.HorsePowerMod;
import se.gory_moon.horsepower.blocks.property.PropertyUnlistedDirection;
import se.gory_moon.horsepower.client.model.modelvariants.HandGrindstoneModels;
import se.gory_moon.horsepower.lib.Constants;
import se.gory_moon.horsepower.tileentity.TileEntityHandGrindstone;
import se.gory_moon.horsepower.util.Localization;
import se.gory_moon.horsepower.util.color.Colors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@Optional.Interface(iface = "mcjty.theoneprobe.api.IProbeInfoAccessor", modid = "theoneprobe")
public class BlockHandGrindstone extends BlockHPBase implements IProbeInfoAccessor {

    public static final PropertyUnlistedDirection FACING = new PropertyUnlistedDirection("facing");
    public static final PropertyEnum<HandGrindstoneModels> PART = PropertyEnum.create("part", HandGrindstoneModels.class);

    private static final AxisAlignedBB COLLISION_AABB = new AxisAlignedBB(1D/16D, 0.0D, 1D/16D, 15D/16D, 10D/16D, 15D/16D);
    private static final AxisAlignedBB BOUNDING_AABB = new AxisAlignedBB(1D/16D, 0.0D, 1D/16D, 15D/16D, 14D/16D, 15D/16D);

    public BlockHandGrindstone() {
        super(Material.ROCK);
        setHardness(1.5F);
        setResistance(10F);
        setHarvestLevel("pickaxe", 1);
        setSoundType(SoundType.STONE);
        setRegistryName(Constants.HAND_GRINDSTONE_BLOCK);
        setUnlocalizedName(Constants.HAND_GRINDSTONE_BLOCK);
        setCreativeTab(HorsePowerMod.creativeTab);
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return COLLISION_AABB;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return COLLISION_AABB;
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
        return EMPTY_AABB;
    }

    @Override
    public int getSlot(IBlockState state, float hitX, float hitY, float hitZ) {
        EnumFacing f = ((IExtendedBlockState)state).getValue(FACING).getOpposite();
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
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote) {
            IExtendedBlockState extendedState = (IExtendedBlockState) getExtendedState(state, worldIn, pos);
            EnumFacing enumfacing = extendedState.getValue(FACING);
            worldIn.setBlockState(pos, extendedState.withProperty(FACING, enumfacing).withProperty(PART, HandGrindstoneModels.BASE), 2);
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
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

        return super.onBlockActivated(worldIn, pos, state, player, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public void emptiedOutput(World world, BlockPos pos) {

    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityHandGrindstone();
    }

    @Override
    public boolean hasCustomBreakingProgress(IBlockState state) {
        return true;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new ExtendedBlockState(this, new IProperty[] {PART}, new IUnlistedProperty[]{FACING});
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntityHandGrindstone tile = getTileEntity(world, pos);
        if (tile == null)
            return state;

        return ((IExtendedBlockState) state).withProperty(FACING, tile.getForward()).withProperty(PART, state.getValue(PART));
    }

    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.setBlockState(pos, ((IExtendedBlockState)state).withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(PART, HandGrindstoneModels.BASE), 2);

        TileEntityHandGrindstone tile = getTileEntity(worldIn, pos);
        if (tile == null)
            return;
        tile.setForward(placer.getAdjustedHorizontalFacing().getOpposite());
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        tooltip.add(Localization.ITEM.HAND_GRINDSTONE.INFO.translate("\n" + Colors.LIGHTGRAY.toString()));
    }

    // The One Probe Integration
    @Optional.Method(modid = "theoneprobe")
    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        TileEntityHandGrindstone tileEntity = (TileEntityHandGrindstone) world.getTileEntity(data.getPos());
        if (tileEntity != null) {
            probeInfo.progress((long) ((((double) tileEntity.getField(1)) / ((double) tileEntity.getField(0))) * 100L), 100L, new ProgressStyle().prefix(Localization.TOP.GRINDSTONE_PROGRESS.translate() + " ").suffix("%"));
        }
    }
}
