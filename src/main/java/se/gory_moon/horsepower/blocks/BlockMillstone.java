package se.gory_moon.horsepower.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import se.gory_moon.horsepower.advancements.AdvancementManager;
import se.gory_moon.horsepower.client.model.modelvariants.MillstoneModels;
import se.gory_moon.horsepower.tileentity.TileEntityMillstone;
import se.gory_moon.horsepower.util.Localization;
import se.gory_moon.horsepower.util.color.Colors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class BlockMillstone extends BlockHPBase {

    public static final BooleanProperty FILLED = BooleanProperty.create("filled");
    public static final EnumProperty<MillstoneModels> PART = EnumProperty.create("part", MillstoneModels.class);

    private static final VoxelShape COLLISION = Block.makeCuboidShape(0, 0, 0, 16, 8, 16);
    private static final VoxelShape BOUNDING = Block.makeCuboidShape(0, 0, 0, 16, 13, 16);

    public BlockMillstone() {
        super(Properties.create(Material.ROCK).hardnessAndResistance(1.5F, 10F).sound(SoundType.STONE));

        setHarvestLevel(ToolType.PICKAXE, 1);
        setDefaultState(getStateContainer().getBaseState().with(FILLED, false).with(PART, MillstoneModels.BASE));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return BOUNDING;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return COLLISION;
    }

    @Override
    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!worldIn.isRemote) {
            boolean filled = state.get(FILLED);
            worldIn.setBlockState(pos, state.with(FILLED, filled).with(PART, MillstoneModels.BASE), 2);
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(FILLED, PART);
    }

    public static void setState(boolean filled, World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        world.setBlockState(pos, state.with(FILLED, filled), 2);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new StringTextComponent(Localization.ITEM.HORSE_MILLSTONE.SIZE.translate(Colors.LIGHTGRAY.toString(), Colors.WHITE.toString())));
        tooltip.add(new StringTextComponent(Localization.ITEM.HORSE_MILLSTONE.LOCATION.translate(Colors.LIGHTGRAY.toString(), Colors.WHITE.toString())));
        tooltip.add(new StringTextComponent(Localization.ITEM.HORSE_MILLSTONE.USE.translate()));
    }

    @Override
    public void onWorkerAttached(PlayerEntity playerIn, CreatureEntity creature) {
        if (playerIn instanceof ServerPlayerEntity)
            AdvancementManager.USE_MILLSTONE.trigger((ServerPlayerEntity) playerIn);
    }

    @Override
    public void emptiedOutput(World world, BlockPos pos) {
        BlockMillstone.setState(false, world, pos);
    }

    @Nonnull
    @Override
    public Class<?> getTileClass() {
        return TileEntityMillstone.class;
    }

    // The One Probe Integration
    /*@Optional.Method(modid = "theoneprobe")
    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        TileEntityMillstone tileEntity = getTileEntity(world, data.getPos());
        if (tileEntity != null) {
            probeInfo.progress((long) ((((double) tileEntity.getField(1)) / ((double) tileEntity.getField(0))) * 100L), 100L, new ProgressStyle().prefix(Localization.TOP.MILLSTONE_PROGRESS.translate() + " ").suffix("%"));
        }
    }*/
}
