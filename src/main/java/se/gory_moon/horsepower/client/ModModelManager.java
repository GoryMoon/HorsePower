package se.gory_moon.horsepower.client;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import se.gory_moon.horsepower.lib.Reference;

import java.util.HashSet;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Reference.MODID)
public class ModModelManager {

    public static final ModModelManager INSTANCE = new ModModelManager();
    private final Set<Item> itemsRegistered = new HashSet<>();
    private static final ResourceLocation MODEL_ChoppingBlock = new ResourceLocation("horsepower", "block/chopper");
    private static final ResourceLocation MODEL_ManualChoppingBlock = new ResourceLocation("horsepower", "block/chopping_block");

    public ModModelManager() {
    }

    @SubscribeEvent
    public static void onModelBake(ModelBakeEvent event) {
        /*replaceChoppingModel(new ModelResourceLocation("horsepower:block/chopper", "facing=north,part=base"), MODEL_ChoppingBlock, event);
        replaceChoppingModel(new ModelResourceLocation("horsepower:block/chopper", "facing=south,part=base"), MODEL_ChoppingBlock, event);
        replaceChoppingModel(new ModelResourceLocation("horsepower:block/chopper", "facing=west,part=base"), MODEL_ChoppingBlock, event);
        replaceChoppingModel(new ModelResourceLocation("horsepower:block/chopper", "facing=east,part=base"), MODEL_ChoppingBlock, event);
        replaceChoppingModel(new ModelResourceLocation("horsepower:block/chopping_block"), MODEL_ManualChoppingBlock, event);

        event.getModelRegistry().put(getModel("block/chopper"), event.getModelRegistry().get(new ModelResourceLocation("horsepower:block/chopper", "facing=north,part=base")));
        event.getModelRegistry().put(getModel("block/chopper"), event.getModelRegistry().get(new ModelResourceLocation("horsepower:block/chopper", "facing=south,part=base")));
        event.getModelRegistry().put(getModel("block/chopper"), event.getModelRegistry().get(new ModelResourceLocation("horsepower:block/chopper", "facing=west,part=base")));
        event.getModelRegistry().put(getModel("block/chopper"), event.getModelRegistry().get(new ModelResourceLocation("horsepower:block/chopper", "facing=east,part=base")));
        event.getModelRegistry().put(getModel("block/chopping_block"), event.getModelRegistry().get(new ModelResourceLocation("horsepower:block/chopping_block")));*/
    }

    @SubscribeEvent
    public static void registerAllModels(ModelRegistryEvent event) {
        INSTANCE.registerBlockModels();
        INSTANCE.registerItemModels();
    }

    private void registerBlockModels() {
        //ModBlocks.RegistrationHandler.ITEM_BLOCKS.stream().filter(item -> !itemsRegistered.contains(item)).forEach(this::registerItemModel);
    }

    /**
     * Register this mod's {@link Item} models.
     */
    private void registerItemModels() {
        // Then register items with default model names
        //ModItems.RegistrationHandler.ITEMS.stream().filter(item -> !itemsRegistered.contains(item)).forEach(this::registerItemModel);
    }


    /**
     * A {@link StateMapperBase} used to create property strings.
     */
    /*private final StateMapperBase propertyStringMapper = new StateMapperBase() {
        @Override
        protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
            return new ModelResourceLocation("minecraft:air");
        }
    };*/

    /**
     * Register a single model for the {@link Block}'s {@link Item}.
     * <p>
     * Uses the registry name as the domain/path and the {@link BlockState} as the variant.
     *
     * @param state The state to use as the variant
     */
    private void registerBlockItemModel(BlockState state) {
        final Block block = state.getBlock();
        final Item item = Item.getItemFromBlock(block);

        if (item != null) {
            //registerItemModel(item, new ModelResourceLocation(block.getRegistryName(), propertyStringMapper.getPropertyString(state.getProperties())));
        }
    }

    /**
     * Register a model for a metadata value of the {@link Block}'s {@link Item}.
     * <p>
     * Uses the registry name as the domain/path and the {@link BlockState} as the variant.
     *
     * @param state    The state to use as the variant
     * @param metadata The items metadata to register the model for
     */
    private void registerBlockItemModelForMeta(BlockState state, int metadata) {
        final Item item = Item.getItemFromBlock(state.getBlock());

        if (item != null) {
            //registerItemModelForMeta(item, metadata, propertyStringMapper.getPropertyString(state.getProperties()));
        }
    }

    /*
     * Register a model for each metadata value of the {@link Block}'s {@link Item} corresponding to the values of an {@link IProperty}.
     * <p>
     * For each value:
     * <li>The domain/path is the registry name</li>
     * <li>The variant is {@code baseState} with the {@link IProperty} set to the value</li>
     * <p>
     * The {@code getMeta} function is used to get the metadata of each value.
     *
     * @param baseState The base state to use for the variant
     * @param property  The property whose values should be used
     * @param getMeta   A function to get the metadata of each value
     * @param <T>       The value type
     */
    /*private <T extends Comparable<T>> void registerVariantBlockItemModels(IBlockState baseState, IProperty<T> property, ToIntFunction<T> getMeta) {
        property.getAllowedValues().forEach(value -> registerBlockItemModelForMeta(baseState.withProperty(property, value), getMeta.applyAsInt(value)));
    }

    private <T extends VariantItem> void registerVariantItems(T variant, String variantName) {
        variant.getMetas().forEach(value -> registerItemModelForMeta(variant, value, variantName + "=" + variant.getVariant(value)));
    }
*/

    /**
     * Register a single model for an {@link Item}.
     * <p>
     * Uses the registry name as the domain/path and {@code "inventory"} as the variant.
     *
     * @param item The Item
     */
    private void registerItemModel(Item item) {
        registerItemModel(item, item.getRegistryName().toString());
    }

    /**
     * Register a single model for an {@link Item}.
     * <p>
     * Uses {@code modelLocation} as the domain/path and {@link "inventory"} as the variant.
     *
     * @param item          The Item
     * @param modelLocation The model location
     */
    private void registerItemModel(Item item, String modelLocation) {
        final ModelResourceLocation fullModelLocation = new ModelResourceLocation(modelLocation, "inventory");
        registerItemModel(item, fullModelLocation);
    }

    /**
     * Register a single model for an {@link Item}.
     * <p>
     * Uses {@code fullModelLocation} as the domain, path and variant.
     *
     * @param item              The Item
     * @param fullModelLocation The full model location
     */
    private void registerItemModel(Item item, ModelResourceLocation fullModelLocation) {
        //Minecraft.getInstance().getItemRenderer().getItemModelMesher().register(item, fullModelLocation);
        itemsRegistered.add(item);
        //registerItemModel(item, MeshDefinitionFix.create(stack -> fullModelLocation));
    }

    /**
     * Register an {@link ItemMeshDefinition} for an {@link Item}.
     *
     * @param item           The Item
     * @param meshDefinition The ItemMeshDefinition
     */
    /*private void registerItemModel(Item item, ItemMeshDefinition meshDefinition) {
        itemsRegistered.add(item);
        //ModelLoader.setCustomMeshDefinition(item, meshDefinition);
    }*/

    /**
     * Register a model for a metadata value an {@link Item}.
     * <p>
     * Uses the registry name as the domain/path and {@code variant} as the variant.
     *
     * @param item     The Item
     * @param metadata The metadata
     * @param variant  The variant
     */
    private void registerItemModelForMeta(Item item, int metadata, String variant) {
        registerItemModelForMeta(item, metadata, new ModelResourceLocation(item.getRegistryName(), variant));
    }

    /**
     * Register a model for a metadata value of an {@link Item}.
     * <p>
     * Uses {@code modelResourceLocation} as the domain, path and variant.
     *
     * @param item                  The Item
     * @param metadata              The metadata
     * @param modelResourceLocation The full model location
     */
    private void registerItemModelForMeta(Item item, int metadata, ModelResourceLocation modelResourceLocation) {
        itemsRegistered.add(item);
        //ModelLoader.setCustomModelResourceLocation(item, metadata, modelResourceLocation);
    }

    public static ModelResourceLocation getModel(String resource) {
        return new ModelResourceLocation(Reference.MODID + ":" + resource, "inventory");
    }

    public static void replaceChoppingModel(ModelResourceLocation modelVariantLocation, ResourceLocation modelLocation, ModelBakeEvent event) {
        try {
            IModel model = ModelLoaderRegistry.getModel(modelLocation);
            IBakedModel standard = event.getModelRegistry().get(modelVariantLocation);
            if (standard != null) {
                /*IBakedModel finalModel = new BakedChopperModel(standard, model, DefaultVertexFormats.BLOCK);

                event.getModelRegistry().put(modelVariantLocation, finalModel);*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
