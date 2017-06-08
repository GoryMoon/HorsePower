package se.gory_moon.horsepower.proxy;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import se.gory_moon.horsepower.client.renderer.TileEntityGrindstoneRender;
import se.gory_moon.horsepower.tileentity.TileEntityGrindstone;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGrindstone.class, new TileEntityGrindstoneRender());
    }
}
