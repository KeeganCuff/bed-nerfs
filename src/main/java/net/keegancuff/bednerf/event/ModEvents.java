package net.keegancuff.bednerf.event;

import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;

public class ModEvents {
    public static void registerEvents(){
        EntitySleepEvents.ALLOW_RESETTING_TIME.register(new SleepEventHandler());
        EntitySleepEvents.ALLOW_SLEEPING.register(new BedClickEventHandler());
    }
}
