package se.gory_moon.horsepower.items;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemBlockDouble extends ItemBlock {

    private static Block fillerBlock = null;
    public ItemBlockDouble(Block block, Block filler) {
        super(block);
        fillerBlock = filler;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        Block block = iblockstate.getBlock();

        if (!block.isReplaceable(worldIn, pos)) {
            pos = pos.offset(facing);
        }

        if (facing == EnumFacing.DOWN || (!worldIn.getBlockState(pos.up()).getBlock().isReplaceable(worldIn, pos.up())))
            pos = pos.down();

        BlockPos posUp = pos.up();
        ItemStack itemstack = player.getHeldItem(hand);

        if (!itemstack.isEmpty()
                && player.canPlayerEdit(pos, facing, itemstack) && worldIn.mayPlace(this.block, pos, false, facing, null)
                && player.canPlayerEdit(posUp, facing, itemstack) && worldIn.mayPlace(fillerBlock, pos, false, facing, null))
        {
            int i = this.getMetadata(itemstack.getMetadata());
            IBlockState blockState = this.block.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, i, player, hand);
            IBlockState blockStateUp = fillerBlock.getStateForPlacement(worldIn, posUp, facing, hitX, hitY, hitZ, EnumFacing.DOWN.getIndex(), player, hand);

            if (placeBlockAt(itemstack, player, worldIn, pos, facing, hitX, hitY, hitZ, blockState)) {
                placeBlockAt(itemstack, player, worldIn, posUp, facing, hitX, hitY, hitZ, blockStateUp);
                SoundType soundtype = worldIn.getBlockState(pos).getBlock().getSoundType(worldIn.getBlockState(pos), worldIn, pos, player);
                worldIn.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                itemstack.shrink(1);
            }

            return EnumActionResult.SUCCESS;
        } else {
            return EnumActionResult.FAIL;
        }
    }
}
