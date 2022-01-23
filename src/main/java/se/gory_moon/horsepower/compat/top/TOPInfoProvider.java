package se.gory_moon.horsepower.compat.top;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.apiimpl.ProbeHitData;
import mcjty.theoneprobe.apiimpl.styles.ProgressStyle;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.blocks.ChopperBlock;
import se.gory_moon.horsepower.blocks.FillerBlock;
import se.gory_moon.horsepower.blocks.ManualChopperBlock;
import se.gory_moon.horsepower.blocks.ManualMillstoneBlock;
import se.gory_moon.horsepower.blocks.MillstoneBlock;
import se.gory_moon.horsepower.blocks.PressBlock;
import se.gory_moon.horsepower.tileentity.ChopperTileEntity;
import se.gory_moon.horsepower.tileentity.ManualChopperTileEntity;
import se.gory_moon.horsepower.tileentity.ManualMillstoneTileEntity;
import se.gory_moon.horsepower.tileentity.MillstoneTileEntity;
import se.gory_moon.horsepower.tileentity.PressTileEntity;
import se.gory_moon.horsepower.util.Localization;

public class TOPInfoProvider implements IProbeInfoProvider {
    
    @Override
    public String getID() {
        return "horsepower:default";
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
        if (!addInfo(mode, probeInfo, player, world, blockState, data) && blockState.getBlock() instanceof FillerBlock) {
            BlockPos pos = data.getPos().offset(blockState.get(DirectionalBlock.FACING));
            BlockState state = world.getBlockState(pos);
            if (FillerBlock.validateFilled(world, state, data.getPos())) {
                addInfo(mode, probeInfo, player, world, state, new ProbeHitData(pos, data.getHitVec(), data.getSideHit(), data.getPickBlock()));
            }
        }
    }

    private static boolean addInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
        boolean added = false;
        Block block = blockState.getBlock();
        if (block instanceof ManualMillstoneBlock) {
            addBlockInfo(probeInfo, (ManualMillstoneTileEntity) world.getTileEntity(data.getPos()));
            added = true;
        } else if (block instanceof PressBlock) {
            addBlockInfo(probeInfo, (PressTileEntity) world.getTileEntity(data.getPos()));
            added = true;
        } else if (block instanceof MillstoneBlock) {
            addBlockInfo(probeInfo, (MillstoneTileEntity) world.getTileEntity(data.getPos()));
            added = true;
        } else if (block instanceof ManualChopperBlock) {
            addBlockInfo(probeInfo, player, (ManualChopperTileEntity) world.getTileEntity(data.getPos()));
            added = true;
        } else if (block instanceof ChopperBlock) {
            addBlockInfo(probeInfo, (ChopperTileEntity)world.getTileEntity(data.getPos()));
            added = true;
        }
        return added;
    }

    private static void addBlockInfo(IProbeInfo probeInfo, ChopperTileEntity tileEntity) {
        if (tileEntity != null) {
            double totalWindup = Configs.SERVER.pointsForWindup.get().intValue() > 0 ? Configs.SERVER.pointsForWindup.get().intValue() : 1;
            probeInfo.progress((long) (((tileEntity.getCurrentWindup()) / totalWindup) * 100L), 100L, new ProgressStyle().prefix(Localization.TOP.WINDUP_PROGRESS.translate() + " ").suffix("%"));
            if (tileEntity.getTotalItemChopTime() > 1)
                probeInfo.progress((long) ((((double) tileEntity.getCurrentItemChopTime()) / ((double) tileEntity.getTotalItemChopTime())) * 100L), 100L, new ProgressStyle().prefix(Localization.TOP.CHOPPING_PROGRESS.translate() + " ").suffix("%"));
        }
    }

    private static void addBlockInfo(IProbeInfo probeInfo, PlayerEntity player, ManualChopperTileEntity te) {
        if (te != null) {
            probeInfo.progress(te.getCurrentProgress(), 100L, new ProgressStyle().prefix(Localization.TOP.CHOPPING_PROGRESS.translate() + " ").suffix("%"));
            if(player.isSneaking() && Configs.CLIENT.showManualChoppingAxeInfo.get().booleanValue()) {
                ItemStack heldItem = player.getHeldItem(Hand.MAIN_HAND);
                probeInfo.text(ITextComponent.getTextComponentOrEmpty(Localization.INFO.MANUAL_CHOPPING_AXES_BASE_AMOUNT.translate() + ManualChopperTileEntity.getBaseAmount(heldItem, player) + "%"));
                probeInfo.text(ITextComponent.getTextComponentOrEmpty(Localization.INFO.MANUAL_CHOPPING_AXES_ADDITIONAL_CHANCE.translate() + ManualChopperTileEntity.getChance(heldItem, player) + "%"));
            }
        }
    }

    private static void addBlockInfo(IProbeInfo probeInfo, MillstoneTileEntity te) {
        if (te != null) {
            double total = te.getTotalItemMillTime();
            double current = te.getCurrentItemMillTime();
            probeInfo.progress((long) ((current / total) * 100L), 100L, new ProgressStyle().prefix(Localization.TOP.MILLSTONE_PROGRESS.translate() + " ").suffix("%"));
        }
    }

    private static void addBlockInfo(IProbeInfo probeInfo, PressTileEntity te) {
        if (te != null) {
            double current = te.getCurrentPressStatus();
            probeInfo.progress((long) ((current / (Configs.SERVER.pointsPerPress.get() > 0 ? Configs.SERVER.pointsPerPress.get(): 1)) * 100L), 100L, new ProgressStyle().prefix(Localization.TOP.PRESS_PROGRESS.translate() + " ").suffix("%"));
        }
    }

    private static void addBlockInfo(IProbeInfo probeInfo, ManualMillstoneTileEntity te) {
        if (te != null) {
            double total = te.getTotalItemMillTime();
            double current = te.getCurrentItemMillTime();
            probeInfo.progress((long) ((current / total) * 100L), 100L, new ProgressStyle().prefix(Localization.TOP.MILLSTONE_PROGRESS.translate() + " ").suffix("%"));
        }
    }
}
