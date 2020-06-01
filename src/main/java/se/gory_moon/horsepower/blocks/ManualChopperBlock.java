package se.gory_moon.horsepower.blocks;

import net.minecraft.block.SoundType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.util.FakePlayer;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.HPEventHandler;
import se.gory_moon.horsepower.tileentity.ManualChopperTileEntity;
import javax.annotation.Nonnull;

public class ManualChopperBlock extends HPChopperBaseBlock {

    private static final VoxelShape SHAPE = Block.makeCuboidShape(0, 0, 0, 16, 6, 16);
    
    public ManualChopperBlock(Properties properties) {
        super(properties.hardnessAndResistance(2.0F, 5F).sound(SoundType.WOOD));
        setHarvestLevel(ToolType.AXE, 0);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }
    
    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand,
            BlockRayTraceResult hit) {
        return player instanceof FakePlayer || player == null ||  super.onBlockActivated(state, worldIn, pos, player, hand, hit);
    }
    
    @Override
    public void onBlockClicked(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
        if (player instanceof FakePlayer || player == null)
            return;

        ManualChopperTileEntity te = getTileEntity(worldIn, pos);

        if (te != null) {
            ItemStack held = player.getHeldItem(Hand.MAIN_HAND);
            if (!held.isEmpty() && ((held.getItem().getHarvestLevel(held, ToolType.AXE, player, null) > -1) || isItemWhitelisted(held))) {
                if (te.chop(player, held)) {
                    player.addExhaustion(Configs.SERVER.choppingExhaustion.get().floatValue());
                    if (Configs.SERVER.shouldDamageAxe.get().booleanValue() && held.isDamageable())
                        held.damageItem(1, player, p -> p.sendBreakAnimation(Hand.MAIN_HAND));
                }
            }
        }
        super.onBlockClicked(state, worldIn, pos, player);
    }

    private boolean isItemWhitelisted(ItemStack stack) {
        for (ItemStack itemStack: HPEventHandler.choppingAxes.keySet()) {
            if (ItemStack.areItemsEqualIgnoreDurability(itemStack, stack))
                return true;
        }
        return false;
    }

    @Override
    public void emptiedOutput(World world, BlockPos pos) {}

    @Override
    @Nonnull
    public Class<?> getTileClass() {
        return ManualChopperTileEntity.class;
    }

//    @Override
//    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
//        if (Configs.general.enableHandChoppingBlock)
//            super.getSubBlocks(tab, list);
//    }

//    @Override
//    protected BlockStateContainer createBlockState() {
//        return new ExtendedBlockState(this, new IProperty[] {}, new IUnlistedProperty[]{SIDE_TEXTURE, TOP_TEXTURE});
//    }
}
