package se.gory_moon.horsepower.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import se.gory_moon.horsepower.HorsePowerMod;
import se.gory_moon.horsepower.lib.Constants;
import se.gory_moon.horsepower.lib.Reference;
import se.gory_moon.horsepower.tileentity.TileEntityFiller;
import se.gory_moon.horsepower.tileentity.TileEntityGrindstone;
import se.gory_moon.horsepower.tileentity.TileEntityHandGrindstone;

import java.util.HashSet;
import java.util.Set;

public class ModBlocks {

    public static final BlockHandGrindstone BLOCK_HAND_GRINDSTONE = new BlockHandGrindstone();
    public static final BlockGrindstone BLOCK_GRINDSTONE = new BlockGrindstone();
    /*public static final BlockChoppingBlock BLOCK_MANUAL_CHOPPER = new BlockChoppingBlock();
    public static final BlockChopper BLOCK_CHOPPER = new BlockChopper();
    public static final BlockFiller BLOCK_CHOPPER_FILLER = (BlockFiller) new BlockFiller(Material.WOOD, "chopper_", true).setHarvestLevel1("axe", 0);//.setHardness(5F).setResistance(5F);
    public static final BlockPress BLOCK_PRESS = new BlockPress();
    public static final BlockFiller BLOCK_PRESS_FILLER = (BlockFiller) new BlockFiller(Material.WOOD, "press_", true).setHarvestLevel1("axe", 1);//.setHardness(5F).setResistance(5F);*/

    public static final TileEntityType<TileEntityGrindstone> GRINDSTONE_TILE = TileEntityType.Builder.create(TileEntityGrindstone::new).build(null);
    public static final TileEntityType<TileEntityHandGrindstone> HAND_GRINDSTONE_TILE = TileEntityType.Builder.create(TileEntityHandGrindstone::new).build(null);
    public static final TileEntityType<TileEntityFiller> FILLER_TILE = TileEntityType.Builder.create(TileEntityFiller::new).build(null);

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

            //BLOCK_PRESS_FILLER.setHarvestLevel("axe", 1);
            final Block[] blocks = {BLOCK_HAND_GRINDSTONE, BLOCK_GRINDSTONE,
                    /*BLOCK_MANUAL_CHOPPER, BLOCK_CHOPPER, BLOCK_CHOPPER_FILLER,
                    BLOCK_PRESS, BLOCK_PRESS_FILLER,*/
            };

            registry.registerAll(blocks);


            IForgeRegistry<TileEntityType<?>> reg = ForgeRegistries.TILE_ENTITIES;
            reg.register(GRINDSTONE_TILE.setRegistryName(Reference.MODID, Constants.GRINDSTONE_BLOCK));
            reg.register(HAND_GRINDSTONE_TILE.setRegistryName(Reference.MODID, Constants.HAND_CHOPPING_BLOCK));
        }

        /**
         * Register this mod's {@link ItemBlock}s.
         *
         * @param event The event
         */
        @SubscribeEvent
        public static void registerItemBlocks(RegistryEvent.Register<Item> event) {
            final ItemBlock[] items = {
                new ItemBlock(BLOCK_HAND_GRINDSTONE, new Item.Builder().group(HorsePowerMod.creativeTab)),
                new ItemBlock(BLOCK_GRINDSTONE, new Item.Builder().group(HorsePowerMod.creativeTab)),
                /*new ItemBlock(BLOCK_MANUAL_CHOPPER),
                new ItemBlockDouble(BLOCK_CHOPPER, BLOCK_CHOPPER_FILLER),
                new ItemBlockDouble(BLOCK_PRESS, BLOCK_PRESS_FILLER)*/
            };

            final IForgeRegistry<Item> registry = event.getRegistry();

            for (final ItemBlock item : items) {
                registry.register(item.setRegistryName(item.getBlock().getRegistryName()));
                ITEM_BLOCKS.add(item);
            }
        }

        @SubscribeEvent
        public static void registerTileEntities(RegistryEvent.Register<TileEntityType<?> > event) {
            IForgeRegistry<TileEntityType<?>> registry = event.getRegistry();
            registry.register(GRINDSTONE_TILE.setRegistryName(Reference.MODID, Constants.GRINDSTONE_BLOCK));
            registry.register(HAND_GRINDSTONE_TILE.setRegistryName(Reference.MODID, Constants.HAND_CHOPPING_BLOCK));
            registry.register(FILLER_TILE.setRegistryName(Reference.MODID, Constants.FILLER_BLOCK));
        }
    }

    public static void registerTileEntities() {
        registerTileEntity(TileEntityHandGrindstone.class);
        registerTileEntity(TileEntityGrindstone.class);
        /*registerTileEntity(TileEntityManualChopper.class);
        registerTileEntity(TileEntityChopper.class);
        registerTileEntity(TileEntityFiller.class);
        registerTileEntity(TileEntityPress.class);*/
    }

    private static void registerTileEntity(Class<? extends TileEntity> tileEntityClass) {

        //GameRegistry.registerTileEntity(tileEntityClass, Reference.RESOURCE_PREFIX + tileEntityClass.getSimpleName().replaceFirst("TileEntity", ""));
    }

}
