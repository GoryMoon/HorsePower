package se.gory_moon.horsepower.util;

import com.google.common.collect.Lists;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.passive.AbstractHorse;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.HorsePowerMod;

import java.util.ArrayList;

public class Utils {

    public static ArrayList<Class<? extends EntityCreature>> getCreatureClasses() {
        ArrayList<Class<? extends EntityCreature>> clazzes = Lists.newArrayList();
        if (Configs.useHorseInterface)
            clazzes.add(AbstractHorse.class);

        for (String e: Configs.grindstoneModList) {
            try {
                Class clazz = Class.forName(e);

                if (EntityCreature.class.isAssignableFrom(clazz)) {
                    clazzes.add(clazz);
                } else {
                    HorsePowerMod.logger.error("Error in config, the mob (" + e + ") can't be leashed");
                }
            } catch (ClassNotFoundException e1) {
                HorsePowerMod.logger.error("Error in config, could not find (" + e + ") mob class");
            }
        }
        return clazzes;
    }

}
