package se.gory_moon.horsepower.util;


import net.minecraft.util.ResourceLocation;

public class Constants {

    //General
    public static final String MOD_ID = "horsepower";
    public static final String RESOURCE_PREFIX = MOD_ID + ":";
    public static final String FINGERPRINT = "@FINGERPRINT@";
    
    
    //Blocks
    public static final String FILLER = "filler";
    public static final String MANUAL_MILLSTONE_BLOCK = "manual_millstone";
    public static final String MILLSTONE_BLOCK = "millstone";
    public static final String MANUAL_CHOPPER_BLOCK = "manual_chopper";
    public static final String CHOPPER_BLOCK = "chopper";
    public static final String PRESS_BLOCK = "press";
    public static final String FILLER_BLOCK = "_" + FILLER;
    public static final String PRESS_FILLER = PRESS_BLOCK + FILLER_BLOCK;
    
    //Items
    public static final String FLOUR_ITEM = "flour";
    public static final String DOUGH_ITEM = "dough";

    //Network
    public static final ResourceLocation NET_ID = new ResourceLocation(MOD_ID, "net");
}
