package se.gorymoon.horsepower.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import se.gorymoon.horsepower.HorsePowerMod;
import se.gorymoon.horsepower.lib.Reference;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@GameRegistry.ObjectHolder(Reference.MODID)
public class ModItems {

    //TODO add textures
    public static final Item FLOUR = new Item().setRegistryName("flour").setUnlocalizedName("flour").setCreativeTab(CreativeTabs.FOOD);
    public static final Item DOUGH = new Item().setRegistryName("dough").setUnlocalizedName("dough").setCreativeTab(CreativeTabs.FOOD);

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
                registry.register(item);
                ITEMS.add(item);
            }
        }
    }

    public static void registerRecipes() {
        removeRecipesWithResult(new ItemStack(Items.BREAD));
        GameRegistry.addShapelessRecipe(new ItemStack(DOUGH), FLOUR, Items.WATER_BUCKET);
        GameRegistry.addSmelting(DOUGH, new ItemStack(Items.BREAD), 0F);
    }

    private static void removeRecipesWithResult(ItemStack resultItem) {
        ArrayList recipes = (ArrayList) CraftingManager.getInstance().getRecipeList();

        for (int scan = 0; scan < recipes.size(); scan++) {
            IRecipe tmpRecipe = (IRecipe) recipes.get(scan);
            ItemStack recipeResult = tmpRecipe.getRecipeOutput();
            if (ItemStack.areItemStacksEqual(resultItem, recipeResult)) {
                HorsePowerMod.logger.debug("Removing Recipe: " + recipes.get(scan) + " -> " + recipeResult);
                recipes.remove(scan);
            }
        }
    }
}
