package se.gory_moon.horsepower.tweaker;

import com.google.common.collect.Lists;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import se.gory_moon.horsepower.recipes.ShapedChoppingRecipe;
import se.gory_moon.horsepower.recipes.ShapelessChoppingRecipe;
import se.gory_moon.horsepower.tweaker.recipes.ChoppingRecipeTweaker;
import se.gory_moon.horsepower.tweaker.recipes.GrindstoneRecipeTweaker;
import se.gory_moon.horsepower.tweaker.recipes.PressRecipeTweaker;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static crafttweaker.api.minecraft.CraftTweakerMC.getItemStacks;

@ZenClass("mods.horsepower.Recipes")
public class TweakerPluginImpl implements ITweakerPlugin {

    public TweakerPluginImpl() {
    }

    private static IntSet usedHashes = new IntOpenHashSet();
    public static List<IHPAction> toAdd = new ArrayList<>();
    public static List<IHPAction> toRemove = new ArrayList<>();

    public static List<IAction> actions = new LinkedList<>();
    public static final List<IAction> LATE_ADDITIONS = new LinkedList<>();

    @Override
    public void applyTweaker() {
        for (IAction action: actions)
            action.apply();
    }

    @Override
    public void register() {
        CraftTweakerAPI.registerClass(GrindstoneRecipeTweaker.class);
        CraftTweakerAPI.registerClass(ChoppingRecipeTweaker.class);
        CraftTweakerAPI.registerClass(PressRecipeTweaker.class);
        CraftTweakerAPI.registerClass(TweakerPluginImpl.class);
    }

    @Override
    public void run() {
        toRemove.forEach(IHPAction::run);
        toAdd.forEach(IHPAction::run);
        LATE_ADDITIONS.forEach(IAction::apply);
    }

    @ZenMethod
    public static void addShaped(String name, IIngredient ore, IItemStack output, IIngredient[][] ingredients) {
        LATE_ADDITIONS.add(new AddShapedChoppingRecipe(name, ore, output, ingredients));
    }

    @ZenMethod
    public static void addShaped(IIngredient ore, IItemStack output, IIngredient[][] ingredients) {
        LATE_ADDITIONS.add(new AddShapedChoppingRecipe(ore, output, ingredients));
    }

    @ZenMethod
    public static void addShapeless(String name, IIngredient ore, IItemStack output, IIngredient[] ingredients) {
        LATE_ADDITIONS.add(new AddShapelessChoppingRecipe(name, ore, output, ingredients));
    }

    @ZenMethod
    public static void addShapeless(IIngredient ore, IItemStack output, IIngredient[] ingredients) {
        LATE_ADDITIONS.add(new AddShapelessChoppingRecipe(ore, output, ingredients));
    }

    public static ShapedChoppingRecipe convert(CTShapedChoppingRecipe recipe, ResourceLocation name) {
        IIngredient[] ingredients = recipe.getIngredients();
        byte[] posx = recipe.getIngredientsX();
        byte[] posy = recipe.getIngredientsY();
        int counter;

        Object[] converted = new Object[recipe.getHeight() * recipe.getWidth()];

        for(counter = 0; counter < ingredients.length; ++counter) {
            converted[posx[counter] + posy[counter] * recipe.getWidth()] = ingredients[counter].getInternal();
        }

        counter = 0;
        String[] parts = new String[recipe.getHeight()];
        ArrayList rarguments = new ArrayList();

        for(int i = 0; i < recipe.getHeight(); ++i) {
            char[] pattern = new char[recipe.getWidth()];

            for(int j = 0; j < recipe.getWidth(); ++j) {
                int off = i * recipe.getWidth() + j;
                if(converted[off] == null) {
                    pattern[j] = 32;
                } else {
                    pattern[j] = (char)(65 + counter);
                    rarguments.add(Character.valueOf(pattern[j]));
                    rarguments.add(converted[off]);
                    ++counter;
                }
            }

            parts[i] = new String(pattern);
        }

        rarguments.addAll(0, Arrays.asList(parts));
        return new ShapedChoppingRecipe(name, Lists.newArrayList(getItemStacks(recipe.getOre().getItems())), (ItemStack) recipe.getOutput().getInternal(), rarguments.toArray());
    }

    public static ShapelessChoppingRecipe convert(CTShapelessChoppingRecipe recipe, ResourceLocation name) {
        IIngredient[] ingredients = recipe.getIngredients();
        Object[] items = new Object[ingredients.length];
        for(int i = 0; i < ingredients.length; i++) {
            items[i] = ingredients[i].getInternal();
        }
        return new ShapelessChoppingRecipe(name, Lists.newArrayList(getItemStacks(recipe.getOre().getItems())), (ItemStack) recipe.getOutput().getInternal(), items);
    }

    private static class AddShapedChoppingRecipe implements IAction {

        private IIngredient ore;
        private IItemStack output;
        private IIngredient[][] ingredients;
        private String name;

        public AddShapedChoppingRecipe(IIngredient ore, IItemStack output, IIngredient[][] ingredients) {
            this(null, ore, output, ingredients);
        }

        public AddShapedChoppingRecipe(String name, IIngredient ore, IItemStack output, IIngredient[][] ingredients) {
            this.ore = ore;
            this.output = output;
            this.ingredients = ingredients;
            if (name == null) {
                this.name = calculateName();
            } else {
                this.name = name.replace(":", "_");
            }
        }

        @Override
        public void apply() {
            CTShapedChoppingRecipe ctRecipe = new CTShapedChoppingRecipe(ore, name, output, ingredients);
            IRecipe recipe = convert(ctRecipe, new ResourceLocation("horsepower", name));

            recipe.setRegistryName(new ResourceLocation("horsepower", this.name));
            ForgeRegistries.RECIPES.register(recipe);
        }

        @Override
        public String describe() {
            if(output != null) {
                return "Adding shaped recipe for " + output.getDisplayName() + " with name " + name;
            } else {
                return "Trying to add shaped recipe without correct output";
            }
        }

        private String calculateName() {
            StringBuilder sb = new StringBuilder();
            sb.append(output);
            for(IIngredient[] ingredientArray : ingredients) {
                for(IIngredient ingredient : ingredientArray) {
                    sb.append(ingredient.toCommandString());
                }
            }

            int hash = sb.toString().hashCode();
            while(usedHashes.contains(hash))
                ++hash;
            usedHashes.add(hash);

            return "hp_shaped" + hash;
        }
    }

    private static class AddShapelessChoppingRecipe implements IAction {

        private IIngredient ore;
        private IItemStack output;
        private IIngredient[] ingredients;
        private String name;

        public AddShapelessChoppingRecipe(IIngredient ore, IItemStack output, IIngredient[] ingredients) {
            this(null, ore, output, ingredients);
        }

        public AddShapelessChoppingRecipe(String name, IIngredient ore, IItemStack output, IIngredient[] ingredients) {
            this.ore = ore;
            this.output = output;
            this.ingredients = ingredients;
            if (name == null) {
                this.name = calculateName();
            } else {
                this.name = name.replace(":", "_");
            }
        }

        @Override
        public void apply() {
            CTShapelessChoppingRecipe ctRecipe = new CTShapelessChoppingRecipe(ore, name, output, ingredients);
            IRecipe recipe = convert(ctRecipe, new ResourceLocation("horsepower", name));

            recipe.setRegistryName(new ResourceLocation("horsepower", this.name));
            ForgeRegistries.RECIPES.register(recipe);
        }

        @Override
        public String describe() {
            if(output != null) {
                return "Adding shapeless recipe for " + output.getDisplayName() + " with name " + name;
            } else {
                return "Trying to add shapeless recipe without correct output";
            }
        }

        private String calculateName() {
            StringBuilder sb = new StringBuilder();
            sb.append(output);
            for(IIngredient ingredient : ingredients) {
                sb.append(ingredient.toCommandString());
            }

            int hash = sb.toString().hashCode();
            while(usedHashes.contains(hash))
                ++hash;
            usedHashes.add(hash);

            return "hp_shapeless" + hash;
        }
    }
}
