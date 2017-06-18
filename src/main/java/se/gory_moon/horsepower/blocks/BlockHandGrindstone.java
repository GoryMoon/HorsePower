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
import net.minecraft.creativetab.CreativeTabs;
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
import se.gory_moon.horsepower.client.renderer.HandGrindstoneModels;
import se.gory_moon.horsepower.lib.Constants;
import se.gory_moon.horsepower.tileentity.TileEntityGrindstone;
import se.gory_moon.horsepower.tileentity.TileEntityHPBase;
import se.gory_moon.horsepower.tileentity.TileEntityHandGrindstone;

import javax.annotation.Nullable;

@Optional.Interface(iface = "mcjty.theoneprobe.api.IProbeInfoAccessor", modid = "theoneprobe")
public class BlockHandGrindstone extends BlockHPBase implements IProbeInfoAccessor {

    public static final UnlistedDirection FACING = new UnlistedDirection("facing");
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
        setCreativeTab(CreativeTabs.DECORATIONS);
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BOUNDING_AABB;
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
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

        if (player instanceof FakePlayer || player == null)
            return true;

        TileEntityHPBase tile = getTileEntity(worldIn, pos);
        if (tile instanceof TileEntityHandGrindstone && tile.canWork() && !player.isSneaking()) {
            ((TileEntityHandGrindstone) tile).turn();
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
    protected BlockStateContainer createBlockState() {
        return new ExtendedBlockState(this, new IProperty[] {PART}, new IUnlistedProperty[]{FACING});
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntityHPBase tile = getTileEntity(world, pos);
        if (tile == null)
            return state;

        return ((IExtendedBlockState) state).withProperty(FACING, tile.getForward()).withProperty(PART, HandGrindstoneModels.BASE);
    }

    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.setBlockState(pos, ((IExtendedBlockState)state).withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);

        TileEntityHPBase tile = getTileEntity(worldIn, pos);
        if (tile == null)
            return;
        tile.setForward(placer.getAdjustedHorizontalFacing().getOpposite());
    }


    // The One Probe Integration
    @Optional.Method(modid = "theoneprobe")
    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        TileEntity tileEntity = world.getTileEntity(data.getPos());
        if (tileEntity instanceof TileEntityHandGrindstone) {
            TileEntityHandGrindstone te = (TileEntityHandGrindstone) tileEntity;
            probeInfo.progress((long) ((((double)te.getField(1)) / ((double)te.getField(0))) * 100L), 100L, new ProgressStyle().suffix("%"));
        }
    }
}
