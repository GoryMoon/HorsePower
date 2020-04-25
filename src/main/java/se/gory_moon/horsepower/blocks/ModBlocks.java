package se.gory_moon.horsepower.blocks;

import com.tterrag.registrate.util.RegistryEntry;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
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
import se.gory_moon.horsepower.tileentity.TileEntityManualChopper;
import se.gory_moon.horsepower.util.Constants;

import java.util.function.Supplier;

import static se.gory_moon.horsepower.HorsePower.registrate;
import static se.gory_moon.horsepower.util.Constants.MOD_ID;

public class ModBlocks {

    private static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, MOD_ID);
    private static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, MOD_ID);
    
    //public static final RegistryObject<BlockItem> MANUAL_MILLSTONE_ITEM = ITEMS.register(Constants.MANUAL_MILLSTONE_BLOCK, () -> new BlockItem(MANUAL_MILLSTONE_BLOCK.get(), new Item.Properties().group(HorsePower.itemGroup)));
    public static final RegistryObject<MillstoneBlock> MILLSTONE_BLOCK = BLOCKS.register(Constants.MILLSTONE_BLOCK, MillstoneBlock::new);
    public static final RegistryObject<PressBlock> PRESS_BLOCK = BLOCKS.register(Constants.PRESS_BLOCK, PressBlock::new);
    public static final RegistryObject<FillerBlock> PRESS_FILLER_BLOCK = BLOCKS.register(Constants.PRESS_FILLER, () -> new FillerBlock(Block.Properties.create(Material.WOOD).hardnessAndResistance(5F).sound(SoundType.WOOD), true).setHarvestLevel(ToolType.AXE, 1));

    /*public static final BlockChoppingBlock BLOCK_MANUAL_CHOPPER = new BlockChoppingBlock();
    public static final BlockChopper BLOCK_CHOPPER = new BlockChopper();
    public static final BlockFiller BLOCK_CHOPPER_FILLER = (BlockFiller) new BlockFiller(Material.WOOD, "chopper_", true).setHarvestLevel1("axe", 0);//.setHardness(5F).setResistance(5F);*/
    public static RegistryEntry<ManualMillstoneBlock> manualMillstoneBlock;
    public static RegistryEntry<BlockChoppingBlock> choppingBlock;    
    
    public static final RegistryObject<BlockItem> MILLSTONE_ITEM = ITEMS.register(Constants.MILLSTONE_BLOCK, () -> new BlockItem(MILLSTONE_BLOCK.get(), new Item.Properties().group(HorsePower.itemGroup)));
    public static final RegistryObject<BlockItem> PRESS_ITEM = ITEMS.register(Constants.PRESS_BLOCK, () -> new DoubleBlockItem(PRESS_BLOCK.get(), PRESS_FILLER_BLOCK.get()));

    private static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, MOD_ID);
    public static final RegistryObject<TileEntityType<MillstoneTileEntity>> MILLSTONE_TILE = TILE_ENTITIES.register(Constants.MILLSTONE_BLOCK, () -> create(MillstoneTileEntity::new, MILLSTONE_BLOCK));
    public static RegistryEntry<TileEntityType<ManualMillstoneTileEntity>> manualMillstoneTile;
    public static RegistryEntry<TileEntityType<TileEntityManualChopper>> choppingBlockTile;
    public static final RegistryObject<TileEntityType<PressTileEntity>> PRESS_TILE = TILE_ENTITIES.register(Constants.PRESS_BLOCK, () -> create(PressTileEntity::new, PRESS_BLOCK));
    public static final RegistryObject<TileEntityType<FillerTileEntity>> FILLER_TILE = TILE_ENTITIES.register(Constants.FILLER, () -> create(FillerTileEntity::new, PRESS_FILLER_BLOCK));

    public static void register(IEventBus event) {
        manualMillstoneBlock = registrate().object(Constants.MANUAL_MILLSTONE_BLOCK)
                .block(Material.ROCK, ManualMillstoneBlock::new)
                .lang("Millstone")
                .blockstate((ctx, prov) -> {})
                .recipe((ctx, prov) -> ShapedRecipeBuilder.shapedRecipe(ctx.getEntry())
                        .patternLine("  S").patternLine("###").patternLine("###")
                        .key('#', Tags.Items.STONE)
                        .key('S', Tags.Items.RODS_WOODEN)
                        .addCriterion("has_stone", prov.hasItem(Tags.Items.STONE))
                        .build(prov))
                .item()
                .model((ctx, prov) -> prov.withExistingParent(ctx.getName(), new ResourceLocation(MOD_ID, "block/manual_millstone_full")))
                .build()
                .tileEntity(ManualMillstoneTileEntity::new)
                .register();
        manualMillstoneTile = manualMillstoneBlock.getSibling(ForgeRegistries.TILE_ENTITIES);

        choppingBlock = registrate().object(Constants.HAND_CHOPPING_BLOCK)
                .block(Material.WOOD, BlockChoppingBlock::new)
                .lang("Chopping Block")
                .blockstate((ctx,prov) -> {})
                .recipe((ctx,prov) -> ShapedRecipeBuilder.shapedRecipe(ctx.getEntry()).patternLine("   ").patternLine("   ").patternLine("WWW")
                        .key('W', Items.OAK_LOG)
                        .addCriterion("has_wood", prov.hasItem(Items.OAK_WOOD))
                        .build(prov))
                .item()
                .model((ctx,prov) -> prov.withExistingParent(ctx.getName(), new ResourceLocation(MOD_ID, "block/chopping_block")))
                .build()
                .tileEntity(TileEntityManualChopper::new)
                .register();
        choppingBlockTile = choppingBlock.getSibling(ForgeRegistries.TILE_ENTITIES);
        

        BLOCKS.register(event);
        ITEMS.register(event);
        TILE_ENTITIES.register(event);
    }

    @SuppressWarnings("unchecked")
    private static <T extends TileEntity> TileEntityType<T> create(Supplier<? extends T> factory, RegistryObject<? extends Block> block) {
        return (TileEntityType<T>) TileEntityType.Builder.create(factory, block.get()).build(null);
    }

}
