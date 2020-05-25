package se.gory_moon.horsepower.blocks;

import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.Block.Properties;
import net.minecraft.block.material.Material;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
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
import se.gory_moon.horsepower.tileentity.TileEntityChopper;
import se.gory_moon.horsepower.tileentity.TileEntityManualChopper;
import se.gory_moon.horsepower.util.Constants;

import java.util.function.Supplier;

import static se.gory_moon.horsepower.HorsePower.registrate;
import static se.gory_moon.horsepower.util.Constants.MOD_ID;

public class ModBlocks {

//    private static final Registrate REGISTRATE = registrate();
    
    public static BlockEntry<MillstoneBlock> millstoneBlock;
    
    public static  BlockEntry<PressBlock> pressBlock;
    public static  BlockEntry<FillerBlock> pressFillerBlock;

    public static BlockEntry<ManualMillstoneBlock> manualMillstoneBlock;
    public static BlockEntry<BlockChoppingBlock> choppingBlock;    
    public static BlockEntry<BlockChopper> chopperBlock;    
    
    public static RegistryEntry<TileEntityType<?>> millstoneTile;
    public static RegistryEntry<TileEntityType<?>> manualMillstoneTile;
    public static RegistryEntry<TileEntityType<?>> choppingBlockTile;
    public static RegistryEntry<TileEntityType<?>> chopperTile;
    public static  RegistryEntry<TileEntityType<PressTileEntity>> pressTile;
    public static  RegistryEntry<TileEntityType<FillerTileEntity>> fillerTile;

    public static void register(IEventBus event) {
        pressFillerBlock = registrate().object(Constants.PRESS_FILLER)
                .block(Material.WOOD, woodProperties -> { 
                    FillerBlock block = new FillerBlock(woodProperties.hardnessAndResistance(5F).sound(SoundType.WOOD), true);
                    block.setHarvestLevel(ToolType.AXE, 1);
                    return block;
                })
                .tileEntity(FillerTileEntity::new)
                .register();
        fillerTile = pressFillerBlock.getSibling(ForgeRegistries.TILE_ENTITIES);
        
        pressBlock = registrate().object(Constants.PRESS_BLOCK)
                .block(Material.WOOD, PressBlock::new)
                .item((block,properties) -> new DoubleBlockItem(block, pressFillerBlock.get()))
                .build()
                .tileEntity(PressTileEntity::new)
                .register();
        pressTile = pressBlock.getSibling(ForgeRegistries.TILE_ENTITIES);
        
        millstoneBlock = registrate().object(Constants.MILLSTONE_BLOCK)
                .block(Material.ROCK, MillstoneBlock::new)
                .item()
                .build()
                .tileEntity(MillstoneTileEntity::new)
                .register();
        millstoneTile = millstoneBlock.getSibling(ForgeRegistries.TILE_ENTITIES);
        
        
        manualMillstoneBlock = registrate().object(Constants.MANUAL_MILLSTONE_BLOCK)
                .block(Material.ROCK, ManualMillstoneBlock::new)
                .item()
                .model((ctx, prov) -> prov.withExistingParent(ctx.getName(), new ResourceLocation(MOD_ID, "block/manual_millstone_full")))
                .build()
                .tileEntity(ManualMillstoneTileEntity::new)
                .register();
        manualMillstoneTile = manualMillstoneBlock.getSibling(ForgeRegistries.TILE_ENTITIES);

        choppingBlock = registrate().object(Constants.HAND_CHOPPING_BLOCK)
                .block(Material.WOOD, BlockChoppingBlock::new)
                .item()
                .build()
                .tileEntity(TileEntityManualChopper::new)
                .register();
        choppingBlockTile = choppingBlock.getSibling(ForgeRegistries.TILE_ENTITIES);
        
        chopperBlock = registrate().object(Constants.CHOPPER_BLOCK)
                .block(Material.WOOD, BlockChopper::new)
                .item()
                .build()
                .tileEntity(TileEntityChopper::new)
                .register();
        chopperTile = chopperBlock.getSibling(ForgeRegistries.TILE_ENTITIES);
    }

    private static void emptyBlockState(@SuppressWarnings("unused") DataGenContext<Block, ? extends Block> ctx, @SuppressWarnings("unused") RegistrateBlockstateProvider blockStateProvider) {
        //
    }

    @SuppressWarnings("unchecked")
    private static <T extends TileEntity> TileEntityType<T> create(Supplier<? extends T> factory, RegistryObject<? extends Block> block) {
        return (TileEntityType<T>) TileEntityType.Builder.create(factory, block.get()).build(null);
    }

    private ModBlocks() {
        // hidden
    }
    
}
