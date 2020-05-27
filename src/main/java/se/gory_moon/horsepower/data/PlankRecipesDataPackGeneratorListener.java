package se.gory_moon.horsepower.data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.util.Constants;
import static se.gory_moon.horsepower.HorsePower.LOGGER;

public class PlankRecipesDataPackGeneratorListener implements ISelectiveResourceReloadListener {

    private static final String DATA = "/data/";
    private static final String CHOPPING_BLOCK_RECIPE_PATH = DATA + Constants.MOD_ID + "/recipes/chopping_block";
    private FMLServerAboutToStartEvent event;

    public PlankRecipesDataPackGeneratorListener(FMLServerAboutToStartEvent event) {
        this.event = event;
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
        LOGGER.info("onResourceManagerReload " + event);
        if (event != null && event.getServer() != null
                && Boolean.TRUE.equals(Configs.SERVER.plankDataPackGeneration.get())) {
            
            String dataPackPath = getDataPackPath(event.getServer());
            if(prepareHorsePowerDataPack(dataPackPath)) { //data pack created or was already existing
                if(clearDirectory(dataPackPath + "/" +Constants.MOD_ID+  "/"+ CHOPPING_BLOCK_RECIPE_PATH)) {
                    //generate recipe data
                    generate(dataPackPath + "/" +Constants.MOD_ID+  "/"+ CHOPPING_BLOCK_RECIPE_PATH);
                }
            }
        }
    }

    private static void generate(String choppingDataPackPath) {
        ResourceLocation tagLog = ItemTags.LOGS.getId();
        Map<ResourceLocation, Item> logs = ForgeRegistries.ITEMS.getEntries().stream().filter(e -> e.getValue().getTags().contains(tagLog)).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
        
        //the log to plank map
        Map<Item,Item> logPlankMap = new HashMap<>();
        logs.entrySet().forEach(entry -> {
            ResourceLocation logResourceLocation = entry.getKey();
            String possiblePlankName = logResourceLocation.getPath().replace("stripped_", "").replace("_log", "").replace("_wood", "").concat("_planks");
            ResourceLocation plankSearch = new ResourceLocation(logResourceLocation.getNamespace(), possiblePlankName);
            Item foundPlank = ForgeRegistries.ITEMS.getValue(plankSearch);
            if(foundPlank != null && !Items.AIR.equals(foundPlank))
            {
                logPlankMap.put(entry.getValue(), foundPlank);
            }
        });
        
        logPlankMap.entrySet().forEach(entry -> LOGGER.info(entry.getKey().getRegistryName() + " -> " +entry.getValue().getRegistryName()));
        Set<Entry<Item, Item>> entrySet = logPlankMap.entrySet();
        for (Entry<Item, Item> logPlankEntry : entrySet) {
            String fileNameWithoutSuffix = logPlankEntry.getKey().getRegistryName().getNamespace() + 
                                "_" + 
                                logPlankEntry.getValue().getRegistryName().getPath() + 
                                "_from_"+ 
                                logPlankEntry.getKey().getRegistryName().getPath();
            String recipeId = Constants.MOD_ID+ ":chopping/" + fileNameWithoutSuffix;
            ChoppingRecipeBuilder.choppingRecipe(logPlankEntry.getValue(), 1, Ingredient.fromItems(logPlankEntry.getKey()))
                .addCriterion("has_oak", hasItem(Items.OAK_LOG))
                .build(recipe -> serializeAndSave(recipe, choppingDataPackPath, fileNameWithoutSuffix), recipeId);
        }
        
    }
    
    /**
     * Generates JSON files for the DataPack.
     */
    private static void serializeAndSave(IFinishedRecipe recipe, String path, String fileNameWithoutSuffix) {
        File file = new File(path + "/" + fileNameWithoutSuffix + ".json");

        try (FileWriter fileWriter = new FileWriter(file.getPath());){
            fileWriter.write(recipe.getRecipeJson().toString());
        } catch (IOException e) {
            LOGGER.debug("DataPack Generator - \"" + file.getPath() + "\" could not created" ); //NOSONAR
            LOGGER.debug(e.getLocalizedMessage());
        }

    }
    
    
    private static InventoryChangeTrigger.Instance hasItem(IItemProvider... providers) {
        return hasItem(Arrays.stream(providers).map(provider -> ItemPredicate.Builder.create().item(provider).build()).toArray(ItemPredicate[]::new));
    }
    
    private static InventoryChangeTrigger.Instance hasItem(ItemPredicate... predicates) {
        return new InventoryChangeTrigger.Instance(MinMaxBounds.IntBound.UNBOUNDED, MinMaxBounds.IntBound.UNBOUNDED, MinMaxBounds.IntBound.UNBOUNDED, predicates);
     }

    private static String getDataPackPath(MinecraftServer server) {
        String serverPath = server.getDataDirectory().getPath();
        return server.isDedicatedServer() ? (serverPath + "/" + server.getFolderName() + "/datapacks") : (serverPath + "/saves/" + server.getFolderName() + "/datapacks");
    }

    /**
     * Clears the existing data in Data Pack, useful on reload or recipe changes.
     */
    private static boolean clearDirectory(String path) {
        try {
            FileUtils.cleanDirectory(new File(path));
            return true;
        } catch (IOException e) {
            LOGGER.debug("DataPack Generator - \"" + path + "\" could not be cleared" ); //NOSONAR
            LOGGER.debug(e.getLocalizedMessage());
        } catch (IllegalArgumentException e) {
            LOGGER.debug("DataPack Generator - \"" + path + "\" could not be cleared because path is not a directory or does not exist" ); //NOSONAR
            LOGGER.debug(e.getLocalizedMessage());
        }
        return false;
    }
    
    /**
     * Create a Horse Power Data Pack in your current world folder
     * @return boolean if the data pack folder structure was created with success
     */
    public static boolean prepareHorsePowerDataPack(String worldFolderPath){

        String path = worldFolderPath+ "/"+ Constants.MOD_ID;
        
        if(createFolder(path) 
                && createPackMCMeta(path + "/pack.mcmeta") 
                && createFolder(path +"/data") 
                && createFolder(path + DATA + Constants.MOD_ID) 
                && createFolder(path + DATA + Constants.MOD_ID + "/recipes") 
                && createFolder(path + CHOPPING_BLOCK_RECIPE_PATH))
        {
                LOGGER.debug("DataPack Generator - Folder structure prepared successfully");
                return true;
        }
        
        LOGGER.debug("DataPack Generator - Error in folder generation");
        return false;
    }

    private static boolean createPackMCMeta(String packMcMetaFilePath) {
             File file = new File(packMcMetaFilePath);
        
        try {
                if(file.createNewFile()){
                    LOGGER.debug("DataPack Generator - \"" + file + "\" created" ); //NOSONAR
                }else{
                    LOGGER.debug("DataPack Generator - \"" + file + "\" already exists" ); //NOSONAR
                }
                
                if(file.length() == 0l)
                {
                    writePackMCMetaFileData(file);
                }
                else{
                    LOGGER.debug("DataPack Generator - \"" + file + "\" has content, seems ok" ); //NOSONAR
                }
                
            } catch (IOException e) {
                LOGGER.debug("DataPack Generator - \"" + file + "\" failed to create file " ); //NOSONAR
                LOGGER.debug(e.getLocalizedMessage());
                return false;
            }
        return true;
    }

    private static void writePackMCMetaFileData(File file) throws IOException {
        try(FileWriter fileWriter = new FileWriter(file.getPath());) {
            //create JSON content
            JsonObject pack = new JsonObject();
            pack.add("description",  new JsonPrimitive(Constants.MOD_ID + " data pack content"));
            pack.add("pack_format",  new JsonPrimitive(Integer.valueOf(4)));

            JsonObject mcmeta = new JsonObject();
            mcmeta.add("pack", pack);
            
            fileWriter.write(mcmeta.toString());
            LOGGER.debug("DataPack Generator - \"" + file.getPath() + "\" successful filled file contents" ); //NOSONAR
        } catch (IOException e) {
            LOGGER.debug("DataPack Generator - \"" + file.getPath() + "\" IOException " ); //NOSONAR
            throw e;
        }
    }

    private static boolean createFolder(String path) {
        File folder = new File(path);
        if(folder.exists()) {
            LOGGER.debug("DataPack Generator - \"" + path + "\" already exists " ); //NOSONAR
            return true;
        }
        if(folder.mkdir()){
            LOGGER.debug("DataPack Generator - \"" + path + "\" created" ); //NOSONAR
            return true;
        }
        LOGGER.debug("DataPack Generator - \"" + path + "\" folder creation failed"); //NOSONAR
        return false;
    }
    
}
