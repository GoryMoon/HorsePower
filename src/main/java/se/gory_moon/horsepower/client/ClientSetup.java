package se.gory_moon.horsepower.client;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import se.gory_moon.horsepower.Registration;
import se.gory_moon.horsepower.client.renderer.TileEntityChopperRender;
import se.gory_moon.horsepower.client.renderer.TileEntityChoppingBlockRender;
import se.gory_moon.horsepower.client.renderer.TileEntityFillerRender;
import se.gory_moon.horsepower.client.renderer.TileEntityManualMillstoneRender;
import se.gory_moon.horsepower.client.renderer.TileEntityMillstoneRender;
import se.gory_moon.horsepower.client.renderer.TileEntityPressRender;
import se.gory_moon.horsepower.tileentity.FillerTileEntity;
import se.gory_moon.horsepower.tileentity.ManualMillstoneTileEntity;
import se.gory_moon.horsepower.tileentity.MillstoneTileEntity;
import se.gory_moon.horsepower.tileentity.PressTileEntity;
import se.gory_moon.horsepower.tileentity.ChopperTileEntity;
import se.gory_moon.horsepower.tileentity.ManualChopperTileEntity;
import se.gory_moon.horsepower.util.Constants;
import se.gory_moon.horsepower.util.color.ColorGetter;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        ClientRegistry.bindTileEntitySpecialRenderer(MillstoneTileEntity.class, new TileEntityMillstoneRender());
        ClientRegistry.bindTileEntitySpecialRenderer(ManualMillstoneTileEntity.class, new TileEntityManualMillstoneRender());
        ClientRegistry.bindTileEntitySpecialRenderer(FillerTileEntity.class, new TileEntityFillerRender());
        ClientRegistry.bindTileEntitySpecialRenderer(ChopperTileEntity.class, new TileEntityChopperRender());
        ClientRegistry.bindTileEntitySpecialRenderer(ManualChopperTileEntity.class, new TileEntityChoppingBlockRender());
        ClientRegistry.bindTileEntitySpecialRenderer(PressTileEntity.class, new TileEntityPressRender());

        //TODO make server command
        //ClientCommandHandler.instance.registerCommand(new HorsePowerCommand());

        Minecraft.getInstance().getBlockColors().register((state, worldIn, pos, tintIndex) -> {
            if (worldIn != null && pos != null) {
                TileEntity tileEntity = worldIn.getTileEntity(pos);
                if (tileEntity instanceof MillstoneTileEntity) {
                    MillstoneTileEntity te = (MillstoneTileEntity) tileEntity;
                    ItemStack outputStack = te.getStackInSlot(1);
                    ItemStack secondaryStack = te.getStackInSlot(2);
                    if (outputStack.getCount() < secondaryStack.getCount())
                        outputStack = secondaryStack;
                    if (!ItemStack.areItemsEqual(te.renderStack, outputStack)) {
                        te.renderStack = outputStack;
                        if (!outputStack.isEmpty())
                            te.millColor = ColorGetter.getColors(outputStack, 1).get(0);
                        else
                            te.millColor = -1;
                        te.renderStack = outputStack;
                    }

                    if (te.millColor != -1)
                        return te.millColor;
                }
            }
            return -1;
        }, Registration.MILLSTONE_BLOCK.get());
    }
}
