package se.gory_moon.horsepower.compat.waila;
//TODO waila support
//import java.util.List;
//
//import mcp.mobius.waila.api.IComponentProvider;
//import mcp.mobius.waila.api.IDataAccessor;
//import mcp.mobius.waila.api.IPluginConfig;
//import mcp.mobius.waila.api.IServerDataProvider;
//import net.minecraft.entity.player.ServerPlayerEntity;
//import net.minecraft.nbt.CompoundNBT;
//import net.minecraft.tileentity.TileEntity;
//import net.minecraft.util.text.ITextComponent;
//import net.minecraft.util.text.StringTextComponent;
//import net.minecraft.world.World;
//import se.gory_moon.horsepower.Configs;
//import se.gory_moon.horsepower.tileentity.PressTileEntity;
//import se.gory_moon.horsepower.util.Localization;
//
//public final class HUDHandlerPress implements IComponentProvider, IServerDataProvider<TileEntity> {
//
//    static final HUDHandlerPress INSTANCE = new HUDHandlerPress();
//
//    @Override
//    public void appendBody(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
//        CompoundNBT nbt = accessor.getServerData();
//        double current = nbt.getInt("currentPressStatus");
//        double total = Configs.SERVER.pointsPerPress.get() > 0 ? Configs.SERVER.pointsPerPress.get(): 1;
//        double progress = Math.round(((current / total) * 100D) * 100D) / 100D;
//        tooltip.add(new StringTextComponent(Localization.WAILA.PRESS_PROGRESS.translate(String.valueOf(progress))));
//        Provider.showItems(tooltip, accessor, config, (int) progress);
//    }
//
//    @Override
//    public void appendServerData(CompoundNBT data, ServerPlayerEntity player, World world, TileEntity te) {
//        if (te instanceof PressTileEntity)
//            te.write(data);
//    }
//}
