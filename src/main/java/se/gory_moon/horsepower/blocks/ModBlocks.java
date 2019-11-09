package se.gory_moon.horsepower.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import se.gory_moon.horsepower.HorsePower;
import se.gory_moon.horsepower.items.DoubleBlockItem;
import se.gory_moon.horsepower.tileentity.FillerTileEntity;
import se.gory_moon.horsepower.tileentity.ManualMillstoneTileEntity;
import se.gory_moon.horsepower.tileentity.MillstoneTileEntity;
import se.gory_moon.horsepower.tileentity.PressTileEntity;
import se.gory_moon.horsepower.util.Constants;

import java.util.function.Supplier;

import static se.gory_moon.horsepower.util.Constants.MOD_ID;

public class ModBlocks {

    private static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, MOD_ID);
    public static final RegistryObject<ManualMillstoneBlock> MANUAL_MILLSTONE_BLOCK = BLOCKS.register(Constants.MANUAL_MILLSTONE_BLOCK, ManualMillstoneBlock::new);
    public static final RegistryObject<MillstoneBlock> MILLSTONE_BLOCK = BLOCKS.register(Constants.MILLSTONE_BLOCK, MillstoneBlock::new);
    public static final RegistryObject<PressBlock> PRESS_BLOCK = BLOCKS.register(Constants.PRESS_BLOCK, PressBlock::new);
    public static final RegistryObject<FillerBlock> PRESS_FILLER_BLOCK = BLOCKS.register(Constants.PRESS_FILLER, () -> new FillerBlock(Block.Properties.create(Material.WOOD).hardnessAndResistance(5F).sound(SoundType.WOOD), true).setHarvestLevel(ToolType.AXE, 1));

    /*public static final BlockChoppingBlock BLOCK_MANUAL_CHOPPER = new BlockChoppingBlock();
    public static final BlockChopper BLOCK_CHOPPER = new BlockChopper();
    public static final BlockFiller BLOCK_CHOPPER_FILLER = (BlockFiller) new BlockFiller(Material.WOOD, "chopper_", true).setHarvestLevel1("axe", 0);//.setHardness(5F).setResistance(5F);*/
    private static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, MOD_ID);
    public static final RegistryObject<BlockItem> MANUAL_MILLSTONE_ITEM = ITEMS.register(Constants.MANUAL_MILLSTONE_BLOCK, () -> new BlockItem(MANUAL_MILLSTONE_BLOCK.get(), new Item.Properties().group(HorsePower.itemGroup)));
    public static final RegistryObject<BlockItem> MILLSTONE_ITEM = ITEMS.register(Constants.MILLSTONE_BLOCK, () -> new BlockItem(MILLSTONE_BLOCK.get(), new Item.Properties().group(HorsePower.itemGroup)));
    public static final RegistryObject<BlockItem> PRESS_ITEM = ITEMS.register(Constants.PRESS_BLOCK, () -> new DoubleBlockItem(PRESS_BLOCK.get(), PRESS_FILLER_BLOCK.get()));

    private static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, MOD_ID);
    public static final RegistryObject<TileEntityType<MillstoneTileEntity>> MILLSTONE_TILE = TILE_ENTITIES.register(Constants.MILLSTONE_BLOCK, () -> create(MillstoneTileEntity::new, MILLSTONE_BLOCK));
    public static final RegistryObject<TileEntityType<ManualMillstoneTileEntity>> HAND_MILLSTONE_TILE = TILE_ENTITIES.register(Constants.MANUAL_MILLSTONE_BLOCK, () -> create(ManualMillstoneTileEntity::new, MANUAL_MILLSTONE_BLOCK));
    public static final RegistryObject<TileEntityType<PressTileEntity>> PRESS_TILE = TILE_ENTITIES.register(Constants.PRESS_BLOCK, () -> create(PressTileEntity::new, PRESS_BLOCK));
    public static final RegistryObject<TileEntityType<FillerTileEntity>> FILLER_TILE = TILE_ENTITIES.register(Constants.FILLER, () -> create(FillerTileEntity::new, PRESS_FILLER_BLOCK));

    public static void register(IEventBus event) {
        BLOCKS.register(event);
        ITEMS.register(event);
        TILE_ENTITIES.register(event);
    }

    @SuppressWarnings("unchecked")
    private static <T extends TileEntity> TileEntityType<T> create(Supplier<? extends T> factory, RegistryObject<? extends Block> block) {
        return (TileEntityType<T>) TileEntityType.Builder.create(factory, block.get()).build(null);
    }

}
