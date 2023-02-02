package net.keegancuff.bednerf.event;

import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.keegancuff.bednerf.util.BedWakeHelper;
import net.minecraft.entity.player.PlayerEntity;

public class SleepEventHandler implements EntitySleepEvents.AllowResettingTime {
    @Override
    public boolean allowResettingTime(PlayerEntity player) {
        return !BedWakeHelper.tryWakePlayer(player.getWorld(), player);
    }
}
