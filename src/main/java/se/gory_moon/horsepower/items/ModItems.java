package se.gory_moon.horsepower.items;

import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFlower;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
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

        if (Configs.removeVanillaRecipes)
            removeRecipes();
    }

    private static void removeRecipes() {
        Container dummyContainer = new Container() {
            @Override
            public boolean canInteractWith(EntityPlayer entityplayer) {
                return true;
            }

            @Override
            public void onCraftMatrixChanged(IInventory par1IInventory) {
            }
        };
        InventoryCrafting crafting = new InventoryCrafting(dummyContainer, 3, 3);


        removeRecipesWithRecipe(findMatchingRecipe(setRecipe(crafting, "###", '#', Items.WHEAT)));
        removeRecipesWithRecipe(findMatchingRecipe(setRecipe(crafting, "#", '#', Items.REEDS)));
        removeRecipesWithRecipe(findMatchingRecipe(setRecipe(crafting, "#", '#', new ItemStack(Blocks.BONE_BLOCK))));
        removeRecipesWithRecipe(findMatchingRecipe(setRecipe(crafting, "#", '#', Items.BONE)));
        removeRecipesWithRecipe(findMatchingRecipe(setRecipe(crafting, "#", '#', Items.BEETROOT)));
        removeRecipesWithRecipe(findMatchingRecipe(setRecipe(crafting, "#", '#', new ItemStack(Blocks.YELLOW_FLOWER, 1, BlockFlower.EnumFlowerType.DANDELION.getMeta()))));
        removeRecipesWithRecipe(findMatchingRecipe(setRecipe(crafting, "#", '#', new ItemStack(Blocks.RED_FLOWER, 1, BlockFlower.EnumFlowerType.POPPY.getMeta()))));

        removeRecipesWithRecipe(findMatchingRecipe(setRecipe(crafting, "#", '#', new ItemStack(Blocks.RED_FLOWER, 1, BlockFlower.EnumFlowerType.BLUE_ORCHID.getMeta()))));
        removeRecipesWithRecipe(findMatchingRecipe(setRecipe(crafting, "#", '#', new ItemStack(Blocks.RED_FLOWER, 1, BlockFlower.EnumFlowerType.ALLIUM.getMeta()))));
        removeRecipesWithRecipe(findMatchingRecipe(setRecipe(crafting, "#", '#', new ItemStack(Blocks.RED_FLOWER, 1, BlockFlower.EnumFlowerType.HOUSTONIA.getMeta()))));
        removeRecipesWithRecipe(findMatchingRecipe(setRecipe(crafting, "#", '#', new ItemStack(Blocks.RED_FLOWER, 1, BlockFlower.EnumFlowerType.RED_TULIP.getMeta()))));
        removeRecipesWithRecipe(findMatchingRecipe(setRecipe(crafting, "#", '#', new ItemStack(Blocks.RED_FLOWER, 1, BlockFlower.EnumFlowerType.ORANGE_TULIP.getMeta()))));
        removeRecipesWithRecipe(findMatchingRecipe(setRecipe(crafting, "#", '#', new ItemStack(Blocks.RED_FLOWER, 1, BlockFlower.EnumFlowerType.WHITE_TULIP.getMeta()))));
        removeRecipesWithRecipe(findMatchingRecipe(setRecipe(crafting, "#", '#', new ItemStack(Blocks.RED_FLOWER, 1, BlockFlower.EnumFlowerType.PINK_TULIP.getMeta()))));
        removeRecipesWithRecipe(findMatchingRecipe(setRecipe(crafting, "#", '#', new ItemStack(Blocks.RED_FLOWER, 1, BlockFlower.EnumFlowerType.OXEYE_DAISY.getMeta()))));
        removeRecipesWithRecipe(findMatchingRecipe(setRecipe(crafting, "#", '#', new ItemStack(Blocks.DOUBLE_PLANT, 1, BlockDoublePlant.EnumPlantType.SUNFLOWER.getMeta()))));
        removeRecipesWithRecipe(findMatchingRecipe(setRecipe(crafting, "#", '#', new ItemStack(Blocks.DOUBLE_PLANT, 1, BlockDoublePlant.EnumPlantType.SYRINGA.getMeta()))));
        removeRecipesWithRecipe(findMatchingRecipe(setRecipe(crafting, "#", '#', new ItemStack(Blocks.DOUBLE_PLANT, 1, BlockDoublePlant.EnumPlantType.ROSE.getMeta()))));
        removeRecipesWithRecipe(findMatchingRecipe(setRecipe(crafting, "#", '#', new ItemStack(Blocks.DOUBLE_PLANT, 1, BlockDoublePlant.EnumPlantType.PAEONIA.getMeta()))));
    }

    private static InventoryCrafting setRecipe(InventoryCrafting crafting, Object... recipeComponents) {
        crafting.clear();
        String s = "";
        int i = 0;
        int j = 0;
        int k = 0;

        if (recipeComponents[i] instanceof String[]) {
            String[] astring = (String[])recipeComponents[i++];

            for (String s2 : astring) {
                ++k;
                j = s2.length();
                s = s + s2;
            }
        } else {
            while (recipeComponents[i] instanceof String) {
                String s1 = (String)recipeComponents[i++];
                ++k;
                j = s1.length();
                s = s + s1;
            }
        }

        Map<Character, ItemStack> map;

        for (map = Maps.newHashMap(); i < recipeComponents.length; i += 2) {
            Character character = (Character)recipeComponents[i];
            ItemStack itemstack = ItemStack.EMPTY;

            if (recipeComponents[i + 1] instanceof Item) {
                itemstack = new ItemStack((Item)recipeComponents[i + 1]);
            } else if (recipeComponents[i + 1] instanceof Block) {
                itemstack = new ItemStack((Block)recipeComponents[i + 1], 1, 32767);
            } else if (recipeComponents[i + 1] instanceof ItemStack) {
                itemstack = (ItemStack)recipeComponents[i + 1];
            }

            map.put(character, itemstack);
        }


        for (int l = 0; l < j * k; ++l) {
            char c0 = s.charAt(l);

            if (map.containsKey(Character.valueOf(c0))) {
                crafting.setInventorySlotContents(l, map.get(Character.valueOf(c0)).copy());
            }
        }

        return crafting;
    }

    private static void removeRecipesWithResult(ItemStack resultItem) {
        ArrayList recipes = (ArrayList) CraftingManager.getInstance().getRecipeList();

        for (int scan = 0; scan < recipes.size(); scan++) {
            IRecipe tmpRecipe = (IRecipe) recipes.get(scan);
            ItemStack recipeResult = tmpRecipe.getRecipeOutput();
            if (ItemStack.areItemStacksEqual(resultItem, recipeResult)) {
                recipes.remove(scan);
            }
        }
    }

    private static void removeRecipesWithRecipe(IRecipe recipe) {
        ArrayList recipes = (ArrayList) CraftingManager.getInstance().getRecipeList();

        if (recipe != null)
            recipes.remove(recipe);
    }

    public static IRecipe findMatchingRecipe(InventoryCrafting craftMatrix) {
        for (IRecipe irecipe : CraftingManager.getInstance().getRecipeList()) {
            if (irecipe.matches(craftMatrix, null)) {
                return irecipe;
            }
        }

        return null;
    }
}
