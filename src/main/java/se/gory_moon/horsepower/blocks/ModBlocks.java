package se.gory_moon.horsepower.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import se.gory_moon.horsepower.HorsePowerItemGroup;
import se.gory_moon.horsepower.HorsePowerMod;
import se.gory_moon.horsepower.items.ItemBlockDouble;
import se.gory_moon.horsepower.lib.Constants;
import se.gory_moon.horsepower.lib.Reference;
import se.gory_moon.horsepower.tileentity.TileEntityFiller;
import se.gory_moon.horsepower.tileentity.TileEntityHandMillstone;
import se.gory_moon.horsepower.tileentity.TileEntityMillstone;
import se.gory_moon.horsepower.tileentity.TileEntityPress;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import static net.minecraftforge.fml.RegistryObject.of;

public class ModBlocks {

    public static final RegistryObject<BlockHandMillstone> BLOCK_HAND_MILLSTONE = of(Reference.RESOURCE_PREFIX + Constants.HAND_MILLSTONE_BLOCK, () -> Block.class);
    public static final RegistryObject<BlockMillstone> BLOCK_MILLSTONE = of(Reference.RESOURCE_PREFIX + Constants.MILLSTONE_BLOCK, () -> Block.class);

    /*public static final BlockChoppingBlock BLOCK_MANUAL_CHOPPER = new BlockChoppingBlock();
    public static final BlockChopper BLOCK_CHOPPER = new BlockChopper();
    public static final BlockFiller BLOCK_CHOPPER_FILLER = (BlockFiller) new BlockFiller(Material.WOOD, "chopper_", true).setHarvestLevel1("axe", 0);//.setHardness(5F).setResistance(5F);*/

    public static final RegistryObject<BlockPress> BLOCK_PRESS = of(Reference.RESOURCE_PREFIX + Constants.PRESS_BLOCK, () -> Block.class);
    public static final RegistryObject<BlockFiller> BLOCK_PRESS_FILLER = of(Reference.RESOURCE_PREFIX + Constants.PRESS_FILLER, () -> Block.class);

    public static TileEntityType<TileEntityMillstone> millstoneTile;
    public static TileEntityType<TileEntityHandMillstone> handMillstoneTile;
    public static TileEntityType<TileEntityPress> pressTile;
    public static TileEntityType<TileEntityFiller> fillerTile;

    @Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistrationHandler {
        public static final Set<BlockItem> ITEM_BLOCKS = new HashSet<>();

        /**
         * Register this mod's {@link Block}s.
         *
         * @param event The event
         */
        @SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event) {
            final IForgeRegistry<Block> registry = event.getRegistry();

            final Block[] blocks = {
                    register(new BlockHandMillstone(), Constants.HAND_MILLSTONE_BLOCK),
                    register(new BlockMillstone(), Constants.MILLSTONE_BLOCK),
                    /*BLOCK_MANUAL_CHOPPER,
                    BLOCK_CHOPPER, BLOCK_CHOPPER_FILLER,*/
                    register(new BlockPress(), Constants.PRESS_BLOCK), register(new BlockFiller(Block.Properties.create(Material.WOOD).hardnessAndResistance(5F).sound(SoundType.WOOD), true).setHarvestLevel(ToolType.AXE, 1), Constants.PRESS_FILLER),
            };

            registry.registerAll(blocks);
        }

        private static Block register(Block block, String name) {
            return block.setRegistryName(Reference.MODID, name);
        }

        /**
         * Register this mod's {@link BlockItem}s.
         *
         * @param event The event
         */
        @SubscribeEvent
        public static void registerItemBlocks(RegistryEvent.Register<Item> event) {
            HorsePowerMod.itemGroup = new HorsePowerItemGroup();
            final BlockItem[] items = {
                new BlockItem(BLOCK_HAND_MILLSTONE.orElse(null), new Item.Properties().group(HorsePowerMod.itemGroup)),
                new BlockItem(BLOCK_MILLSTONE.orElse(null), new Item.Properties().group(HorsePowerMod.itemGroup)),
                /*new ItemBlock(BLOCK_MANUAL_CHOPPER),
                new ItemBlockDouble(BLOCK_CHOPPER, BLOCK_CHOPPER_FILLER),*/
                new ItemBlockDouble(BLOCK_PRESS.orElse(null), BLOCK_PRESS_FILLER.orElse(null))
            };

            final IForgeRegistry<Item> registry = event.getRegistry();

            for (final BlockItem item : items) {
                registry.register(item.setRegistryName(item.getBlock().getRegistryName()));
                ITEM_BLOCKS.add(item);
            }
        }

        @SubscribeEvent
        public static void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event) {
            IForgeRegistry<TileEntityType<?>> reg = event.getRegistry();
            reg.register(millstoneTile = create(TileEntityMillstone::new, BLOCK_MILLSTONE, Constants.MILLSTONE_BLOCK));
            reg.register(handMillstoneTile = create(TileEntityHandMillstone::new, BLOCK_HAND_MILLSTONE, Constants.HAND_CHOPPING_BLOCK));
            reg.register(pressTile = create(TileEntityPress::new, BLOCK_PRESS, Constants.PRESS_BLOCK));
            reg.register(fillerTile = create(TileEntityFiller::new, BLOCK_PRESS_FILLER, Constants.PRESS_FILLER));
        }

        private static <T extends TileEntity> TileEntityType<T> create(Supplier<? extends T> factory, RegistryObject<? extends Block> block, String name) {
            return (TileEntityType<T>) TileEntityType.Builder.create(factory, block.orElseThrow(RuntimeException::new)).build(null).setRegistryName(Reference.MODID, name);
        }
    }

}
