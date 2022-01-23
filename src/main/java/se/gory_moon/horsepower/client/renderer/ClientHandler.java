package se.gory_moon.horsepower.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import se.gory_moon.horsepower.Configs;
import se.gory_moon.horsepower.blocks.HPBlock;
import se.gory_moon.horsepower.blocks.MillstoneBlock;
import se.gory_moon.horsepower.tileentity.HPHorseBaseTileEntity;
import se.gory_moon.horsepower.util.Constants;
import se.gory_moon.horsepower.util.RenderUtils;

import java.util.stream.StreamSupport;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
public class ClientHandler {

    @SubscribeEvent
    public static void renderWorld(RenderWorldLastEvent event) {
        final ItemStack[] itemStack = { ItemStack.EMPTY };
        if (Configs.CLIENT.showObstructedPlace.get()) {
            if (StreamSupport.stream(Minecraft.getInstance().player.getHeldEquipment().spliterator(), false).anyMatch(stack -> !stack.isEmpty() && isHPBlock((itemStack[0] = stack).getItem()))) {
                Minecraft mc = Minecraft.getInstance();
                if (mc.objectMouseOver == null || mc.objectMouseOver.getType() != RayTraceResult.Type.BLOCK)
                    return;

                int offset = 0;
                if (!itemStack[0].isEmpty() && ((BlockItem) itemStack[0].getItem()).getBlock() instanceof MillstoneBlock)
                    offset = -1;

                Direction enumFacing = ((BlockRayTraceResult) mc.objectMouseOver).getFace();
                BlockPos pos = ((BlockRayTraceResult) mc.objectMouseOver).getPos();
                if (!mc.world.getBlockState(pos).getMaterial().isReplaceable())
                    pos = pos.offset(enumFacing);
                if (offset == 0 && !mc.world.getBlockState(pos.up()).getMaterial().isReplaceable())
                    pos = pos.down();

                RenderUtils.renderUsedArea(mc.world, pos, offset, 0.15F, 0.05F);
            }
        }
    }

    private static boolean isHPBlock(Item item) {
        if (item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof HPBlock) {
            return HPHorseBaseTileEntity.class.isAssignableFrom(((HPBlock) ((BlockItem) item).getBlock()).getTileClass());
        }
        return false;
    }
}
