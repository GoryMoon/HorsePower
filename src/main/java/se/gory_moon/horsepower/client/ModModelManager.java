package se.gory_moon.horsepower.client;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import se.gory_moon.horsepower.util.Constants;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Constants.MOD_ID)
public class ModModelManager {

    public static final ModModelManager INSTANCE = new ModModelManager();
    private static final ResourceLocation MODEL_ChoppingBlock = new ResourceLocation("horsepower", "block/chopper");
    private static final ResourceLocation MODEL_ManualChoppingBlock = new ResourceLocation("horsepower", "block/chopping_block");
    private final Set<Item> itemsRegistered = new HashSet<>();

    public ModModelManager() {}

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

    public static ModelResourceLocation getModel(String resource) {
        return new ModelResourceLocation(Constants.MOD_ID + ":" + resource, "inventory");
    }

    //TODO reactivate baked models ?
//    public static void replaceChoppingModel(ModelResourceLocation modelVariantLocation, ResourceLocation modelLocation, ModelBakeEvent event) {
//        try {
//            IModel model = ModelLoaderRegistry.getModel(modelLocation);
//            IBakedModel standard = event.getModelRegistry().get(modelVariantLocation);
//            if (standard != null) {
//                /*IBakedModel finalModel = new BakedChopperModel(standard, model, DefaultVertexFormats.BLOCK);
//
//                event.getModelRegistry().put(modelVariantLocation, finalModel);*/
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
