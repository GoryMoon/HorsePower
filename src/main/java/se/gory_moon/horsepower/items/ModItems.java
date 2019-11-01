package se.gory_moon.horsepower.items;

import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import se.gory_moon.horsepower.HorsePower;
import se.gory_moon.horsepower.util.Constants;

import static se.gory_moon.horsepower.util.Constants.MOD_ID;


public class ModItems {

    private static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, MOD_ID);

    public static final RegistryObject<Item> FLOUR = ITEMS.register(Constants.FLOUR_ITEM, () -> new Item(new Item.Properties().group(HorsePower.itemGroup)));
    public static final RegistryObject<Item> DOUGH = ITEMS.register(Constants.DOUGH_ITEM, () -> new Item(new Item.Properties().group(HorsePower.itemGroup)));

    public static void register(IEventBus event) {
        ITEMS.register(event);
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
