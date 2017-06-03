package se.gorymoon.horsepower.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemLead;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import se.gorymoon.horsepower.tileentity.TileEntityMill;

import javax.annotation.Nullable;

//TODO add model and texture
public class BlockMill extends Block {

    public BlockMill() {
        super(Material.ROCK);
        setHardness(0.2F);
        setResistance(5F);
        setSoundType(SoundType.STONE);
        setRegistryName("mill");
        setUnlocalizedName("mill");
        setCreativeTab(CreativeTabs.REDSTONE);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityMill();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = playerIn.getHeldItem(hand);
        TileEntityMill tileEntityMill = (TileEntityMill) worldIn.getTileEntity(pos);
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        if (stack.getItem() instanceof ItemLead) {
            for (AbstractHorse abstractHorse : worldIn.getEntitiesWithinAABB(AbstractHorse.class, new AxisAlignedBB((double)x - 7.0D, (double)y - 7.0D, (double)z - 7.0D, (double)x + 7.0D, (double)y + 7.0D, (double)z + 7.0D))){
                if (abstractHorse.getLeashed() && abstractHorse.getLeashedToEntity() == playerIn) {
                    if (!tileEntityMill.hasWorker()) {
                        abstractHorse.clearLeashed(true, false);
                        tileEntityMill.setWorker(abstractHorse);
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        } else if (!stack.isEmpty() && tileEntityMill.isItemValidForSlot(0, stack)) {
            ItemStack itemStack = tileEntityMill.getStackInSlot(0);
            boolean flag = false;

            if (itemStack.isEmpty()) {
                tileEntityMill.setInventorySlotContents(0, stack.copy());
                stack.setCount(0);
                flag = true;
            } else if (TileEntityMill.canCombine(itemStack, stack)) {
                int i = stack.getMaxStackSize() - itemStack.getCount();
                int j = Math.min(stack.getCount(), i);
                stack.shrink(j);
                itemStack.grow(j);
                flag = j > 0;
            }

            if (flag)
                return true;
        }

        ItemStack result = tileEntityMill.removeStackFromSlot(1);
        if (result.isEmpty() && stack.isEmpty() && hand != EnumHand.OFF_HAND) {
            result = tileEntityMill.removeStackFromSlot(0);
        }

        if (result.isEmpty())
            return false;

        if (stack.isEmpty()) {
            playerIn.setHeldItem(hand, result);
        } else if (playerIn.func_191521_c(result)) {
            playerIn.dropItem(result, false);
        }

        tileEntityMill.markDirty();
        return true;
    }


}
