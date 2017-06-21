package se.gory_moon.horsepower.waila;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.blocks.*;
import se.gory_moon.horsepower.tileentity.TileEntityChopper;
import se.gory_moon.horsepower.tileentity.TileEntityFiller;
import se.gory_moon.horsepower.tileentity.TileEntityGrindstone;
import se.gory_moon.horsepower.tileentity.TileEntityHandGrindstone;
import se.gory_moon.horsepower.util.Localization;

import java.util.List;

public class Provider implements IWailaDataProvider {

    public static void callbackRegister(IWailaRegistrar registrar) {
        Provider provider = new Provider();

        registrar.registerStackProvider(provider, BlockFiller.class);
        registrar.registerBodyProvider(provider, BlockGrindstone.class);
        registrar.registerBodyProvider(provider, BlockHandGrindstone.class);
        registrar.registerBodyProvider(provider, BlockChopper.class);
        registrar.registerBodyProvider(provider, BlockFiller.class);
        registrar.registerNBTProvider(provider, BlockGrindstone.class);
        registrar.registerNBTProvider(provider, BlockHandGrindstone.class);
        registrar.registerNBTProvider(provider, BlockChopper.class);
        registrar.registerNBTProvider(provider, BlockFiller.class);
        //registrar.addConfig(Reference.NAME, "showItems", Localization.WAILA.SHOW_ITEMS.translate());
    }

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (accessor.getBlock().equals(ModBlocks.BLOCK_CHOPPER_FILLER))
            return new ItemStack(ModBlocks.BLOCK_CHOPPER, 1);
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        NBTTagCompound nbt = accessor.getNBTData();
        if (nbt.hasKey("horsepower:grindstone", 10)) {
            nbt = nbt.getCompoundTag("horsepower:grindstone");

            double total = (double) nbt.getInteger("totalMillTime");
            double current = (double) nbt.getInteger("millTime");
            double progress = Math.round(((current / total) * 100D) * 100D) / 100D;
            currenttip.add(Localization.WAILA.GRINDSTONE_PROGRESS.translate(String.valueOf(progress) + "%"));
        } else if (nbt.hasKey("horsepower:chopper", 10)) {
            nbt = nbt.getCompoundTag("horsepower:chopper");

            double totalWindup = Configs.pointsForWindup > 0 ? Configs.pointsForWindup: 1;
            double windup = (double) nbt.getInteger("currentWindup");
            double current = (double) nbt.getInteger("chopTime");
            double total = (double) nbt.getInteger("totalChopTime");
            double progressWindup = Math.round(((windup / totalWindup) * 100D) * 100D) / 100D;
            double progressChopping = Math.round(((current / total) * 100D) * 100D) / 100D;

            currenttip.add(Localization.WAILA.WINDUP_PROGRESS.translate(String.valueOf(progressWindup) + "%"));
            if (total > 1) {
                currenttip.add(Localization.WAILA.CHOPPING_PROGRESS.translate(String.valueOf(progressChopping) + "%"));
            }
        }
        return currenttip;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
        NBTTagCompound tile = new NBTTagCompound();
        if (te instanceof TileEntityFiller) te = ((TileEntityFiller) te).getFilledTileEntity();
        te.writeToNBT(tile);
        if (te instanceof TileEntityGrindstone || te instanceof TileEntityHandGrindstone) tag.setTag("horsepower:grindstone", tile);
        else if (te instanceof TileEntityChopper) tag.setTag("horsepower:chopper", tile);
        return tag;
    }
}
