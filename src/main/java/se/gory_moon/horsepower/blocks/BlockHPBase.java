package se.gory_moon.horsepower.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemLead;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import se.gory_moon.horsepower.tileentity.TileEntityGrindstone;
import se.gory_moon.horsepower.tileentity.TileEntityHPBase;
import se.gory_moon.horsepower.util.Utils;

import java.util.ArrayList;

public abstract class BlockHPBase extends Block {

    protected static boolean keepInventory = false;

    public BlockHPBase(Material materialIn) {
        super(materialIn);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
        super.onBlockHarvested(worldIn, pos, state, player);

        if (!player.capabilities.isCreativeMode && !worldIn.isRemote) {
            TileEntityHPBase te = getTileEntity(worldIn, pos);

            if (te != null) {
                InventoryHelper.dropInventoryItems(worldIn, pos, te);
                if (te.hasWorker())
                    InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY() + 1, pos.getZ(), new ItemStack(Items.LEAD));
            }
        }
    }

    private TileEntityHPBase getTileEntity(World worldIn, BlockPos pos) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity instanceof TileEntityHPBase ? (TileEntityHPBase)tileentity : null;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!keepInventory && !worldIn.isRemote) {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof TileEntityGrindstone) {
                worldIn.updateComparatorOutputLevel(pos, this);
            }
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = playerIn.getHeldItem(hand);
        TileEntityHPBase tileEntityHPBase = (TileEntityHPBase) worldIn.getTileEntity(pos);
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        EntityCreature creature = null;
        ArrayList<Class<? extends EntityCreature>> clazzes = Utils.getCreatureClasses();
        search: for (Class<? extends Entity> clazz: clazzes) {
            for (Object entity : worldIn.getEntitiesWithinAABB(clazz, new AxisAlignedBB((double)x - 7.0D, (double)y - 7.0D, (double)z - 7.0D, (double)x + 7.0D, (double)y + 7.0D, (double)z + 7.0D))){
                if (entity instanceof EntityCreature) {
                    EntityCreature tmp = (EntityCreature) entity;
                    if ((tmp.getLeashed() && tmp.getLeashedToEntity() == playerIn)) {
                        creature = tmp;
                        break search;
                    }
                }
            }
        }
        if (stack.getItem() instanceof ItemLead && creature != null || creature != null) {
            if (!tileEntityHPBase.hasWorker()) {
                creature.clearLeashed(true, false);
                tileEntityHPBase.setWorker(creature);
                return true;
            } else {
                return false;
            }
        } else if (!stack.isEmpty() && tileEntityHPBase.isItemValidForSlot(0, stack)) {
            ItemStack itemStack = tileEntityHPBase.getStackInSlot(0);
            boolean flag = false;

            if (itemStack.isEmpty()) {
                tileEntityHPBase.setInventorySlotContents(0, stack.copy());
                stack.setCount(0);
                flag = true;
            } else if (TileEntityHPBase.canCombine(itemStack, stack)) {
                int i = stack.getMaxStackSize() - itemStack.getCount();
                int j = Math.min(stack.getCount(), i);
                stack.shrink(j);
                itemStack.grow(j);
                flag = j > 0;
            }

            if (flag)
                return true;
        }

        ItemStack result = tileEntityHPBase.removeStackFromSlot(1);
        if (result.isEmpty() && stack.isEmpty() && hand != EnumHand.OFF_HAND) {
            result = tileEntityHPBase.removeStackFromSlot(0);
            BlockGrindstone.setState(false, worldIn, pos);
        }

        if (result.isEmpty()) {
            if (!stack.isEmpty())
                return false;

            tileEntityHPBase.setWorkerToPlayer(playerIn);
        }

        if (stack.isEmpty()) {
            playerIn.setHeldItem(hand, result);
        } else if (playerIn.func_191521_c(result)) {
            playerIn.dropItem(result, false);
        }

        tileEntityHPBase.markDirty();
        return true;
    }
}
