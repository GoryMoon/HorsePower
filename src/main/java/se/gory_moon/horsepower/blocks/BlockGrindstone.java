package se.gory_moon.horsepower.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ShapeUtils;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import se.gory_moon.horsepower.advancements.Manager;
import se.gory_moon.horsepower.client.model.modelvariants.GrindStoneModels;
import se.gory_moon.horsepower.lib.Constants;
import se.gory_moon.horsepower.lib.Reference;
import se.gory_moon.horsepower.tileentity.TileEntityGrindstone;
import se.gory_moon.horsepower.util.Localization;
import se.gory_moon.horsepower.util.color.Colors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class BlockGrindstone extends BlockHPBase {

    public static final BooleanProperty FILLED = BooleanProperty.create("filled");
    public static final EnumProperty<GrindStoneModels> PART = EnumProperty.create("part", GrindStoneModels.class);

    private static final AxisAlignedBB COLLISION_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 8D/16D, 1.0D);
    private static final AxisAlignedBB BOUNDING_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 13D/16D, 1.0D);

    public BlockGrindstone() {
        super(Builder.create(Material.ROCK).hardnessAndResistance(1.5F, 10F));
        setRegistryName(Reference.RESOURCE_PREFIX + Constants.GRINDSTONE_BLOCK);

        /*setHarvestLevel("pickaxe", 1);
        setSoundType(SoundType.STONE);
        setUnlocalizedName(Constants.GRINDSTONE_BLOCK);*/
    }

    @Override
    public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
        return ShapeUtils.create(BOUNDING_AABB);
    }

    @Override
    public VoxelShape getCollisionShape(IBlockState p_196268_1_, IBlockReader p_196268_2_, BlockPos p_196268_3_) {
        return ShapeUtils.create(COLLISION_AABB);
    }

    @Override
    public void onBlockAdded(IBlockState state, World worldIn, BlockPos pos, IBlockState oldState) {
        if (!worldIn.isRemote) {
            boolean filled = state.get(FILLED);
            worldIn.setBlockState(pos, state.with(FILLED, filled).with(PART, GrindStoneModels.BASE), 2);
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> stateBuilder) {
        stateBuilder.add(FILLED, PART);
    }

    public static void setState(boolean filled, World worldIn, BlockPos pos) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        keepInventory = true;
        worldIn.setBlockState(pos, ModBlocks.BLOCK_GRINDSTONE.getDefaultState().with(FILLED, filled).with(PART, GrindStoneModels.BASE), 3);
        keepInventory = false;

        if (tileentity != null) {
            tileentity.validate();
            worldIn.setTileEntity(pos, tileentity);
            tileentity.validate();
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TextComponentString(Localization.ITEM.HORSE_GRINDSTONE.SIZE.translate(Colors.LIGHTGRAY.toString(), Colors.WHITE.toString())));
        tooltip.add(new TextComponentString(Localization.ITEM.HORSE_GRINDSTONE.LOCATION.translate(Colors.LIGHTGRAY.toString(), Colors.WHITE.toString())));
        tooltip.add(new TextComponentString(Localization.ITEM.HORSE_GRINDSTONE.USE.translate()));
    }

    // The One Probe Integration
    /*@Optional.Method(modid = "theoneprobe")
    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        TileEntityGrindstone tileEntity = getTileEntity(world, data.getPos());
        if (tileEntity != null) {
            probeInfo.progress((long) ((((double) tileEntity.getField(1)) / ((double) tileEntity.getField(0))) * 100L), 100L, new ProgressStyle().prefix(Localization.TOP.GRINDSTONE_PROGRESS.translate() + " ").suffix("%"));
        }
    }*/

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
