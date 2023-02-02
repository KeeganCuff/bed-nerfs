package net.keegancuff.bednerf.mixin;

import net.keegancuff.bednerf.util.BedWrapper;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.state.StateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BedBlock.class)
public class BedMixin extends HorizontalFacingBlock implements BedWrapper {


    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BedBlock;setDefaultState(Lnet/minecraft/block/BlockState;)V"))
    private BlockState injected(BlockState state){
        return state.with(PLAYER_PLACED, false);
    }

    @Inject(method = "getPlacementState", at = @At("RETURN"), cancellable = true)
    private void injected(CallbackInfoReturnable<BlockState> ci){
        ci.setReturnValue(ci.getReturnValue().with(PLAYER_PLACED, true));
    }

    @Inject(method = "appendProperties", at = @At("TAIL"))
    private void injected(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci){
        builder.add(PLAYER_PLACED);
    }



    protected BedMixin(Settings settings) {
        super(settings);
    }

    @Override
    public boolean isPlayerPlaced() {
        return false;
    }
}
