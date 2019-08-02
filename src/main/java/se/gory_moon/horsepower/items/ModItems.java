package se.gory_moon.horsepower.items;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import se.gory_moon.horsepower.HorsePower;
import se.gory_moon.horsepower.util.Constants;

import java.util.HashSet;
import java.util.Set;


public class ModItems {

    public static final RegistryObject<Item> FLOUR = RegistryObject.of(Constants.RESOURCE_PREFIX + Constants.FLOUR_ITEM, () -> Item.class);
    public static final RegistryObject<Item> DOUGH = RegistryObject.of(Constants.RESOURCE_PREFIX + Constants.DOUGH_ITEM, () -> Item.class);

    @Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistrationHandler {
        public static final Set<Item> ITEMS = new HashSet<>();

        /**
         * Register this mod's {@link Item}s.
         *
         * @param event The event
         */
        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event) {
            final Item[] items = {
                    new Item(new Item.Properties().group(HorsePower.itemGroup)).setRegistryName(Constants.RESOURCE_PREFIX + Constants.FLOUR_ITEM),
                    new Item(new Item.Properties().group(HorsePower.itemGroup)).setRegistryName(Constants.RESOURCE_PREFIX + Constants.DOUGH_ITEM)
            };

            final IForgeRegistry<Item> registry = event.getRegistry();

            for (final Item item : items) {
                registry.register(item);
                ITEMS.add(item);
            }
        }
    }

    /*@SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> ev) throws NoSuchFieldException, IllegalAccessException {
        recipes = ev.getRegistry().getEntries();
        ResourceLocation loc = new ResourceLocation("horsepower:chopper");
        ev.getRegistry().register(new ShapedChoppingRecipe(loc, OreDictionary.getOres("logWood"), new ItemStack(ModBlocks.BLOCK_CHOPPER), "LSL", "SFS", "SWS", 'S', "stickWood", 'L', Ingredient.fromStacks(new ItemStack(Items.LEAD)), 'F', Ingredient.fromStacks(new ItemStack(Items.FLINT)), 'W', "logWood").setRegistryName(loc));
        if (Configs.general.enableHandChoppingBlock) {
            loc = new ResourceLocation("horsepower:manual_chopping");
            ev.getRegistry().register(new ShapelessChoppingRecipe(loc, OreDictionary.getOres("logWood"), new ItemStack(ModBlocks.BLOCK_MANUAL_CHOPPER, 2), "logWood", Items.FLINT).setRegistryName(loc));
        }

        if (Configs.general.removeVanillaRecipes)
            removeRecipes();
    }*/
}
