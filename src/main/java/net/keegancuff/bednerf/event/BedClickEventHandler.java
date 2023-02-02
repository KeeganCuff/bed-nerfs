package net.keegancuff.bednerf.event;

import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.keegancuff.bednerf.util.BedWrapper;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BedClickEventHandler implements EntitySleepEvents.AllowSleeping {
    @Override
    public PlayerEntity.@Nullable SleepFailureReason allowSleep(PlayerEntity player, BlockPos sleepingPos) {
        World world = player.getWorld();
        BlockState state = world.getBlockState(sleepingPos);
        if (state.getBlock() instanceof BedBlock && !state.get(BedWrapper.PLAYER_PLACED)){
            player.sendMessage(Text.literal("You cannot sleep in naturally generated beds"), true);
            return PlayerEntity.SleepFailureReason.OTHER_PROBLEM;
        }
        return null;
    }
}
