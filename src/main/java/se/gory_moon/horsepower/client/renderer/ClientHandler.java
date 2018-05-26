package se.gory_moon.horsepower.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.blocks.BlockGrindstone;
import se.gory_moon.horsepower.blocks.BlockHPBase;
import se.gory_moon.horsepower.tileentity.TileEntityHPHorseBase;
import se.gory_moon.horsepower.util.RenderUtils;

import java.util.stream.StreamSupport;

public class ClientHandler {

    @SubscribeEvent
    public static void renderWorld(RenderWorldLastEvent event) {
        final ItemStack[] itemStack = {ItemStack.EMPTY};
        if (Configs.client.showObstructedPlace) {
            if (StreamSupport.stream(Minecraft.getMinecraft().player.getHeldEquipment().spliterator(), false).anyMatch(stack -> !stack.isEmpty() && isHPBlock((itemStack[0] = stack).getItem()))) {
                Minecraft mc = Minecraft.getMinecraft();
                if (mc.objectMouseOver.typeOfHit != RayTraceResult.Type.BLOCK)
                    return;

                int offset = 0;
                if (!itemStack[0].isEmpty() && ((ItemBlock) itemStack[0].getItem()).getBlock() instanceof BlockGrindstone)
                    offset = -1;

                EnumFacing enumFacing = mc.objectMouseOver.sideHit;
                BlockPos pos = mc.objectMouseOver.getBlockPos();
                if (!mc.world.getBlockState(pos).getBlock().isReplaceable(mc.world, pos))
                    pos = pos.offset(enumFacing);
                if (offset == 0 && !mc.world.getBlockState(pos.up()).getBlock().isReplaceable(mc.world, pos.up()))
                    pos = pos.down();

                RenderUtils.renderUsedArea(mc.world, pos, offset, 0.15F, 0.05F);
            }
        }
    }

    private static boolean isHPBlock(Item item) {
        if (item instanceof ItemBlock && ((ItemBlock) item).getBlock() instanceof BlockHPBase) {
            if (TileEntityHPHorseBase.class.isAssignableFrom(((BlockHPBase) ((ItemBlock) item).getBlock()).getTileClass()))
                return true;
        }
        return false;
    }
}
