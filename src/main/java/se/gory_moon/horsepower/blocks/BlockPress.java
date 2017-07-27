package se.gory_moon.horsepower.blocks;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoAccessor;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.apiimpl.styles.ProgressStyle;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
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
import net.minecraftforge.fml.common.Optional;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.HorsePowerMod;
import se.gory_moon.horsepower.client.model.modelvariants.PressModels;
import se.gory_moon.horsepower.lib.Constants;
import se.gory_moon.horsepower.tileentity.TileEntityHPBase;
import se.gory_moon.horsepower.tileentity.TileEntityPress;
import se.gory_moon.horsepower.util.Localization;
import se.gory_moon.horsepower.util.color.Colors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

@Optional.Interface(iface = "mcjty.theoneprobe.api.IProbeInfoAccessor", modid = "theoneprobe")
public class BlockPress extends BlockHPBase implements IProbeInfoAccessor {

    public static final PropertyDirection FACING = PropertyDirection.create("facing", Arrays.asList(EnumFacing.HORIZONTALS));
    public static final PropertyEnum<PressModels> PART = PropertyEnum.create("part", PressModels.class);

    private static final AxisAlignedBB COLLISION_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D + 12D/16D, 1.0D);

    public BlockPress() {
        super(Material.ROCK);
        setHardness(5F);
        setResistance(5F);
        setHarvestLevel("axe", 1);
        setSoundType(SoundType.WOOD);
        setRegistryName(Constants.PRESS_BLOCK);
        setUnlocalizedName(Constants.PRESS_BLOCK);
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
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote) {
            EnumFacing filled = state.getValue(FACING);
            worldIn.setBlockState(pos, state.withProperty(FACING, filled).withProperty(PART, PressModels.BASE), 2);
        }
    }
    
    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityPress();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, PART);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex();
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(PART, PressModels.BASE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta)).withProperty(PART, PressModels.BASE);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);

        TileEntityHPBase tile = getTileEntity(worldIn, pos);
        if (tile == null)
            return;
        tile.setForward(placer.getHorizontalFacing().getOpposite());
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
    }

    // The One Probe Integration
    @Optional.Method(modid = "theoneprobe")
    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        TileEntityPress tileEntity = getTileEntity(world, data.getPos());
        if (tileEntity != null) {
            probeInfo.progress((long) ((((double) tileEntity.getField(0)) / (float)(Configs.general.pointsForPress > 0 ? Configs.general.pointsForPress: 1)) * 100L), 100L, new ProgressStyle().prefix(Localization.TOP.PRESS_PROGRESS.translate() + " ").suffix("%"));
        }
    }

    @Override
    public void emptiedOutput(World world, BlockPos pos) {}

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        tooltip.add(Localization.ITEM.HORSE_PRESS.SIZE.translate(Colors.WHITE.toString(), Colors.LIGHTGRAY.toString()));
        tooltip.add(Localization.ITEM.HORSE_PRESS.LOCATION.translate());
        tooltip.add(Localization.ITEM.HORSE_PRESS.USE.translate());
    }

    @Nonnull
    @Override
    public Class<?> getTileClass() {
        return TileEntityPress.class;
    }
}
