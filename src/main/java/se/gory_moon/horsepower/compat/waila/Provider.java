package se.gory_moon.horsepower.compat.waila;
//TODO waila support
//import java.util.List;
//
//import mcp.mobius.waila.api.IDataAccessor;
//import mcp.mobius.waila.api.IPluginConfig;
//import mcp.mobius.waila.api.IRegistrar;
//import mcp.mobius.waila.api.IWailaPlugin;
//import mcp.mobius.waila.api.RenderableTextComponent;
//import mcp.mobius.waila.api.TooltipPosition;
//import mcp.mobius.waila.api.WailaPlugin;
//import net.minecraft.item.ItemStack;
//import net.minecraft.nbt.CompoundNBT;
//import net.minecraft.tileentity.TileEntity;
//import net.minecraft.util.ResourceLocation;
//import net.minecraft.util.text.ITextComponent;
//import net.minecraftforge.items.CapabilityItemHandler;
//import se.gory_moon.horsepower.blocks.FillerBlock;
//import se.gory_moon.horsepower.blocks.ManualMillstoneBlock;
//import se.gory_moon.horsepower.blocks.MillstoneBlock;
//import se.gory_moon.horsepower.blocks.PressBlock;
//import se.gory_moon.horsepower.tileentity.FillerTileEntity;
//import se.gory_moon.horsepower.tileentity.HPBaseTileEntity;
//import se.gory_moon.horsepower.util.Constants;
//
//@WailaPlugin
//public class Provider implements IWailaPlugin {
//
//    static final ResourceLocation RENDER_ITEM = new ResourceLocation("item");
//    static final ResourceLocation RENDER_SPACER = new ResourceLocation("spacer");
//    static final ResourceLocation RENDER_FURNACE_PROGRESS = new ResourceLocation("furnace_progress");
//
//    static final ResourceLocation CONFIG_SHOW_ITEMS = new ResourceLocation(Constants.MOD_ID, "show_items");
//
//    public static void showItems(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config, int prog) {
//        if (config.get(CONFIG_SHOW_ITEMS) && (accessor.getTileEntity() instanceof HPBaseTileEntity || accessor.getTileEntity() instanceof FillerTileEntity)) {
//            TileEntity te = accessor.getTileEntity();
//            te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(itemHandler -> {
//                final ItemStack stack = itemHandler.getStackInSlot(0);
//                final ItemStack output = itemHandler.getStackInSlot(1);
//                final ItemStack bonus = itemHandler.getStackInSlot(2);
//
//                if (!stack.isEmpty()) {
//                    CompoundNBT tag = new CompoundNBT();
//                    tag.putString("id", stack.getItem().getRegistryName().toString());
//                    tag.putInt("count", stack.getCount());
//                    if (stack.hasTag())
//                        tag.putString("nbt", stack.getTag().toString());
//
//                    CompoundNBT progress = new CompoundNBT();
//                    progress.putInt("progress", prog);
//                    progress.putInt("total", 100);
//
//                    tooltip.add(new RenderableTextComponent(
//                            getRenderable(stack),
//                            new RenderableTextComponent(RENDER_FURNACE_PROGRESS, progress),
//                            getRenderable(output),
//                            getRenderable(bonus)
//                    ));
//
//                }
//            });
//        }
//    }
//
//    private static RenderableTextComponent getRenderable(ItemStack stack) {
//        if (!stack.isEmpty()) {
//            CompoundNBT tag = new CompoundNBT();
//            tag.putString("id", stack.getItem().getRegistryName().toString());
//            tag.putInt("count", stack.getCount());
//            if (stack.hasTag())
//                tag.putString("nbt", stack.getTag().toString());
//            return new RenderableTextComponent(RENDER_ITEM, tag);
//        } else {
//            CompoundNBT spacerTag = new CompoundNBT();
//            spacerTag.putInt("width", 18);
//            return new RenderableTextComponent(RENDER_SPACER, spacerTag);
//        }
//    }
//
//    @Override
//    public void register(IRegistrar registrar) {
//        //registrar.registerStackProvider(provider, BlockFiller.class);
//
//        registrar.registerComponentProvider(HUDHandlerMillstone.INSTANCE, TooltipPosition.BODY, MillstoneBlock.class);
//        registrar.registerComponentProvider(HUDHandlerMillstone.INSTANCE, TooltipPosition.BODY, ManualMillstoneBlock.class);
//        registrar.registerBlockDataProvider(HUDHandlerMillstone.INSTANCE, MillstoneBlock.class);
//        registrar.registerBlockDataProvider(HUDHandlerMillstone.INSTANCE, ManualMillstoneBlock.class);
//
//        //registrar.registerComponentProvider(this, TooltipPosition.BODY, BlockHPChoppingBase.class);
//        //registrar.registerBlockDataProvider(this, TooltipPosition.BODY, BlockChopper.class);
//        //registrar.registerBlockDataProvider(this, TooltipPosition.BODY, BlockChoppingBlock.class);
//
//        registrar.registerComponentProvider(HUDHandlerPress.INSTANCE, TooltipPosition.BODY, PressBlock.class);
//        registrar.registerBlockDataProvider(HUDHandlerPress.INSTANCE, PressBlock.class);
//
//        registrar.registerComponentProvider(HUDHandlerFiller.INSTANCE, TooltipPosition.BODY, FillerBlock.class);
//        registrar.registerBlockDataProvider(HUDHandlerFiller.INSTANCE, FillerBlock.class);
//
//        registrar.addConfig(CONFIG_SHOW_ITEMS, true);
//    }
//
//    /*
//    @Override
//    public ItemStack getStack(IDataAccessor accessor, IPluginConfig config) {
//        //if (accessor.getBlock().equals(ModBlocks.BLOCK_CHOPPER_FILLER))
//        //    return accessor.getBlock().getPickBlock(accessor.getBlockState(), accessor.getHitResult(), accessor.getWorld(), accessor.getPosition(), accessor.getPlayer());
//        return ItemStack.EMPTY;
//    }*/
///*
//    @Override
//    public void appendBody(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
//        CompoundNBT nbt = accessor.getNBTData();
//        if (nbt.hasKey("horsepower:chopper", 10)) {
//            nbt = nbt.getCompoundTag("horsepower:chopper");
//
//            double totalWindup = Configs.general.pointsForWindup > 0 ? Configs.general.pointsForWindup : 1;
//            double windup = (double) nbt.getInteger("currentWindup");
//            double current = (double) nbt.getInteger("chopTime");
//            double total = (double) nbt.getInteger("totalChopTime");
//            double progressWindup = Math.round(((windup / totalWindup) * 100D) * 100D) / 100D;
//            double progressChopping = Math.round(((current / total) * 100D) * 100D) / 100D;
//
//            if (accessor.getTileEntity() instanceof TileEntityChopper || accessor.getTileEntity() instanceof FillerTileEntity)
//                currenttip.add(Localization.WAILA.WINDUP_PROGRESS.translate(String.valueOf(progressWindup) + "%"));
//            if (total > 1 || accessor.getTileEntity() instanceof TileEntityManualChopper) {
//                currenttip.add(Localization.WAILA.CHOPPING_PROGRESS.translate(String.valueOf(progressChopping) + "%"));
//            }
//        }
//    }*/
//
///*    @Override
//    public void appendServerData(CompoundNBT data, ServerPlayerEntity player, World world, TileEntity tileEntity) {
//        CompoundNBT tile = new CompoundNBT();
//        if (te != null)
//            te.writeToNBT(tile);
//        if (te instanceof TileEntityMillstone || te instanceof TileEntityHandMillstone)
//            tag.setTag("horsepower:grindstone", tile);
//        else if (te instanceof TileEntityChopper || te instanceof TileEntityManualChopper)
//            tag.setTag("horsepower:chopper", tile);
//        else if (te instanceof TileEntityPress)
//            tag.setTag("horsepower:press", tile);
//        return tag;
//    }*/
//}
