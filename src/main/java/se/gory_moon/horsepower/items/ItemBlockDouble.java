package se.gory_moon.horsepower.items;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import se.gory_moon.horsepower.HorsePowerMod;
import se.gory_moon.horsepower.blocks.BlockFiller;

import javax.annotation.Nullable;

public class ItemBlockDouble extends ItemBlock {

    private Block fillerBlock;
    public ItemBlockDouble(Block block, Block filler) {
        super(block, new Item.Properties().group(HorsePowerMod.itemGroup));
        fillerBlock = filler;
    }

    @Override
    public EnumActionResult onItemUse(ItemUseContext p_195939_1_) {
        return super.onItemUse(p_195939_1_);
    }

    @Override
    public EnumActionResult tryPlace(BlockItemUseContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        EnumFacing facing = context.getFace();
        EntityPlayer player = context.getPlayer();

        BlockItemUseContext contextUp = new BlockItemUseContext(world, player, context.getItem(), pos.up(), EnumFacing.DOWN, context.getHitX(), context.getHitY(), context.getHitZ());
        if (!context.canPlace()) {
            pos = pos.offset(facing);
        }

        if (facing == EnumFacing.DOWN || !contextUp.canPlace())
            pos = pos.down();

        ItemStack itemstack = context.getItem();
        IBlockState blockState = getStateForPlacement(context);
        IBlockState blockStateUp = getStateForPlacementFiller(contextUp);

        if (!itemstack.isEmpty() && blockState != null && blockStateUp != null) {
            blockStateUp = blockStateUp.with(BlockFiller.FACING, EnumFacing.DOWN);
            if (placeBlock(context, blockState) && placeBlock(context, blockStateUp)) {
                SoundType soundtype = world.getBlockState(pos).getBlock().getSoundType(world.getBlockState(pos), world, pos, player);
                world.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                itemstack.shrink(1);
            }

            return EnumActionResult.SUCCESS;
        } else {
            return EnumActionResult.FAIL;
        }
    }

    @Nullable
    protected IBlockState getStateForPlacementFiller(BlockItemUseContext context) {
        IBlockState iblockstate = this.fillerBlock.getStateForPlacement(context);
        return iblockstate != null && this.canPlace(context, iblockstate) ? iblockstate : null;
    }
}
