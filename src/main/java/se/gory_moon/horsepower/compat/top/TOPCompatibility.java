package se.gory_moon.horsepower.compat.top;

import com.google.common.base.Function;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.apiimpl.ProbeHitData;
import mcjty.theoneprobe.apiimpl.styles.ProgressStyle;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.InterModComms;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.blocks.ChopperBlock;
import se.gory_moon.horsepower.blocks.FillerBlock;
import se.gory_moon.horsepower.blocks.ManualChopperBlock;
import se.gory_moon.horsepower.blocks.ManualMillstoneBlock;
import se.gory_moon.horsepower.blocks.MillstoneBlock;
import se.gory_moon.horsepower.blocks.PressBlock;
import se.gory_moon.horsepower.tileentity.ChopperTileEntity;
import se.gory_moon.horsepower.tileentity.HPBaseTileEntity;
import se.gory_moon.horsepower.tileentity.ManualChopperTileEntity;
import se.gory_moon.horsepower.tileentity.ManualMillstoneTileEntity;
import se.gory_moon.horsepower.tileentity.MillstoneTileEntity;
import se.gory_moon.horsepower.tileentity.PressTileEntity;
import se.gory_moon.horsepower.util.Localization;

import javax.annotation.Nullable;

public class TOPCompatibility {

    public static void register() {
        InterModComms.sendTo("theoneprobe", "getTheOneProbe", GetTheOneProbe::new);
    }

    public static class GetTheOneProbe implements Function<ITheOneProbe, Void> {

        @Nullable
        @Override
        public Void apply(@Nullable ITheOneProbe probe) {

            probe.registerProvider(new IProbeInfoProvider() {
                @Override
                public String getID() {
                    return "horsepower:default";
                }

                @Override
                public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
                    if (!addInfo(mode, probeInfo, player, world, blockState, data) && blockState.getBlock() instanceof FillerBlock) {
                        BlockPos pos = data.getPos().offset(blockState.get(FillerBlock.FACING));
                        BlockState state = world.getBlockState(pos);
                        if (FillerBlock.validateFilled(world, state, data.getPos())) {
                            addInfo(mode, probeInfo, player, world, state, new ProbeHitData(pos, data.getHitVec(), data.getSideHit(), data.getPickBlock()));
                        }
                    }
                }

                private boolean addInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
                    boolean added = false;
                    Block block = blockState.getBlock();
                    if (block instanceof ManualMillstoneBlock) {
                        ManualMillstoneTileEntity te = (ManualMillstoneTileEntity) world.getTileEntity(data.getPos());
                        if (te != null) {
                            double total = te.getTotalItemMillTime();
                            double current = te.getCurrentItemMillTime();
                            probeInfo.progress((long) ((current / total) * 100L), 100L, new ProgressStyle().prefix(Localization.TOP.MILLSTONE_PROGRESS.translate() + " ").suffix("%"));
                        }
                        added = true;
                    } else if (block instanceof PressBlock) {
                        PressTileEntity te = (PressTileEntity) world.getTileEntity(data.getPos());
                        if (te != null) {
                            double current = te.getCurrentPressStatus();
                            probeInfo.progress((long) ((current / (Configs.SERVER.pointsPerPress.get() > 0 ? Configs.SERVER.pointsPerPress.get(): 1)) * 100L), 100L, new ProgressStyle().prefix(Localization.TOP.PRESS_PROGRESS.translate() + " ").suffix("%"));
                        }
                        added = true;
                    } else if (block instanceof MillstoneBlock) {
                        MillstoneTileEntity te = (MillstoneTileEntity) world.getTileEntity(data.getPos());
                        if (te != null) {
                            double total = te.getTotalItemMillTime();
                            double current = te.getCurrentItemMillTime();
                            probeInfo.progress((long) ((current / total) * 100L), 100L, new ProgressStyle().prefix(Localization.TOP.MILLSTONE_PROGRESS.translate() + " ").suffix("%"));
                        }
                        added = true;
                    } else if (block instanceof ManualChopperBlock) {
                        TileEntity te =  world.getTileEntity(data.getPos());
                        if (te instanceof ManualChopperTileEntity) {
                            probeInfo.progress(((ManualChopperTileEntity) te).getCurrentProgress(), 100L, new ProgressStyle().prefix(Localization.TOP.CHOPPING_PROGRESS.translate() + " ").suffix("%"));
                            if(player.isSneaking() && Configs.CLIENT.showManualChoppingAxeInfo.get().booleanValue())
                            {
                                ItemStack heldItem = player.getHeldItem(Hand.MAIN_HAND);
                                probeInfo.text(Localization.INFO.MANUAL_CHOPPING_AXES_BASE_AMOUNT.translate()+ ManualChopperTileEntity.getBaseAmount(heldItem, player) +"%");
                                probeInfo.text(Localization.INFO.MANUAL_CHOPPING_AXES_ADDITIONAL_CHANCE.translate()+ ManualChopperTileEntity.getChance(heldItem, player) +"%");
                            }
                        }
                        added = true;
                    } else if (block instanceof ChopperBlock) {
                        TileEntity te =  world.getTileEntity(data.getPos());
                        if (te instanceof ChopperTileEntity) {
                            ChopperTileEntity tileEntity = (ChopperTileEntity)te;
                            double totalWindup = Configs.SERVER.pointsForWindup.get().intValue() > 0 ? Configs.SERVER.pointsForWindup.get().intValue() : 1;
                            probeInfo.progress((long) (((tileEntity.getCurrentWindup()) / totalWindup) * 100L), 100L, new ProgressStyle().prefix(Localization.TOP.WINDUP_PROGRESS.translate() + " ").suffix("%"));
                            if (tileEntity.getTotalItemChopTime() > 1)
                                probeInfo.progress((long) ((((double) tileEntity.getCurrentItemChopTime()) / ((double) tileEntity.getTotalItemChopTime())) * 100L), 100L, new ProgressStyle().prefix(Localization.TOP.CHOPPING_PROGRESS.translate() + " ").suffix("%"));
                        }
                        added = true;
                    }
                    return added;
                }
            });

            return null;
        }
    }
}
