package se.gory_moon.horsepower.blocks;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ContainerBlock;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.LeadItem;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.items.ItemHandlerHelper;
import se.gory_moon.horsepower.tileentity.HPBaseTileEntity;
import se.gory_moon.horsepower.tileentity.HPHorseBaseTileEntity;
import se.gory_moon.horsepower.util.Utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

public abstract class HPBaseBlock extends ContainerBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private ToolType type;
    private int level;

    public HPBaseBlock(Properties builder) {
        super(builder);
    }

    public abstract void emptiedOutput(World world, BlockPos pos);

    public int getSlot(BlockState state, BlockRayTraceResult hit) {
        return -1;
    }

    public HPBaseBlock setHarvestLevel(ToolType type, int level) {
        this.type = type;
        this.level = level;
        return this;
    }

    public void onWorkerAttached(PlayerEntity playerIn, CreatureEntity creature) {}

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return createNewTileEntity(world);
    }

    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, IFluidState fluid) {
        // we pull up a few calls to this point in time because we still have the TE here
        // the execution otherwise is equivalent to vanilla order
        this.onPlayerDestroy(world, pos, state);
        onBlockHarvested(world, pos, state, player);
        if (willHarvest) {
            this.harvestBlock(world, player, pos, state, world.getTileEntity(pos), player.getHeldItemMainhand());
        }

        world.setBlockState(pos, Blocks.AIR.getDefaultState());
        // return false to prevent the above called functions to be called again
        return false;
    }

    @Override
    public BlockRenderType getRenderType(BlockState p_149645_1_) {
        return BlockRenderType.MODEL;
    }

    @Nonnull
    public abstract Class<?> getTileClass();

    protected <T extends HPBaseTileEntity> T getTileEntity(IBlockReader worldIn, BlockPos pos) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return (tileentity != null && getTileClass().isAssignableFrom(tileentity.getClass())) ? (T) tileentity: null;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader iBlockReader) {
        try {
            return (TileEntity) getTileClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        ItemStack stack = hand == Hand.MAIN_HAND ? player.getHeldItem(hand): ItemStack.EMPTY;
        HPBaseTileEntity te = (HPBaseTileEntity) worldIn.getTileEntity(pos);
        HPHorseBaseTileEntity teH = null;
        if (te == null)
            return false;
        if (te instanceof HPHorseBaseTileEntity)
            teH = (HPHorseBaseTileEntity) te;

        CreatureEntity creature = null;
        if (teH != null) {
            ArrayList<Class<? extends CreatureEntity>> clazzes = Utils.getCreatureClasses();
            search:
            for (Class<? extends Entity> clazz : clazzes) {
                for (Object entity : worldIn.getEntitiesWithinAABB(clazz, new AxisAlignedBB(-7.0D, -7.0D, -7.0D, 7.0D, 7.0D, 7.0D).offset(pos))) {
                    if (entity instanceof CreatureEntity && !(entity instanceof IMob)) {
                        CreatureEntity tmp = (CreatureEntity) entity;
                        if ((tmp.getLeashed() && tmp.getLeashHolder() == player)) {
                            creature = tmp;
                            break search;
                        }
                    }
                }
            }
        }
        if (teH != null && ((stack.getItem() instanceof LeadItem && creature != null) || creature != null)) {
            if (!teH.hasWorker()) {
                creature.clearLeashed(true, false);
                teH.setWorker(creature);
                onWorkerAttached(player, creature);
                return true;
            } else {
                return false;
            }
        } else if (!stack.isEmpty() && te.isItemValidForSlot(0, stack)) {
            ItemStack itemStack = te.getStackInSlot(0);
            boolean flag = false;

            if (itemStack.isEmpty()) {
                te.setInventorySlotContents(0, stack.copy());
                stack.setCount(stack.getCount() - te.getInventoryStackLimit(stack));
                flag = true;
            } else if (HPBaseTileEntity.canCombine(itemStack, stack)) {
                int i = Math.min(te.getInventoryStackLimit(stack), stack.getMaxStackSize()) - itemStack.getCount();
                int j = Math.min(stack.getCount(), i);
                stack.shrink(j);
                itemStack.grow(j);
                flag = j > 0;
            }

            if (flag)
                return true;
        }

        int slot = getSlot(state.getBlock().getExtendedState(state, worldIn, pos), hit);
        ItemStack result = ItemStack.EMPTY;
        if (slot > -1) {
            result = te.removeStackFromSlot(slot);
        } else if (slot > -2) {
            result = te.removeStackFromSlot(1);
            if (result.isEmpty()) {
                result = te.removeStackFromSlot(2);
                if (result.isEmpty() && stack.isEmpty() && hand != Hand.OFF_HAND) {
                    result = te.removeStackFromSlot(0);
                }
            }
            if (!result.isEmpty())
                emptiedOutput(worldIn, pos);
        }

        if (result.isEmpty()) {
            if (!stack.isEmpty())
                return false;
            if (teH != null)
                teH.setWorkerToPlayer(player);
        }

        if (!result.isEmpty())
            ItemHandlerHelper.giveItemToPlayer(player, result, EquipmentSlotType.MAINHAND.getSlotIndex());

        te.markDirty();
        return true;
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBlockHarvested(worldIn, pos, state, player);

        if (!player.abilities.isCreativeMode && !worldIn.isRemote) {
            HPBaseTileEntity te = getTileEntity(worldIn, pos);

            if (te != null) {
                InventoryHelper.dropInventoryItems(worldIn, pos, te.getInventory());
                if (te instanceof HPHorseBaseTileEntity && ((HPHorseBaseTileEntity) te).hasWorker())
                    InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY() + 1, pos.getZ(), new ItemStack(Items.LEAD));
            }
        }
    }

    @Nullable
    @Override
    public ToolType getHarvestTool(BlockState state) {
        return this.type;
    }

    /*@Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!keepInventory && !worldIn.isRemote) {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof TileEntityHPBase) {
                worldIn.updateComparatorOutputLevel(pos, this);
            }
        }
        super.breakBlock(worldIn, pos, state);
    }*/

    @Override
    public int getHarvestLevel(BlockState state) {
        return this.level;
    }
}
