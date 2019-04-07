package se.gory_moon.horsepower.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntityType;
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
import se.gory_moon.horsepower.tileentity.TileEntityGrindstone;
import se.gory_moon.horsepower.tileentity.TileEntityHandGrindstone;
import se.gory_moon.horsepower.tileentity.TileEntityPress;

import java.util.HashSet;
import java.util.Set;

import static net.minecraftforge.fml.RegistryObject.of;

public class ModBlocks {

    public static final RegistryObject<BlockHandGrindstone> BLOCK_HAND_GRINDSTONE = of(Reference.RESOURCE_PREFIX + Constants.HAND_GRINDSTONE_BLOCK, () -> Block.class);
    public static final RegistryObject<BlockGrindstone> BLOCK_GRINDSTONE = of(Reference.RESOURCE_PREFIX + Constants.GRINDSTONE_BLOCK, () -> Block.class);

    /*public static final BlockChoppingBlock BLOCK_MANUAL_CHOPPER = new BlockChoppingBlock();
    public static final BlockChopper BLOCK_CHOPPER = new BlockChopper();
    public static final BlockFiller BLOCK_CHOPPER_FILLER = (BlockFiller) new BlockFiller(Material.WOOD, "chopper_", true).setHarvestLevel1("axe", 0);//.setHardness(5F).setResistance(5F);*/

    public static final RegistryObject<BlockPress> BLOCK_PRESS = of(Reference.RESOURCE_PREFIX + Constants.PRESS_BLOCK, () -> Block.class);
    public static final RegistryObject<BlockFiller> BLOCK_PRESS_FILLER = of(Reference.RESOURCE_PREFIX + "press_filler", () -> Block.class);

    public static final TileEntityType<TileEntityGrindstone> GRINDSTONE_TILE = TileEntityType.Builder.create(TileEntityGrindstone::new).build(null);
    public static final TileEntityType<TileEntityHandGrindstone> HAND_GRINDSTONE_TILE = TileEntityType.Builder.create(TileEntityHandGrindstone::new).build(null);
    public static final TileEntityType<TileEntityPress> PRESS_TILE = TileEntityType.Builder.create(TileEntityPress::new).build(null);
    public static final TileEntityType<TileEntityFiller> FILLER_TILE = TileEntityType.Builder.create(TileEntityFiller::new).build(null);

    @Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
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

            //BLOCK_PRESS_FILLER.setHarvestLevel("axe", 1);
            final Block[] blocks = {
                    new BlockHandGrindstone(),
                    new BlockGrindstone(),
                    /*BLOCK_MANUAL_CHOPPER,
                    BLOCK_CHOPPER, BLOCK_CHOPPER_FILLER,*/
                    new BlockPress(), new BlockFiller(Block.Properties.create(Material.WOOD).hardnessAndResistance(5F), "press_", true)//.setHarvestLevel1("axe", 1),
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
            HorsePowerMod.itemGroup = new HorsePowerItemGroup();
            final ItemBlock[] items = {
                new ItemBlock(BLOCK_HAND_GRINDSTONE.orElse(null), new Item.Properties().group(HorsePowerMod.itemGroup)),
                new ItemBlock(BLOCK_GRINDSTONE.orElse(null), new Item.Properties().group(HorsePowerMod.itemGroup)),
                /*new ItemBlock(BLOCK_MANUAL_CHOPPER),
                new ItemBlockDouble(BLOCK_CHOPPER, BLOCK_CHOPPER_FILLER),*/
                new ItemBlockDouble(BLOCK_PRESS.orElse(null), BLOCK_PRESS_FILLER.orElse(null))
            };

            final IForgeRegistry<Item> registry = event.getRegistry();

            for (final ItemBlock item : items) {
                registry.register(item.setRegistryName(item.getBlock().getRegistryName()));
                ITEM_BLOCKS.add(item);
            }
        }

        @SubscribeEvent
        public static void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event) {
            IForgeRegistry<TileEntityType<?>> reg = event.getRegistry();
            reg.register(GRINDSTONE_TILE.setRegistryName(Reference.MODID, Constants.GRINDSTONE_BLOCK));
            reg.register(HAND_GRINDSTONE_TILE.setRegistryName(Reference.MODID, Constants.HAND_CHOPPING_BLOCK));
            reg.register(PRESS_TILE.setRegistryName(Reference.MODID, Constants.PRESS_BLOCK));
            reg.register(FILLER_TILE.setRegistryName(Reference.MODID, Constants.FILLER_BLOCK));
        }
    }

}
