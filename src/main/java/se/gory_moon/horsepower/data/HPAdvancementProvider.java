package se.gory_moon.horsepower.data;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import se.gory_moon.horsepower.advancements.UseHorseMillstoneTrigger;
import se.gory_moon.horsepower.advancements.UsePressTrigger;
import se.gory_moon.horsepower.blocks.ModBlocks;
import se.gory_moon.horsepower.items.ModItems;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Consumer;

public class HPAdvancementProvider implements IDataProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    private final DataGenerator generator;

    public HPAdvancementProvider(DataGenerator generator) {
        this.generator = generator;
    }

    private static Path getPath(Path path, Advancement advancement) {
        return path.resolve("data/" + advancement.getId().getNamespace() + "/advancements/" + advancement.getId().getPath() + ".json");
    }

    /**
     * Performs this provider's action.
     */
    @Override
    public void act(DirectoryCache cache) {
        Path path = this.generator.getOutputFolder();
        Set<ResourceLocation> set = Sets.newHashSet();
        Consumer<Advancement> consumer = (advancement) -> {
            if (!set.add(advancement.getId())) {
                throw new IllegalStateException("Duplicate advancement " + advancement.getId());
            } else {
                Path path1 = getPath(path, advancement);

                try {
                    IDataProvider.save(GSON, cache, advancement.copy().serialize(), path1);
                } catch (IOException ioexception) {
                    LOGGER.error("Couldn't save advancement {}", path1, ioexception);
                }

            }
        };
        registerAdvancements(consumer);

    }

    /**
     * Gets a name for this provider, to use in logging.
     */
    @Override
    public String getName() {
        return "Horsepower Advancements";
    }

    private void registerAdvancements(Consumer<Advancement> consumer) {
        Advancement root = Advancement.Builder.builder().withDisplay(ModBlocks.manualMillstoneBlock.get(), new TranslationTextComponent("advancements.horsepower.root.title"), new TranslationTextComponent("advancements.horsepower.root.description"), new ResourceLocation("textures/gui/advancements/backgrounds/stone.png"), FrameType.TASK, false, false, false).withRequirementsStrategy(IRequirementsStrategy.OR).withCriterion("get_millstone", InventoryChangeTrigger.Instance.forItems(ModBlocks.manualMillstoneBlock.orElse(null))).withCriterion("get_wheat", InventoryChangeTrigger.Instance.forItems(Items.WHEAT)).register(consumer, "horsepower:horsepower/root");
        Advancement getFlour = Advancement.Builder.builder().withParent(root).withDisplay(ModItems.FLOUR.get(), new TranslationTextComponent("advancements.horsepower.flour.title"), new TranslationTextComponent("advancements.horsepower.flour.description"), null, FrameType.TASK, true, true, false).withCriterion("get_flour", InventoryChangeTrigger.Instance.forItems(ModItems.FLOUR.orElse(null))).register(consumer, "horsepower:horsepower/get_flour");
        Advancement use_hpgrindstone = Advancement.Builder.builder().withParent(root).withDisplay(ModBlocks.MILLSTONE_BLOCK.get(), new TranslationTextComponent("advancements.horsepower.millstone.title"), new TranslationTextComponent("advancements.horsepower.millstone.description"), null, FrameType.TASK, true, true, false).withCriterion("use_millstone", UseHorseMillstoneTrigger.Instance.userMillstone()).register(consumer, "horsepower:horsepower/use_hpgrindstone");
        Advancement use_hppress = Advancement.Builder.builder().withParent(root).withDisplay(ModBlocks.PRESS_BLOCK.get(), new TranslationTextComponent("advancements.horsepower.press.title"), new TranslationTextComponent("advancements.horsepower.press.description"), null, FrameType.TASK, true, true, false).withCriterion("use_press", UsePressTrigger.Instance.userPress()).register(consumer, "horsepower:horsepower/use_hppress");

    }
}
