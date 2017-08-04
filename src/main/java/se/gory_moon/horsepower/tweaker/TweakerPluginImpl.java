package se.gory_moon.horsepower.tweaker;

import com.google.common.collect.Lists;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.oredict.IOreDictEntry;
import crafttweaker.mc1120.recipes.MCRecipeManager;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import se.gory_moon.horsepower.recipes.ChoppingRecipe;
import se.gory_moon.horsepower.tweaker.recipes.ChoppingRecipeTweaker;
import se.gory_moon.horsepower.tweaker.recipes.GrindstoneRecipeTweaker;
import se.gory_moon.horsepower.tweaker.recipes.PressRecipeTweaker;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ZenClass("mods.horsepower.Recipes")
public class TweakerPluginImpl implements ITweakerPlugin {

    public TweakerPluginImpl() {
    }

    public static List<IAction> actions = Lists.newArrayList();
    private static TIntSet usedHashes = new TIntHashSet();

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




    @ZenMethod
    public static void addShaped(String name, IOreDictEntry ore, IItemStack output, IIngredient[][] ingredients) {
        MCRecipeManager.recipesToAdd.add(new AddChoppingRecipe(name, ore, output, ingredients));
    }

    @ZenMethod
    public static void addShaped(IOreDictEntry ore, IItemStack output, IIngredient[][] ingredients) {
        MCRecipeManager.recipesToAdd.add(new AddChoppingRecipe(ore, output, ingredients));
    }

    public static IRecipe convert(ShapedChoppingRecipe recipe, ResourceLocation name) {
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
        return new ChoppingRecipe(name, OreDictionary.getOres(recipe.getOre().getName()), (ItemStack) recipe.getOutput().getInternal(), rarguments.toArray());
    }

    private static class AddChoppingRecipe extends MCRecipeManager.ActionBaseAddRecipe {

        private IOreDictEntry ore;
        private IItemStack output;
        private IIngredient[][] ingredients;
        private String name;

        public AddChoppingRecipe(IOreDictEntry ore, IItemStack output, IIngredient[][] ingredients) {
            this.ore = ore;
            this.output = output;
            this.ingredients = ingredients;
            this.name = calculateName(output, ingredients);
        }

        public AddChoppingRecipe(String name, IOreDictEntry ore, IItemStack output, IIngredient[][] ingredients) {
            this.ore = ore;
            this.name = MCRecipeManager.cleanRecipeName(name);
            this.output = output;
            this.ingredients = ingredients;
        }

        public static String calculateName(IItemStack output, IIngredient[][] ingredients) {
            StringBuilder sb = new StringBuilder();
            sb.append(MCRecipeManager.saveToString(output));

            for(IIngredient[] ingredient : ingredients) {
                for(IIngredient iIngredient : ingredient) {
                    sb.append(MCRecipeManager.saveToString(iIngredient));
                }
            }

            int hash = sb.toString().hashCode();
            while(usedHashes.contains(hash))
                ++hash;
            usedHashes.add(hash);

            return "hp_shaped" + hash;
        }

        @Override
        public void apply() {
            ShapedChoppingRecipe recipe = new ShapedChoppingRecipe(ore, name, output, ingredients);
            IRecipe irecipe = convert(recipe, new ResourceLocation("horsepower", name));

            irecipe.setRegistryName(new ResourceLocation("horsepower", this.name));
            ForgeRegistries.RECIPES.register(irecipe);
        }

        @Override
        public String describe() {
            return "Adding dynamic chopping recipe for " + MCRecipeManager.saveToString(output);
        }
    }
}
