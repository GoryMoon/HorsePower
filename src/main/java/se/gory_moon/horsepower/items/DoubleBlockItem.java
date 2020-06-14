package se.gory_moon.horsepower.items;

import javax.annotation.Nullable;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import se.gory_moon.horsepower.blocks.FillerBlock;

public class DoubleBlockItem extends BlockItem {

    private Block fillerBlock;

    public DoubleBlockItem(Block block, Block filler, Properties properties) {
        super(block, properties);
        fillerBlock = filler;
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        return super.onItemUse(context);
    }

    
    
    @Override
    public ActionResultType tryPlace(BlockItemUseContext context) {
        World world = context.getWorld();
        
        Direction direction = context.getFace();
        BlockPos contextPos = context.getPos();
        
        BlockPos topPos = Direction.DOWN.equals(direction) ? contextPos : contextPos.up(1);
        BlockPos bottomPos = Direction.DOWN.equals(direction) ? contextPos.down(1) : contextPos;
        BlockItemUseContext topContext = BlockItemUseContext.func_221536_a(context, topPos, Direction.DOWN.equals(direction) ? Direction.UP : direction);
        BlockItemUseContext bottomContext = BlockItemUseContext.func_221536_a(context, bottomPos, Direction.DOWN.equals(direction) ? Direction.UP : direction);
        
        ItemStack itemstack = context.getItem();
        
        if(itemstack.isEmpty() || !world.getBlockState(topPos).isReplaceable(topContext) || !world.getBlockState(bottomPos).isReplaceable(bottomContext))
            return ActionResultType.FAIL;
        
        BlockState topBlockState = getStateForPlacementFiller(topContext);
        BlockState bottomBlockState = getStateForPlacement(bottomContext);

        if (bottomBlockState != null && topBlockState != null) {
            topBlockState = topBlockState.with(FillerBlock.FACING, Direction.DOWN);
            if (placeBlock(bottomContext, bottomBlockState) && placeBlock(topContext, topBlockState)) {
                PlayerEntity player = context.getPlayer();
                BlockState blockstate1 = world.getBlockState(bottomPos);
                Block block = blockstate1.getBlock();
                if (block == bottomBlockState.getBlock()) {
                    this.onBlockPlaced(bottomPos, world, player, itemstack, blockstate1);
                    block.onBlockPlacedBy(world, bottomPos, blockstate1, player, itemstack);
                    if (player instanceof ServerPlayerEntity) {
                        CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity) player, bottomPos, itemstack);
                    }
                }
                SoundType soundtype = world.getBlockState(bottomPos).getBlock().getSoundType(world.getBlockState(bottomPos), world, bottomPos, player);
                world.playSound(player, bottomPos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
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
        return iblockstate != null && this.canPlace(context, iblockstate) ? iblockstate: null;
    }
}
