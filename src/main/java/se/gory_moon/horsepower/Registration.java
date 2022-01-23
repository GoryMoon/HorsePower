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
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.registries.ForgeRegistries;
import se.gory_moon.horsepower.blocks.ChopperBlock;
import se.gory_moon.horsepower.blocks.FillerBlock;
import se.gory_moon.horsepower.blocks.ManualChopperBlock;
import se.gory_moon.horsepower.blocks.ManualMillstoneBlock;
import se.gory_moon.horsepower.blocks.MillstoneBlock;
import se.gory_moon.horsepower.blocks.PressBlock;
import se.gory_moon.horsepower.client.model.modelvariants.ChopperModels;
import se.gory_moon.horsepower.client.model.modelvariants.ManualMillstoneModels;
import se.gory_moon.horsepower.client.model.modelvariants.MillstoneModels;
import se.gory_moon.horsepower.client.model.modelvariants.PressModels;
import se.gory_moon.horsepower.client.renderer.ChopperRenderTileEntity;
import se.gory_moon.horsepower.client.renderer.ChoppingTileEntityBlockRender;
import se.gory_moon.horsepower.client.renderer.FillerTileEntityRender;
import se.gory_moon.horsepower.client.renderer.ManualMillstoneTileEntityRender;
import se.gory_moon.horsepower.client.renderer.MillstoneTileEntityRender;
import se.gory_moon.horsepower.client.renderer.PressTileEntityRender;
import se.gory_moon.horsepower.items.DoubleBlockItem;
import se.gory_moon.horsepower.tileentity.ChopperTileEntity;
import se.gory_moon.horsepower.tileentity.FillerTileEntity;
import se.gory_moon.horsepower.tileentity.ManualChopperTileEntity;
import se.gory_moon.horsepower.tileentity.ManualMillstoneTileEntity;
import se.gory_moon.horsepower.tileentity.MillstoneTileEntity;
import se.gory_moon.horsepower.tileentity.PressTileEntity;
import se.gory_moon.horsepower.util.Constants;
import se.gory_moon.horsepower.util.HPUtils;

import static se.gory_moon.horsepower.HorsePower.getRegistrate;
import static se.gory_moon.horsepower.util.Constants.MOD_ID;

public class Registration {

    private static final Registrate REGISTRATE = getRegistrate();
    
    /*
     * REGISTER BLOCKS AND ITEM_BLOCKS
     */

    public static final  BlockEntry<FillerBlock> WOODEN_FILLER_BLOCK = REGISTRATE.object(Constants.WOODEN_FILLER_BLOCK)
            .block(Material.WOOD, woodProperties -> {
                FillerBlock block = new FillerBlock(woodProperties.hardnessAndResistance(5F).sound(SoundType.WOOD), true);
                block.setHarvestLevel(ToolType.AXE, 1);
                return block;
            })
            .blockstate((ctx, provider) -> provider.simpleBlock(ctx.get(), new ModelFile.UncheckedModelFile("block/generated")))
            .tileEntity(FillerTileEntity::new)
            .renderer(() -> FillerTileEntityRender::new)
                .build()
            .register();
    
    public static final  BlockEntry<PressBlock> PRESS_BLOCK = REGISTRATE.object(Constants.PRESS_BLOCK)
            .block(Material.WOOD, PressBlock::new)
            .blockstate((ctx, provider) -> {
                provider.horizontalBlock(ctx.get(), state -> {
                    if (state.get(PressBlock.PART).equals(PressModels.TOP))
                        return provider.models().getExistingFile(HPUtils.rl("press"));
                    else
                        return provider.models().getExistingFile(HPUtils.rl("press_top"));
                });
            })
            .item((block, properties) -> new DoubleBlockItem(block, WOODEN_FILLER_BLOCK.get(), properties))
            .build()
            .tileEntity(PressTileEntity::new)
            .renderer(() -> PressTileEntityRender::new)
                .build()
            .register();

    public static final BlockEntry<ManualMillstoneBlock> MANUAL_MILLSTONE_BLOCK = REGISTRATE.object(Constants.MANUAL_MILLSTONE_BLOCK)
            .block(Material.ROCK, ManualMillstoneBlock::new)
            .blockstate((ctx, provider) -> {
                provider.horizontalBlock(ctx.get(), state -> {
                    if (state.get(ManualMillstoneBlock.PART).equals(ManualMillstoneModels.BASE))
                        return provider.models().getExistingFile(HPUtils.rl("manual_millstone"));
                    else
                        return provider.models().getExistingFile(HPUtils.rl("manual_millstone_center"));
                });
            })
            .item()
            .model((ctx, prov) -> prov.withExistingParent(ctx.getName(), new ResourceLocation(MOD_ID, "block/manual_millstone_full")))
            .build()
            .tileEntity(ManualMillstoneTileEntity::new)
            .renderer(() -> ManualMillstoneTileEntityRender::new)
                .build()
            .register();
    
    public static final BlockEntry<MillstoneBlock> MILLSTONE_BLOCK = REGISTRATE.object(Constants.MILLSTONE_BLOCK)
            .block(Material.ROCK, MillstoneBlock::new)
            .blockstate((ctx, provider) -> {
                provider.getVariantBuilder(ctx.get())
                        .partialState().with(MillstoneBlock.PART, MillstoneModels.BASE)
                        .modelForState().modelFile(provider.models().getExistingFile(HPUtils.rl("millstone"))).addModel()
                        .partialState().with(MillstoneBlock.PART, MillstoneModels.FILLED)
                        .modelForState().modelFile(provider.models().getExistingFile(HPUtils.rl("millstone_filled"))).addModel();
            })
            .item()
            .build()
            .tileEntity(MillstoneTileEntity::new)
            .renderer(() -> MillstoneTileEntityRender::new)
                .build()
            .register();
    
    
    public static final BlockEntry<ManualChopperBlock> MANUAL_CHOPPER_BLOCK = REGISTRATE.object(Constants.MANUAL_CHOPPER_BLOCK)
            .block(Material.WOOD, ManualChopperBlock::new)
            .blockstate((ctx, provider) -> provider
                    .getVariantBuilder(ctx.get())
                    .partialState()
                    .setModels(new ConfiguredModel(provider.models().getExistingFile(HPUtils.rl("manual_chopper")))))
            .item()
            .build()
            .tileEntity(ManualChopperTileEntity::new)
            .renderer(() -> ChoppingTileEntityBlockRender::new)
                .build()
            .register();
    
    public static final BlockEntry<ChopperBlock> CHOPPER_BLOCK = REGISTRATE.object(Constants.CHOPPER_BLOCK)
            .block(Material.WOOD, ChopperBlock::new)
            .blockstate((ctx, provider) -> {
                provider.horizontalBlock(ctx.get(), state -> {
                    if (state.get(ChopperBlock.PART).equals(ChopperModels.BASE))
                        return provider.models().getExistingFile(HPUtils.rl("chopper"));
                    else
                        return provider.models().getExistingFile(HPUtils.rl("chopper_blade"));
                });
            })
            .item((block, properties) -> new DoubleBlockItem(block, WOODEN_FILLER_BLOCK.get(), properties))
            .build()
            .tileEntity(ChopperTileEntity::new)
            .renderer(() -> ChopperRenderTileEntity::new)
                .build()
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
    public static final RegistryEntry<TileEntityType<FillerTileEntity>> FILLER_TILE = WOODEN_FILLER_BLOCK.getSibling(ForgeRegistries.TILE_ENTITIES);

    private Registration() {
        // hidden
    }
    
    public static void init() {
        //
    }
}
