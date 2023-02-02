package net.keegancuff.bednerf;

import net.fabricmc.api.ModInitializer;
import net.keegancuff.bednerf.event.ModEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BedNerfMod implements ModInitializer {
	public static final String MODID = "bednerf";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	@Override
	public void onInitialize() {
		ModEvents.registerEvents();
	}
}

