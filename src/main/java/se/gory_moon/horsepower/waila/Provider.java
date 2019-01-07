package se.gory_moon.horsepower.waila;
/*
import mcp.mobius.waila.api.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.blocks.*;
import se.gory_moon.horsepower.lib.Reference;
import se.gory_moon.horsepower.tileentity.*;
import se.gory_moon.horsepower.util.Localization;

import java.util.List;

public class Provider implements IWailaDataProvider {

    public static void callbackRegister(IWailaRegistrar registrar) {
        Provider provider = new Provider();

        //registrar.registerStackProvider(provider, BlockFiller.class);
        registrar.registerBodyProvider(provider, BlockGrindstone.class);
        registrar.registerBodyProvider(provider, BlockHandGrindstone.class);
        registrar.registerBodyProvider(provider, BlockHPChoppingBase.class);
        registrar.registerBodyProvider(provider, BlockPress.class);
        registrar.registerBodyProvider(provider, BlockFiller.class);
        registrar.registerNBTProvider(provider, BlockGrindstone.class);
        registrar.registerNBTProvider(provider, BlockHandGrindstone.class);
        registrar.registerNBTProvider(provider, BlockChopper.class);
        registrar.registerNBTProvider(provider, BlockChoppingBlock.class);
        registrar.registerNBTProvider(provider, BlockPress.class);
        registrar.registerNBTProvider(provider, BlockFiller.class);
        registrar.addConfig(Reference.NAME, "horsepower:showItems", Localization.WAILA.SHOW_ITEMS.translate());
    }

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (accessor.getBlock().equals(ModBlocks.BLOCK_CHOPPER_FILLER))
            return accessor.getBlock().getPickBlock(accessor.getBlockState(), accessor.getMOP(), accessor.getWorld(), accessor.getPosition(), accessor.getPlayer());
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

            double totalWindup = Configs.general.pointsForWindup > 0 ? Configs.general.pointsForWindup : 1;
            double windup = (double) nbt.getInteger("currentWindup");
            double current = (double) nbt.getInteger("chopTime");
            double total = (double) nbt.getInteger("totalChopTime");
            double progressWindup = Math.round(((windup / totalWindup) * 100D) * 100D) / 100D;
            double progressChopping = Math.round(((current / total) * 100D) * 100D) / 100D;

            if (accessor.getTileEntity() instanceof TileEntityChopper || accessor.getTileEntity() instanceof TileEntityFiller)
                currenttip.add(Localization.WAILA.WINDUP_PROGRESS.translate(String.valueOf(progressWindup) + "%"));
            if (total > 1 || accessor.getTileEntity() instanceof TileEntityManualChopper) {
                currenttip.add(Localization.WAILA.CHOPPING_PROGRESS.translate(String.valueOf(progressChopping) + "%"));
            }
        } else if (nbt.hasKey("horsepower:press")) {
            nbt = nbt.getCompoundTag("horsepower:press");
            double current = (double) nbt.getInteger("currentPressStatus");
            double total = Configs.general.pointsForPress > 0 ? Configs.general.pointsForPress: 1;
            double progress = Math.round(((current / total) * 100D) * 100D) / 100D;
            currenttip.add(Localization.WAILA.PRESS_PROGRESS.translate(String.valueOf(progress) + "%"));
        }

        if (config.getConfig("horsepower:showItems") && (accessor.getTileEntity() instanceof TileEntityHPBase || accessor.getTileEntity() instanceof TileEntityFiller) && accessor.getPlayer().isSneaking()) {
            TileEntity te = accessor.getTileEntity();
            if (te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP)) {
                IItemHandler itemHandler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
                if (itemHandler != null) {
                    {
                        final ItemStack stack = itemHandler.getStackInSlot(0);
                        final String name = String.valueOf(stack.getItem().getRegistryName().toString());
                        if (!stack.isEmpty())
                            currenttip.add(SpecialChars.getRenderString("waila.stack", "1", name, String.valueOf(stack.getCount()), String.valueOf(stack.getItemDamage()), String.valueOf(stack.serializeNBT().toString())) + TextFormatting.WHITE + stack.getDisplayName());
                    }
                    {
                        final ItemStack stack = itemHandler.getStackInSlot(1);
                        final String name = String.valueOf(stack.getItem().getRegistryName().toString());
                        if (!stack.isEmpty())
                            currenttip.add(SpecialChars.getRenderString("waila.stack", "1", name, String.valueOf(stack.getCount()), String.valueOf(stack.getItemDamage()), String.valueOf(stack.serializeNBT().toString())) + TextFormatting.WHITE + stack.getDisplayName());
                    }
                    {
                        final ItemStack stack = itemHandler.getStackInSlot(2);
                        final String name = String.valueOf(stack.getItem().getRegistryName().toString());
                        if (!stack.isEmpty())
                            currenttip.add(SpecialChars.getRenderString("waila.stack", "1", name, String.valueOf(stack.getCount()), String.valueOf(stack.getItemDamage()), String.valueOf(stack.serializeNBT().toString())) + TextFormatting.WHITE + stack.getDisplayName());
                    }
                }
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
        if (te instanceof TileEntityFiller)
            te = ((TileEntityFiller) te).getFilledTileEntity();
        if (te != null)
            te.writeToNBT(tile);
        if (te instanceof TileEntityGrindstone || te instanceof TileEntityHandGrindstone)
            tag.setTag("horsepower:grindstone", tile);
        else if (te instanceof TileEntityChopper || te instanceof TileEntityManualChopper)
            tag.setTag("horsepower:chopper", tile);
        else if (te instanceof TileEntityPress)
            tag.setTag("horsepower:press", tile);
        return tag;
    }
}
*/