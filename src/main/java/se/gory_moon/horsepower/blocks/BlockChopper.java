package se.gory_moon.horsepower.blocks;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoAccessor;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.apiimpl.styles.ProgressStyle;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.common.Optional;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.client.renderer.ChopperModels;
import se.gory_moon.horsepower.lib.Constants;
import se.gory_moon.horsepower.tileentity.TileEntityChopper;
import se.gory_moon.horsepower.tileentity.TileEntityHPBase;
import se.gory_moon.horsepower.util.Colors;
import se.gory_moon.horsepower.util.Localization;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

@Optional.Interface(iface = "mcjty.theoneprobe.api.IProbeInfoAccessor", modid = "theoneprobe")
public class BlockChopper extends BlockHPBase implements IProbeInfoAccessor {

    public static final UnlistedDirection FACING = new UnlistedDirection("facing");
    public static final PropertyDirection DIRECTION = PropertyDirection.create("facing", Arrays.asList(EnumFacing.HORIZONTALS));
    public static final PropertyEnum<ChopperModels> PART = PropertyEnum.create("part", ChopperModels.class);

    private static final AxisAlignedBB COLLISION_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 2.0D, 1.0D);

    public BlockChopper() {
        super(Material.WOOD);
        setHardness(2.0F);
        setResistance(5.0F);
        setSoundType(SoundType.WOOD);
        setRegistryName(Constants.CHOPPER_BLOCK);
        setUnlocalizedName(Constants.CHOPPER_BLOCK);
        setCreativeTab(CreativeTabs.DECORATIONS);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityChopper();
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return COLLISION_AABB;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return COLLISION_AABB;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean hasCustomBreakingProgress(IBlockState state) {
        return true;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new ExtendedBlockState(this, new IProperty[] {PART, DIRECTION}, new IUnlistedProperty[]{FACING});
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

        return ((IExtendedBlockState) state).withProperty(FACING, tile.getForward()).withProperty(DIRECTION, tile.getForward()).withProperty(PART, state.getValue(PART));
    }

    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.setBlockState(pos, ((IExtendedBlockState)state).withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(DIRECTION, placer.getHorizontalFacing().getOpposite()), 2);

        TileEntityHPBase tile = getTileEntity(worldIn, pos);
        if (tile == null)
            return;
        tile.setForward(placer.getAdjustedHorizontalFacing().getOpposite());
    }

    @Override
    public void emptiedOutput(World world, BlockPos pos) {}

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        tooltip.add(Localization.ITEM.HORSE_CHOPPING.SIZE.translate(Colors.WHITE.toString(), Colors.LIGHTGRAY.toString()));
        tooltip.add(Localization.ITEM.HORSE_CHOPPING.LOCATION.translate());
        tooltip.add(Localization.ITEM.HORSE_CHOPPING.USE.translate());
    }

    // The One Probe Integration
    @Optional.Method(modid = "theoneprobe")
    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        TileEntity tileEntity = world.getTileEntity(data.getPos());
        if (tileEntity instanceof TileEntityChopper) {
            TileEntityChopper te = (TileEntityChopper) tileEntity;
            double totalWindup = Configs.pointsForWindup > 0 ? Configs.pointsForWindup: 1;
            probeInfo.progress((long) ((((double)te.getField(2)) / totalWindup) * 100L), 100L, new ProgressStyle().prefix(Localization.TOP.WINDUP_PROGRESS.translate() + " ").suffix("%"));
            if (te.getField(1) > 1)
                probeInfo.progress((long) ((((double)te.getField(1)) / ((double)te.getField(0))) * 100L), 100L, new ProgressStyle().prefix(Localization.TOP.CHOPPING_PROGRESS.translate() + " ").suffix("%"));
        }
    }
}
