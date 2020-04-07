package se.gory_moon.horsepower.recipes;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import se.gory_moon.horsepower.util.Constants;

public class RecipeSerializers {

    public static final HPRecipeType<MillingRecipe> MILLING_TYPE = new HPRecipeType<>("milling");
    public static final HPRecipeType<PressingRecipe> PRESSING_TYPE = new HPRecipeType<>("pressing");

    public static final MillingSerializer MILLING_SERIALIZER = new MillingSerializer();
    public static final PressingSerializer PRESSING_SERIALIZER = new PressingSerializer();

    @Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistrationHandler {

        @SubscribeEvent
        public static void registerRecipeTypes(RegistryEvent.Register<IRecipeSerializer<?>> event) {
            IForgeRegistry<IRecipeSerializer<?>> registry = event.getRegistry();
            registerRecipeType(MILLING_TYPE);
            registerRecipeType(PRESSING_TYPE);

            registry.register(MILLING_SERIALIZER.setRegistryName(Constants.MOD_ID, "milling"));
            registry.register(PRESSING_SERIALIZER.setRegistryName(Constants.MOD_ID, "pressing"));
        }

        private static <T extends IRecipe<?>> void registerRecipeType(IRecipeType<T> type) {
            Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(Constants.MOD_ID, type.toString()), type);
        }
    }

    private static class HPRecipeType<T extends IRecipe<?>> implements IRecipeType<T> {
        private String key;

        private HPRecipeType(String key) {
            this.key = key;
        }

        @Override
        public String toString() {
            return key;
        }
    }

}
