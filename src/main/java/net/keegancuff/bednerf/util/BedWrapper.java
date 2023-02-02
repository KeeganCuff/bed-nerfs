package net.keegancuff.bednerf.util;

import net.minecraft.state.property.BooleanProperty;

public interface BedWrapper {
    final BooleanProperty PLAYER_PLACED = BooleanProperty.of("player_placed");
    boolean isPlayerPlaced();
}
