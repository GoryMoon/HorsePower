package se.gory_moon.horsepower.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.blocks.ModBlocks;
import se.gory_moon.horsepower.lib.Constants;
import se.gory_moon.horsepower.lib.Reference;

import java.util.HashSet;
import java.util.Set;

@GameRegistry.ObjectHolder(Reference.MODID)
public class ModItems {

    public static final Item FLOUR = new Item().setRegistryName(Constants.FLOUR_ITEM).setUnlocalizedName(Constants.FLOUR_ITEM).setCreativeTab(CreativeTabs.FOOD);
    public static final Item DOUGH = new Item().setRegistryName(Constants.DOUGH_ITEM).setUnlocalizedName(Constants.DOUGH_ITEM).setCreativeTab(CreativeTabs.FOOD);

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class RegistrationHandler {
        public static final Set<Item> ITEMS = new HashSet<>();

        /**
         * Register this mod's {@link Item}s.
         *
         * @param event The event
         */
        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event) {

            final Item[] items = {FLOUR, DOUGH};

            final IForgeRegistry<Item> registry = event.getRegistry();

            for (final Item item : items) {
                if (!Configs.enableFlour && item == FLOUR)
                    continue;
                if (!Configs.enableDough && item == DOUGH)
                    continue;

                registry.register(item);
                ITEMS.add(item);
            }
        }
    }

    public static void registerRecipes() {
        if (Configs.enableDough) {
            if (Configs.enableFlour) {
                GameRegistry.addShapelessRecipe(new ItemStack(DOUGH), FLOUR, Items.WATER_BUCKET);
                OreDictionary.registerOre("foodFlour", FLOUR);
            }
            GameRegistry.addSmelting(DOUGH, new ItemStack(Items.BREAD), 0F);
            OreDictionary.registerOre("foodDough", DOUGH);
        }
        GameRegistry.addRecipe(new ShapedOreRecipe(ModBlocks.BLOCK_GRINDSTONE, "LSL", "###", "###", 'S', "stickWood", '#', "stone", 'L', Items.LEAD));
        GameRegistry.addRecipe(new ShapedOreRecipe(ModBlocks.BLOCK_HAND_GRINSTONE, "  S", "###", "###", 'S', "stickWood", '#', "stone"));
        GameRegistry.addRecipe(new ShapedOreRecipe(ModBlocks.BLOCK_CHOPPER, "LSL", "SFS", "SWS", 'S', "stickWood", 'L', Items.LEAD, 'F', Items.FLINT, 'W', "logWood"));
    }
}
