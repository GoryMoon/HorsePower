package se.gory_moon.horsepower.data;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import se.gory_moon.horsepower.util.Constants;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataEventHandler {

    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onDataDump(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        generator.addProvider(new HPRecipeProvider(generator));
        generator.addProvider(new HPAdvancementProvider(generator));
    }
}
