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
import se.gory_moon.horsepower.util.Localization;
import se.gory_moon.horsepower.blocks.BlockGrindstone;

import java.util.List;

public class Provider implements IWailaDataProvider {

    public static void callbackRegister(IWailaRegistrar registrar) {
        Provider provider = new Provider();

        registrar.registerBodyProvider(provider, BlockGrindstone.class);
        registrar.registerNBTProvider(provider, BlockGrindstone.class);
        //registrar.addConfig(Reference.NAME, "showItems", Localization.WAILA.SHOW_ITEMS.translate());
    }

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
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
            currenttip.add(Localization.WAILA.PROGRESS.translate(String.valueOf(progress) + "%"));
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
        te.writeToNBT(tile);
        tag.setTag("horsepower:grindstone", tile);
        return tag;
    }
}
