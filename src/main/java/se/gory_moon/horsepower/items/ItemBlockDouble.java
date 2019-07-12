package se.gory_moon.horsepower.items;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import se.gory_moon.horsepower.HorsePowerMod;
import se.gory_moon.horsepower.blocks.BlockFiller;

import javax.annotation.Nullable;

public class ItemBlockDouble extends BlockItem {

    private Block fillerBlock;
    public ItemBlockDouble(Block block, Block filler) {
        super(block, new Item.Properties().group(HorsePowerMod.itemGroup));
        fillerBlock = filler;
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        return super.onItemUse(context);
    }

    @Override
    public ActionResultType tryPlace(BlockItemUseContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        Direction direction = context.getFace();
        PlayerEntity player = context.getPlayer();

        BlockItemUseContext contextUp = BlockItemUseContext.func_221536_a(context, pos.up(1), Direction.DOWN);
        if (!context.canPlace()) {
            pos = pos.offset(direction);
        }

        if (direction == Direction.DOWN || !contextUp.canPlace())
            pos = pos.down();

        ItemStack itemstack = context.getItem();
        BlockState blockState = getStateForPlacement(context);
        BlockState blockStateUp = getStateForPlacementFiller(contextUp);

        if (!itemstack.isEmpty() && blockState != null && blockStateUp != null) {
            blockStateUp = blockStateUp.with(BlockFiller.FACING, Direction.DOWN);
            if (placeBlock(context, blockState) && placeBlock(contextUp, blockStateUp)) {

                BlockState blockstate1 = world.getBlockState(pos);
                Block block = blockstate1.getBlock();
                if (block == blockState.getBlock()) {
                    this.onBlockPlaced(pos, world, player, itemstack, blockstate1);
                    block.onBlockPlacedBy(world, pos, blockstate1, player, itemstack);
                    if (player instanceof ServerPlayerEntity) {
                        CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity)player, pos, itemstack);
                    }
                }
                SoundType soundtype = world.getBlockState(pos).getBlock().getSoundType(world.getBlockState(pos), world, pos, player);
                world.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                itemstack.shrink(1);
            }

            return ActionResultType.SUCCESS;
        } else {
            return ActionResultType.FAIL;
        }
    }

    @Nullable
    protected BlockState getStateForPlacementFiller(BlockItemUseContext context) {
        BlockState iblockstate = this.fillerBlock.getStateForPlacement(context);
        return iblockstate != null && this.canPlace(context, iblockstate) ? iblockstate : null;
    }
}
