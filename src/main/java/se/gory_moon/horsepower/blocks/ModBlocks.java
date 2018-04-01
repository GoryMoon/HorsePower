package se.gory_moon.horsepower.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import se.gory_moon.horsepower.items.ItemBlockChopper;
import se.gory_moon.horsepower.items.ItemBlockPress;
import se.gory_moon.horsepower.lib.Reference;
import se.gory_moon.horsepower.tileentity.*;

import java.util.HashSet;
import java.util.Set;

public class ModBlocks {

    public static final BlockHandGrindstone BLOCK_HAND_GRINDSTONE = new BlockHandGrindstone();
    public static final BlockGrindstone BLOCK_GRINDSTONE = new BlockGrindstone();
    public static final BlockChoppingBlock BLOCK_MANUAL_CHOPPER = new BlockChoppingBlock();
    public static final BlockChopper BLOCK_CHOPPER = new BlockChopper();
    public static final BlockFiller BLOCK_CHOPPER_FILLER = (BlockFiller) new BlockFiller(Material.WOOD, "chopper_", true).setHarvestLevel1("axe", 0).setHardness(5F).setResistance(5F);
    public static final BlockPress BLOCK_PRESS = new BlockPress();
    public static final BlockFiller BLOCK_PRESS_FILLER = (BlockFiller) new BlockFiller(Material.WOOD, "press_", true).setHarvestLevel1("axe", 1).setHardness(5F).setResistance(5F);

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

            BLOCK_PRESS_FILLER.setHarvestLevel("axe", 1);
            final Block[] blocks = {BLOCK_HAND_GRINDSTONE, BLOCK_GRINDSTONE,
                    BLOCK_MANUAL_CHOPPER, BLOCK_CHOPPER, BLOCK_CHOPPER_FILLER,
                    BLOCK_PRESS, BLOCK_PRESS_FILLER,
            };

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
                new ItemBlock(BLOCK_HAND_GRINDSTONE),
                new ItemBlock(BLOCK_GRINDSTONE),
                new ItemBlock(BLOCK_MANUAL_CHOPPER),
                new ItemBlockChopper(BLOCK_CHOPPER),
                new ItemBlockPress(BLOCK_PRESS)
            };

            final IForgeRegistry<Item> registry = event.getRegistry();

            for (final ItemBlock item : items) {
                registry.register(item.setRegistryName(item.getBlock().getRegistryName()));
                ITEM_BLOCKS.add(item);
            }
        }
    }

    public static void registerTileEntities() {
        registerTileEntity(TileEntityHandGrindstone.class);
        registerTileEntity(TileEntityGrindstone.class);
        registerTileEntity(TileEntityManualChopper.class);
        registerTileEntity(TileEntityChopper.class);
        registerTileEntity(TileEntityFiller.class);
        registerTileEntity(TileEntityPress.class);
    }

    private static void registerTileEntity(Class<? extends TileEntity> tileEntityClass) {
        GameRegistry.registerTileEntity(tileEntityClass, Reference.RESOURCE_PREFIX + tileEntityClass.getSimpleName().replaceFirst("TileEntity", ""));
    }

}
