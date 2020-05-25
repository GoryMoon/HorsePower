package se.gory_moon.horsepower;

import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.registries.ForgeRegistries;
import se.gory_moon.horsepower.blocks.ChopperBlock;
import se.gory_moon.horsepower.blocks.ManualChopperBlock;
import se.gory_moon.horsepower.blocks.FillerBlock;
import se.gory_moon.horsepower.blocks.ManualMillstoneBlock;
import se.gory_moon.horsepower.blocks.MillstoneBlock;
import se.gory_moon.horsepower.blocks.PressBlock;
import se.gory_moon.horsepower.items.DoubleBlockItem;
import se.gory_moon.horsepower.tileentity.FillerTileEntity;
import se.gory_moon.horsepower.tileentity.ManualMillstoneTileEntity;
import se.gory_moon.horsepower.tileentity.MillstoneTileEntity;
import se.gory_moon.horsepower.tileentity.PressTileEntity;
import se.gory_moon.horsepower.tileentity.ChopperTileEntity;
import se.gory_moon.horsepower.tileentity.ManualChopperTileEntity;
import se.gory_moon.horsepower.util.Constants;

import static se.gory_moon.horsepower.HorsePower.getRegistrate;
import static se.gory_moon.horsepower.util.Constants.MOD_ID;

public class Registration {

    private static final Registrate REGISTRATE = getRegistrate();
    
    /*
     * REGISTER BLOCKS AND ITEM_BLOCKS
     */

    public static final  BlockEntry<FillerBlock> PRESS_FILLER_BLOCK = REGISTRATE.object(Constants.PRESS_FILLER)
            .block(Material.WOOD, woodProperties -> { 
                FillerBlock block = new FillerBlock(woodProperties.hardnessAndResistance(5F).sound(SoundType.WOOD), true);
                block.setHarvestLevel(ToolType.AXE, 1);
                return block;
            })
            .tileEntity(FillerTileEntity::new)
            .register();
    
    public static final  BlockEntry<PressBlock> PRESS_BLOCK = REGISTRATE.object(Constants.PRESS_BLOCK)
            .block(Material.WOOD, PressBlock::new)
            .item((block,properties) -> new DoubleBlockItem(block, PRESS_FILLER_BLOCK.get(),properties))
            .build()
            .tileEntity(PressTileEntity::new)
            .register();

    public static final BlockEntry<ManualMillstoneBlock> MANUAL_MILLSTONE_BLOCK = REGISTRATE.object(Constants.MANUAL_MILLSTONE_BLOCK)
            .block(Material.ROCK, ManualMillstoneBlock::new)
            .item()
            .model((ctx, prov) -> prov.withExistingParent(ctx.getName(), new ResourceLocation(MOD_ID, "block/manual_millstone_full")))
            .build()
            .tileEntity(ManualMillstoneTileEntity::new)
            .register();
    
    public static final BlockEntry<MillstoneBlock> MILLSTONE_BLOCK = REGISTRATE.object(Constants.MILLSTONE_BLOCK)
            .block(Material.ROCK, MillstoneBlock::new)
            .item()
            .build()
            .tileEntity(MillstoneTileEntity::new)
            .register();
    
    
    public static final BlockEntry<ManualChopperBlock> MANUAL_CHOPPER_BLOCK = REGISTRATE.object(Constants.MANUAL_CHOPPER_BLOCK)
            .block(Material.WOOD, ManualChopperBlock::new)
            .item()
            .build()
            .tileEntity(ManualChopperTileEntity::new)
            .register();
    
    public static final BlockEntry<ChopperBlock> CHOPPER_BLOCK = REGISTRATE.object(Constants.CHOPPER_BLOCK)
            .block(Material.WOOD, ChopperBlock::new)
            .item()
            .build()
            .tileEntity(ChopperTileEntity::new)
            .register();  
    
    /*
     * ITEMS
     */
    
    public static final ItemEntry<Item> FLOUR = REGISTRATE.object(Constants.FLOUR_ITEM).item(Item::new).register();
    public static final ItemEntry<Item> DOUGH = REGISTRATE.object(Constants.DOUGH_ITEM).item(Item::new).register();

    /*
     * REGISTER TILE ENTITES
     */
    
    public static final RegistryEntry<TileEntityType<ManualMillstoneTileEntity>> MANUAL_MILLSTONE_TILE = MANUAL_MILLSTONE_BLOCK.getSibling(ForgeRegistries.TILE_ENTITIES);
    public static final RegistryEntry<TileEntityType<MillstoneTileEntity>> MILLSTONE_TILE = MILLSTONE_BLOCK.getSibling(ForgeRegistries.TILE_ENTITIES);
    public static final RegistryEntry<TileEntityType<ManualChopperTileEntity>> MANUAL_CHOPPER_TILE = MANUAL_CHOPPER_BLOCK.getSibling(ForgeRegistries.TILE_ENTITIES);
    public static final RegistryEntry<TileEntityType<ChopperTileEntity>> CHOPPER_TILE = CHOPPER_BLOCK.getSibling(ForgeRegistries.TILE_ENTITIES);
    public static final RegistryEntry<TileEntityType<PressTileEntity>> PRESS_TILE = PRESS_BLOCK.getSibling(ForgeRegistries.TILE_ENTITIES);
    public static final RegistryEntry<TileEntityType<FillerTileEntity>> FILLER_TILE = PRESS_FILLER_BLOCK.getSibling(ForgeRegistries.TILE_ENTITIES);

    private Registration() {
        // hidden
    }
    
    public static void init() {
        //
    }
}
