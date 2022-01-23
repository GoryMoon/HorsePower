package se.gory_moon.horsepower.client;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import se.gory_moon.horsepower.Registration;
import se.gory_moon.horsepower.client.utils.color.ColorGetter;
import se.gory_moon.horsepower.tileentity.MillstoneTileEntity;
import se.gory_moon.horsepower.util.Constants;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {

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
