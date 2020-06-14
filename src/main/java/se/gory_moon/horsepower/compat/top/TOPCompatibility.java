package se.gory_moon.horsepower.compat.top;

import javax.annotation.Nullable;

import com.google.common.base.Function;

import mcjty.theoneprobe.api.ITheOneProbe;
import net.minecraftforge.fml.InterModComms;

public class TOPCompatibility {

    public static void register() {
        InterModComms.sendTo("theoneprobe", "getTheOneProbe", GetTheOneProbe::new);
    }

    public static class GetTheOneProbe implements Function<ITheOneProbe, Void> {

        @Nullable
        @Override
        public Void apply(@Nullable ITheOneProbe probe) {
            if(probe == null)
                return null;
            
            probe.registerProvider(new TOPInfoProvider());

            return null;
        }
    }
}
