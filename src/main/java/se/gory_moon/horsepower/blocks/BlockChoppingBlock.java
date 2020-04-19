package se.gory_moon.horsepower.blocks;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoAccessor;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.apiimpl.styles.ProgressStyle;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.block.Block.Properties;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
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
import se.gory_moon.horsepower.tileentity.TileEntityManualChopper;
import se.gory_moon.horsepower.util.Constants;
import se.gory_moon.horsepower.util.Localization;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

//@Optional.Interface(iface = "mcjty.theoneprobe.api.IProbeInfoAccessor", modid = "theoneprobe")
public class BlockChoppingBlock extends BlockHPChoppingBase {  //TODO restore TOP implements IProbeInfoAccessor

    private static final AxisAlignedBB COLLISION_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 6D/16D, 1.0D);

    public BlockChoppingBlock(Properties properties) {
        super(properties.hardnessAndResistance(2.0F, 5F).sound(SoundType.WOOD));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        // TODO Auto-generated method stub
        return super.getShape(state, worldIn, pos, context);
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

        TileEntityManualChopper te = getTileEntity(worldIn, pos);

        if (te != null) {
            ItemStack held = player.getHeldItem(Hand.MAIN_HAND);
            if (!held.isEmpty() && ((held.getItem().getHarvestLevel(held, ToolType.AXE, player, null) > -1) || isItemWhitelisted(held))) {
                if (te.chop(player, held)) {
                    player.addExhaustion((float) 0.1); //FIXME config Configs.general.choppingblockExhaustion
                    if (Boolean.TRUE) //FIXME config Configs.general.shouldDamageAxe
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
        return TileEntityManualChopper.class;
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


    // The One Probe Integration //TODO TOP integration
//    @Optional.Method(modid = "theoneprobe")
//    @Override
//    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, BlockState blockState, IProbeHitData data) {
//        TileEntityManualChopper tileEntity = getTileEntity(world, data.getPos());
//        if (tileEntity != null) {
//            probeInfo.progress((long) ((((double) tileEntity.getField(1)) / ((double) tileEntity.getField(0))) * 100L), 100L, new ProgressStyle().prefix(Localization.TOP.CHOPPING_PROGRESS.translate() + " ").suffix("%"));
//        }
//    }
}
