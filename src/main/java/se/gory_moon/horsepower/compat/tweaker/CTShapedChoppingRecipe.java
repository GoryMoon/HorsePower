package se.gory_moon.horsepower.compat.tweaker;
/*
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;

public class CTShapedChoppingRecipe {

    private final int width;
    private final int height;
    private final byte[] posx;
    private final byte[] posy;
    private final IIngredient ore;
    private final IItemStack result;
    private final IIngredient[] ingredients;
    private final String name;

    public CTShapedChoppingRecipe(IIngredient ore, String name, IItemStack result, IIngredient[][] ingredients) {
        int numIngredients = 0;
        IIngredient[][] var8 = ingredients;
        int height1 = ingredients.length;

        int ix;
        IIngredient[] row;
        int i;
        for(ix = 0; ix < height1; ++ix) {
            IIngredient[] ingredient = var8[ix];
            row = ingredient;
            i = ingredient.length;

            for(int var14 = 0; var14 < i; ++var14) {
                IIngredient anIngredient = row[var14];
                if(anIngredient != null) {
                    ++numIngredients;
                }
            }
        }

        this.posx = new byte[numIngredients];
        this.posy = new byte[numIngredients];
        this.result = result;
        this.ingredients = new IIngredient[numIngredients];
        this.name = name;
        int width1 = 0;
        height1 = ingredients.length;
        ix = 0;

        for(int j = 0; j < ingredients.length; ++j) {
            row = ingredients[j];
            width1 = Math.max(width1, row.length);

            for(i = 0; i < row.length; ++i) {
                if(row[i] != null) {
                    this.posx[ix] = (byte)i;
                    this.posy[ix] = (byte)j;
                    this.ingredients[ix] = row[i];
                    ++ix;
                }
            }
        }

        this.width = width1;
        this.height = height1;
        this.ore = ore;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public IIngredient[] getIngredients() {
        return this.ingredients;
    }

    public byte[] getIngredientsX() {
        return this.posx;
    }

    public byte[] getIngredientsY() {
        return this.posy;
    }

    public IItemStack getOutput() {
        return this.result;
    }

    public IIngredient getOre() {
        return ore;
    }

    public String getString() {
        return this.name;
    }

}
*/