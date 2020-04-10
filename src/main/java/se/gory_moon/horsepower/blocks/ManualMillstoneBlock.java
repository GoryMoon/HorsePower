package se.gory_moon.horsepower.blocks;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.util.FakePlayer;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.client.model.modelvariants.ManualMillstoneModels;
import se.gory_moon.horsepower.tileentity.ManualMillstoneTileEntity;
import se.gory_moon.horsepower.util.Localization;
import se.gory_moon.horsepower.util.color.Colors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ManualMillstoneBlock extends HPBaseBlock {
    public static final EnumProperty<ManualMillstoneModels> PART = EnumProperty.create("part", ManualMillstoneModels.class);

    private static final VoxelShape COLLISION = Block.makeCuboidShape(1, 0, 1, 15, 10, 15);

    private static final VoxelShape HIT_NORTH = Block.makeCuboidShape(6, 10, 2, 10, 11, 6);
    private static final VoxelShape HIT_WEST = Block.makeCuboidShape(2, 10, 6, 6, 11, 10);
    private static final VoxelShape HIT_SOUTH = Block.makeCuboidShape(6, 10, 10, 10, 11, 14);
    private static final VoxelShape HIT_EAST = Block.makeCuboidShape(10, 10, 6, 14, 11, 10);

    // VoxelShape for each rotation
    private static final VoxelShape ROT_NORTH = VoxelShapes.or(COLLISION, HIT_NORTH, HIT_WEST, HIT_EAST);
    private static final VoxelShape ROT_EAST = VoxelShapes.or(COLLISION, HIT_EAST, HIT_NORTH, HIT_SOUTH);
    private static final VoxelShape ROT_SOUTH = VoxelShapes.or(COLLISION, HIT_SOUTH, HIT_WEST, HIT_EAST);
    private static final VoxelShape ROT_WEST = VoxelShapes.or(COLLISION, HIT_WEST, HIT_NORTH, HIT_SOUTH);

    // Slot aabb and ids
    private static final AxisAlignedBB BB_NORTH = HIT_NORTH.getBoundingBox();
    private static final AxisAlignedBB BB_EAST = HIT_EAST.getBoundingBox();
    private static final AxisAlignedBB BB_SOUTH = HIT_SOUTH.getBoundingBox();
    private static final AxisAlignedBB BB_WEST = HIT_WEST.getBoundingBox();
    private static final List<Slot> NORTH_SLOTS = ImmutableList.of(new Slot(1, BB_WEST), new Slot(0, BB_EAST), new Slot(2, BB_NORTH));
    private static final List<Slot> SOUTH_SLOTS = ImmutableList.of(new Slot(1, BB_EAST), new Slot(0, BB_WEST), new Slot(2, BB_SOUTH));
    private static final List<Slot> WEST_SLOTS = ImmutableList.of(new Slot(1, BB_SOUTH), new Slot(0, BB_NORTH), new Slot(2, BB_EAST));
    private static final List<Slot> EAST_SLOTS = ImmutableList.of(new Slot(1, BB_NORTH), new Slot(0, BB_SOUTH), new Slot(2, BB_WEST));

    public ManualMillstoneBlock(Properties properties) {
        super(properties.hardnessAndResistance(1.5F, 10F).sound(SoundType.STONE));

        setHarvestLevel(ToolType.PICKAXE, 1);
        setDefaultState(getStateContainer().getBaseState().with(FACING, Direction.NORTH).with(PART, ManualMillstoneModels.BASE));
    }

    @Override
    public void emptiedOutput(World world, BlockPos pos) {}

    @Override
    public int getSlot(BlockState state, World worldIn, PlayerEntity player, BlockRayTraceResult hit) {
        Direction dir = state.get(FACING);
        Vec3d traceStart = player.getEyePosition(0);
        Vec3d lookVector = player.getLook(0);
        Vec3d traceEnd = traceStart.add(lookVector.x * 5, lookVector.y * 5, lookVector.z * 5);

        Stream<Slot> slots;
        if (dir == Direction.NORTH) {
            slots = NORTH_SLOTS.stream();
        } else if (dir == Direction.SOUTH) {
            slots = SOUTH_SLOTS.stream();
        } else if (dir == Direction.EAST) {
            slots = EAST_SLOTS.stream();
        } else if (dir == Direction.WEST) {
            slots = WEST_SLOTS.stream();
        } else {
            return -2;
        }

        Optional<Integer> closest = slots
                .map(slot -> Pair.of(slot, slot.getAABB().offset(hit.getPos()).rayTrace(traceStart, traceEnd).orElse(Vec3d.ZERO)))
                .filter(pair -> pair.getSecond() != Vec3d.ZERO)
                .min(Comparator.comparingDouble(o -> o.getSecond().lengthSquared())).map(pair -> pair.getFirst().getId());
        return closest.orElse(-2);
    }

    @Nonnull
    @Override
    public Class<?> getTileClass() {
        return ManualMillstoneTileEntity.class;
    }

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (player instanceof FakePlayer || player == null)
            return true;

        ManualMillstoneTileEntity tile = getTileEntity(worldIn, pos);
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
    public boolean hasCustomBreakingProgress(BlockState state) {
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        if (context.isSneaking()) {
            switch (state.get(FACING)) {
                case SOUTH:
                    return ROT_SOUTH;
                case WEST:
                    return ROT_WEST;
                case EAST:
                    return ROT_EAST;
                default:
                    return ROT_NORTH;
            }
        }
        return COLLISION;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        worldIn.setBlockState(pos, state.with(FACING, placer.getHorizontalFacing().getOpposite()).with(PART, ManualMillstoneModels.BASE), 2);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new StringTextComponent(Localization.ITEM.MANUAL_MILLSTONE.INFO.translate("\n" + Colors.LIGHTGRAY.toString())));
    }

    private static class Slot {
        private AxisAlignedBB aabb;
        private int id;

        public Slot(int id, AxisAlignedBB aabb) {
            this.id = id;
            this.aabb = aabb;
        }

        public AxisAlignedBB getAABB() {
            return aabb;
        }

        public int getId() {
            return id;
        }
    }
}
