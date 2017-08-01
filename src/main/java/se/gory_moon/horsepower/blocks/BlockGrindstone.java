package se.gory_moon.horsepower.blocks;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoAccessor;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.apiimpl.styles.ProgressStyle;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import se.gory_moon.horsepower.advancements.Manager;
import se.gory_moon.horsepower.client.model.modelvariants.GrindStoneModels;
import se.gory_moon.horsepower.lib.Constants;
import se.gory_moon.horsepower.tileentity.TileEntityGrindstone;
import se.gory_moon.horsepower.util.Localization;
import se.gory_moon.horsepower.util.color.Colors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@Optional.Interface(iface = "mcjty.theoneprobe.api.IProbeInfoAccessor", modid = "theoneprobe")
public class BlockGrindstone extends BlockHPBase implements IProbeInfoAccessor {

    public static final PropertyBool FILLED = PropertyBool.create("filled");
    public static final PropertyEnum<GrindStoneModels> PART = PropertyEnum.create("part", GrindStoneModels.class);

    private static final AxisAlignedBB COLLISION_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 8D/16D, 1.0D);
    private static final AxisAlignedBB BOUNDING_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 13D/16D, 1.0D);

    public BlockGrindstone() {
        super(Material.ROCK);
        setHardness(1.5F);
        setResistance(10F);
        setHarvestLevel("pickaxe", 1);
        setSoundType(SoundType.STONE);
        setRegistryName(Constants.GRINDSTONE_BLOCK);
        setUnlocalizedName(Constants.GRINDSTONE_BLOCK);
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
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote) {
            boolean filled = state.getValue(FILLED);
            worldIn.setBlockState(pos, state.withProperty(FILLED, filled).withProperty(PART, GrindStoneModels.BASE), 2);
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FILLED, PART);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FILLED) ? 1: 0;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FILLED, meta == 1).withProperty(PART, GrindStoneModels.BASE);
    }

    public static void setState(boolean filled, World worldIn, BlockPos pos) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        keepInventory = true;
        worldIn.setBlockState(pos, ModBlocks.BLOCK_GRINDSTONE.getDefaultState().withProperty(FILLED, filled).withProperty(PART, GrindStoneModels.BASE), 3);
        keepInventory = false;

        if (tileentity != null) {
            tileentity.validate();
            worldIn.setTileEntity(pos, tileentity);
            tileentity.validate();
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced) {
        tooltip.add(Localization.ITEM.HORSE_GRINDSTONE.SIZE.translate(Colors.WHITE.toString(), Colors.LIGHTGRAY.toString()));
        tooltip.add(Localization.ITEM.HORSE_GRINDSTONE.LOCATION.translate(Colors.WHITE.toString(), Colors.LIGHTGRAY.toString()));
        tooltip.add(Localization.ITEM.HORSE_GRINDSTONE.USE.translate());
    }

    // The One Probe Integration
    @Optional.Method(modid = "theoneprobe")
    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        TileEntityGrindstone tileEntity = getTileEntity(world, data.getPos());
        if (tileEntity != null) {
            probeInfo.progress((long) ((((double) tileEntity.getField(1)) / ((double) tileEntity.getField(0))) * 100L), 100L, new ProgressStyle().prefix(Localization.TOP.GRINDSTONE_PROGRESS.translate() + " ").suffix("%"));
        }
    }

    @Override
    public void onWorkerAttached(EntityPlayer playerIn, EntityCreature creature) {
        if (playerIn instanceof EntityPlayerMP)
            Manager.USE_GRINDSTONE.trigger((EntityPlayerMP) playerIn);
    }

    @Override
    public void emptiedOutput(World world, BlockPos pos) {
        BlockGrindstone.setState(false, world, pos);
    }

    @Nonnull
    @Override
    public Class<?> getTileClass() {
        return TileEntityGrindstone.class;
    }
}
