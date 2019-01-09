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

    private static final VoxelShape COLLISION = Block.makeCuboidShape(0, 0, 0, 16, 8, 16);
    private static final VoxelShape BOUNDING = Block.makeCuboidShape(0, 0, 0, 16, 13, 16);

    public BlockGrindstone() {
        super(Builder.create(Material.ROCK).hardnessAndResistance(1.5F, 10F));
        setRegistryName(Reference.MODID, Constants.GRINDSTONE_BLOCK);

        /*setHarvestLevel("pickaxe", 1);
        setSoundType(SoundType.STONE);
        setUnlocalizedName(Constants.GRINDSTONE_BLOCK);*/
        setDefaultState(getStateContainer().getBaseState().with(FILLED, false).with(PART, GrindStoneModels.BASE));
    }

    @Override
    public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
        return BOUNDING;
    }

    @Override
    public VoxelShape getCollisionShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
        return COLLISION;
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

    public static void setState(boolean filled, World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        world.setBlockState(pos, state.with(FILLED, filled), 2);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TextComponentString(Localization.ITEM.HORSE_GRINDSTONE.SIZE.translate(Colors.LIGHTGRAY.toString(), Colors.WHITE.toString())));
        tooltip.add(new TextComponentString(Localization.ITEM.HORSE_GRINDSTONE.LOCATION.translate(Colors.LIGHTGRAY.toString(), Colors.WHITE.toString())));
        tooltip.add(new TextComponentString(Localization.ITEM.HORSE_GRINDSTONE.USE.translate()));
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

    // The One Probe Integration
    /*@Optional.Method(modid = "theoneprobe")
    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        TileEntityGrindstone tileEntity = getTileEntity(world, data.getPos());
        if (tileEntity != null) {
            probeInfo.progress((long) ((((double) tileEntity.getField(1)) / ((double) tileEntity.getField(0))) * 100L), 100L, new ProgressStyle().prefix(Localization.TOP.GRINDSTONE_PROGRESS.translate() + " ").suffix("%"));
        }
    }*/
}
