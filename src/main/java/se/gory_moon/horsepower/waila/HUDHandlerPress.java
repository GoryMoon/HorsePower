package se.gory_moon.horsepower.waila;

import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IDataAccessor;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerDataProvider;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.tileentity.PressTileEntity;
import se.gory_moon.horsepower.util.Localization;

import java.util.List;

public final class HUDHandlerPress implements IComponentProvider, IServerDataProvider<TileEntity> {

    static final HUDHandlerPress INSTANCE = new HUDHandlerPress();

    @Override
    public void appendBody(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
        CompoundNBT nbt = accessor.getServerData();
        double current = (double) nbt.getInt("currentPressStatus");
        double total = Configs.general.pointsForPress > 0 ? Configs.general.pointsForPress: 1;
        double progress = Math.round(((current / total) * 100D) * 100D) / 100D;
        tooltip.add(new StringTextComponent(Localization.WAILA.PRESS_PROGRESS.translate(String.valueOf(progress))));
        Provider.showItems(tooltip, accessor, config, (int) progress);
    }

    @Override
    public void appendServerData(CompoundNBT data, ServerPlayerEntity player, World world, TileEntity te) {
        if (te instanceof PressTileEntity)
            te.write(data);
    }
}
