package se.gorymoon.horsepower.proxy;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import se.gorymoon.horsepower.client.renderer.TileEntityGrindstoneRender;
import se.gorymoon.horsepower.tileentity.TileEntityGrindstone;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGrindstone.class, new TileEntityGrindstoneRender());
    }
}
