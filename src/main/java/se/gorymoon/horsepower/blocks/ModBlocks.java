package se.gorymoon.horsepower.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import se.gorymoon.horsepower.lib.Constants;
import se.gorymoon.horsepower.lib.Reference;
import se.gorymoon.horsepower.tileentity.TileEntityMill;

import java.util.HashSet;
import java.util.Set;

public class ModBlocks {

    public static final BlockMill BLOCK_MILL = new BlockMill();

    @Mod.EventBusSubscriber(modid = Reference.MODID)
    public static class RegistrationHandler {
        public static final Set<ItemBlock> ITEM_BLOCKS = new HashSet<>();

        /**
         * Register this mod's {@link Block}s.
         *
         * @param event The event
         */
        @SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event) {
            final IForgeRegistry<Block> registry = event.getRegistry();

            final Block[] blocks = {BLOCK_MILL};

            registry.registerAll(blocks);
        }

        /**
         * Register this mod's {@link ItemBlock}s.
         *
         * @param event The event
         */
        @SubscribeEvent
        public static void registerItemBlocks(RegistryEvent.Register<Item> event) {
            final ItemBlock[] items = {
                new ItemBlock(BLOCK_MILL)
            };

            final IForgeRegistry<Item> registry = event.getRegistry();

            for (final ItemBlock item : items) {
                registry.register(item.setRegistryName(item.getBlock().getRegistryName()));
                ITEM_BLOCKS.add(item);
            }
        }
    }

    public static void registerTileEntities() {
        registerTileEntityNoPrefix(TileEntityMill.class, TileEntityMill.class.getSimpleName().replaceFirst("TileEntity", ""), Constants.MILL_TE_ID);
    }

    private static void registerTileEntity(Class<? extends TileEntity> tileEntityClass) {
        GameRegistry.registerTileEntity(tileEntityClass, Reference.RESOURCE_PREFIX + tileEntityClass.getSimpleName().replaceFirst("TileEntity", ""));
    }

    private static void registerTileEntity(Class<? extends TileEntity> tileEntityClass, String name, String legacyName) {
        GameRegistry.registerTileEntityWithAlternatives(tileEntityClass, Reference.RESOURCE_PREFIX + name, Reference.RESOURCE_PREFIX + legacyName);
    }

    private static void registerTileEntityNoPrefix(Class<? extends TileEntity> tileEntityClass, String name, String legacyName) {
        GameRegistry.registerTileEntityWithAlternatives(tileEntityClass, Reference.RESOURCE_PREFIX + name, legacyName);
    }

}
