package se.gory_moon.horsepower.recipes;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import se.gory_moon.horsepower.lib.Reference;

public class RecipeSerializers {



    @Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistrationHandler {

        @SubscribeEvent
        public void registerRecipeTypes(RegistryEvent.Register<IRecipeSerializer<?>> event) {

        }

    }

}
