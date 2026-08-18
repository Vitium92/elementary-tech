package elementarytech.util;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;

public class ETBucketHandler {

    @SubscribeEvent
    public void onBucketFill(FillBucketEvent event) {
        if (event.current != null && event.current.getItem() == net.minecraft.init.Items.bucket) {
            if (event.world.getBlock(event.target.blockX, event.target.blockY, event.target.blockZ) instanceof ETFluidBlock) {
                event.setCanceled(true);
                event.result = null;
            }
        }
    }
}
