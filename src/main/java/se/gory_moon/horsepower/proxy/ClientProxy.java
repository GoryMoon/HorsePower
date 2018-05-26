package se.gory_moon.horsepower.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import se.gory_moon.horsepower.blocks.ModBlocks;
import se.gory_moon.horsepower.client.renderer.*;
import se.gory_moon.horsepower.tileentity.*;
import se.gory_moon.horsepower.util.HorsePowerCommand;
import se.gory_moon.horsepower.util.color.ColorGetter;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit() {
        super.preInit();
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGrindstone.class, new TileEntityGrindstoneRender());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityChopper.class, new TileEntityChopperRender());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFiller.class, new TileEntityFillerRender());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityHandGrindstone.class, new TileEntityHandGrindstoneRender());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityManualChopper.class, new TileEntityChoppingBlockRender());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPress.class, new TileEntityPressRender());
    }

    @Override
    public void init() {
        MinecraftForge.EVENT_BUS.register(ClientHandler.class);
    }

    @Override
    public void loadComplete() {
        ClientCommandHandler.instance.registerCommand(new HorsePowerCommand());

        ((IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager()).registerReloadListener(resourceManager -> {
            TileEntityHPBaseRenderer.clearDestroyStageicons();
        });

        Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler((state, worldIn, pos, tintIndex) -> {
            if (worldIn != null && pos != null) {
                TileEntity tileEntity = worldIn.getTileEntity(pos);
                if (tileEntity instanceof TileEntityGrindstone) {
                    TileEntityGrindstone te = (TileEntityGrindstone) tileEntity;
                    ItemStack outputStack = te.getStackInSlot(1);
                    ItemStack secondaryStack = te.getStackInSlot(2);
                    if (outputStack.getCount() < secondaryStack.getCount())
                        outputStack = secondaryStack;
                    if (!OreDictionary.itemMatches(te.renderStack, outputStack, true)) {
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
