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
import se.gory_moon.horsepower.HorsePower;
import se.gory_moon.horsepower.HorsePowerItemGroup;
import se.gory_moon.horsepower.items.DoubleBlockItem;
import se.gory_moon.horsepower.tileentity.FillerTileEntity;
import se.gory_moon.horsepower.tileentity.HandMillstoneTileEntity;
import se.gory_moon.horsepower.tileentity.MillstoneTileEntity;
import se.gory_moon.horsepower.tileentity.PressTileEntity;
import se.gory_moon.horsepower.util.Constants;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import static net.minecraftforge.fml.RegistryObject.of;
import static se.gory_moon.horsepower.util.Constants.RESOURCE_PREFIX;

@SuppressWarnings("RedundantCast")
public class ModBlocks {

    public static final RegistryObject<HandMillstoneBlock> BLOCK_HAND_MILLSTONE = of(RESOURCE_PREFIX + Constants.HAND_MILLSTONE_BLOCK, () -> Block.class);
    public static final RegistryObject<MillstoneBlock> BLOCK_MILLSTONE = of(RESOURCE_PREFIX + Constants.MILLSTONE_BLOCK, () -> Block.class);

    /*public static final BlockChoppingBlock BLOCK_MANUAL_CHOPPER = new BlockChoppingBlock();
    public static final BlockChopper BLOCK_CHOPPER = new BlockChopper();
    public static final BlockFiller BLOCK_CHOPPER_FILLER = (BlockFiller) new BlockFiller(Material.WOOD, "chopper_", true).setHarvestLevel1("axe", 0);//.setHardness(5F).setResistance(5F);*/

    public static final RegistryObject<PressBlock> BLOCK_PRESS = of(RESOURCE_PREFIX + Constants.PRESS_BLOCK, () -> Block.class);
    public static final RegistryObject<FillerBlock> BLOCK_PRESS_FILLER = of(RESOURCE_PREFIX + Constants.PRESS_FILLER, () -> Block.class);

    public static final RegistryObject<TileEntityType<MillstoneTileEntity>> MILLSTONE_TILE = of(RESOURCE_PREFIX + Constants.MILLSTONE_BLOCK, (Supplier<Class<? super TileEntityType<?>>>) () -> TileEntityType.class);
    public static final RegistryObject<TileEntityType<HandMillstoneTileEntity>> HAND_MILLSTONE_TILE = of(RESOURCE_PREFIX + Constants.HAND_MILLSTONE_BLOCK, (Supplier<Class<? super TileEntityType<?>>>) () -> TileEntityType.class);
    public static final RegistryObject<TileEntityType<PressTileEntity>> PRESS_TILE = of(RESOURCE_PREFIX + Constants.PRESS_BLOCK, (Supplier<Class<? super TileEntityType<?>>>) () -> TileEntityType.class);
    public static final RegistryObject<TileEntityType<FillerTileEntity>> FILLER_TILE = of(RESOURCE_PREFIX + Constants.FILLER, (Supplier<Class<? super TileEntityType<?>>>) () -> TileEntityType.class);

    @Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
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
                    register(new HandMillstoneBlock(), Constants.HAND_MILLSTONE_BLOCK),
                    register(new MillstoneBlock(), Constants.MILLSTONE_BLOCK),
                    /*BLOCK_MANUAL_CHOPPER,
                    BLOCK_CHOPPER, BLOCK_CHOPPER_FILLER,*/
                    register(new PressBlock(), Constants.PRESS_BLOCK), register(new FillerBlock(Block.Properties.create(Material.WOOD).hardnessAndResistance(5F).sound(SoundType.WOOD), true).setHarvestLevel(ToolType.AXE, 1), Constants.PRESS_FILLER),
            };

            registry.registerAll(blocks);
        }

        private static Block register(Block block, String name) {
            return block.setRegistryName(Constants.MOD_ID, name);
        }

        /**
         * Register this mod's {@link BlockItem}s.
         *
         * @param event The event
         */
        @SubscribeEvent
        public static void registerItemBlocks(RegistryEvent.Register<Item> event) {
            HorsePower.itemGroup = new HorsePowerItemGroup();
            final BlockItem[] items = {
                    new BlockItem(BLOCK_HAND_MILLSTONE.orElseThrow(IllegalStateException::new), new Item.Properties().group(HorsePower.itemGroup)),
                    new BlockItem(BLOCK_MILLSTONE.orElseThrow(IllegalStateException::new), new Item.Properties().group(HorsePower.itemGroup)),
                    /*new ItemBlock(BLOCK_MANUAL_CHOPPER),
                    new ItemBlockDouble(BLOCK_CHOPPER, BLOCK_CHOPPER_FILLER),*/
                    new DoubleBlockItem(BLOCK_PRESS.orElseThrow(IllegalStateException::new), BLOCK_PRESS_FILLER.orElseThrow(IllegalStateException::new))
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
            reg.register(create(MillstoneTileEntity::new, BLOCK_MILLSTONE, Constants.MILLSTONE_BLOCK));
            reg.register(create(HandMillstoneTileEntity::new, BLOCK_HAND_MILLSTONE, Constants.HAND_MILLSTONE_BLOCK));
            reg.register(create(PressTileEntity::new, BLOCK_PRESS, Constants.PRESS_BLOCK));
            reg.register(create(FillerTileEntity::new, BLOCK_PRESS_FILLER, Constants.FILLER));
        }

        private static <T extends TileEntity> TileEntityType<T> create(Supplier<? extends T> factory, RegistryObject<? extends Block> block, String name) {
            return (TileEntityType<T>) TileEntityType.Builder.create(factory, block.orElseThrow(IllegalStateException::new)).build(null).setRegistryName(Constants.MOD_ID, name);
        }
    }

}
