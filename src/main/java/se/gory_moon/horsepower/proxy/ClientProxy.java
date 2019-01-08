package se.gory_moon.horsepower.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.oredict.OreDictionary;
import se.gory_moon.horsepower.blocks.ModBlocks;
import se.gory_moon.horsepower.client.ModModelManager;
import se.gory_moon.horsepower.client.renderer.ClientHandler;
import se.gory_moon.horsepower.client.renderer.TileEntityFillerRender;
import se.gory_moon.horsepower.client.renderer.TileEntityGrindstoneRender;
import se.gory_moon.horsepower.client.renderer.TileEntityHandGrindstoneRender;
import se.gory_moon.horsepower.tileentity.TileEntityFiller;
import se.gory_moon.horsepower.tileentity.TileEntityGrindstone;
import se.gory_moon.horsepower.tileentity.TileEntityHandGrindstone;
import se.gory_moon.horsepower.util.color.ColorGetter;

import java.awt.*;

@OnlyIn(Dist.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit() {
        super.preInit();
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGrindstone.class, new TileEntityGrindstoneRender());
        //ClientRegistry.bindTileEntitySpecialRenderer(TileEntityChopper.class, new TileEntityChopperRender());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFiller.class, new TileEntityFillerRender());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityHandGrindstone.class, new TileEntityHandGrindstoneRender());
        //ClientRegistry.bindTileEntitySpecialRenderer(TileEntityManualChopper.class, new TileEntityChoppingBlockRender());
        //ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPress.class, new TileEntityPressRender());
    }

    @Override
    public void init() {
        MinecraftForge.EVENT_BUS.register(ClientHandler.class);
    }

    @Override
    public void loadComplete() {
        //ClientCommandHandler.instance.registerCommand(new HorsePowerCommand());

        /*((IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager()).registerReloadListener(resourceManager -> {
            TileEntityHPBaseRenderer.clearDestroyStageicons();
        });*/

        Minecraft.getInstance().getBlockColors().register((state, worldIn, pos, tintIndex) -> {
            if (worldIn != null && pos != null) {
                TileEntity tileEntity = worldIn.getTileEntity(pos);
                if (tileEntity instanceof TileEntityGrindstone) {
                    TileEntityGrindstone te = (TileEntityGrindstone) tileEntity;
                    ItemStack outputStack = te.getStackInSlot(1);
                    ItemStack secondaryStack = te.getStackInSlot(2);
                    if (outputStack.getCount() < secondaryStack.getCount())
                        outputStack = secondaryStack;
                    te.renderStack = outputStack;
                    if (!ItemStack.areItemsEqual(te.renderStack, outputStack)) {
                        te.renderStack = outputStack;
                        if (!outputStack.isEmpty())
                            te.grindColor = ColorGetter.getColors(outputStack, 2).get(0);
                        else
                            te.grindColor = null;
                        te.renderStack = outputStack;
                    }

                    if (te.grindColor != null)
                        return te.grindColor.getRGB();
                }
            }
            return -1;
        }, ModBlocks.BLOCK_GRINDSTONE);
    }
}
