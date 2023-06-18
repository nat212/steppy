package com.natashadraper.steppy.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.natashadraper.steppy.config.ModConfig;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {

  private static float originalStepHeight = -1.0f;

  @Shadow public abstract boolean isSneaking();
  @Shadow public boolean autoJumpEnabled;

  private boolean shouldEnableSteppy() {
    boolean enableSteppy = ModConfig.get().enableSteppy ;
    boolean enableSteppyWhenSneaking = ModConfig.get().enableSteppyWhenSneaking;
    return enableSteppy && (!this.isSneaking() || enableSteppyWhenSneaking);
  }

  @Inject(method = "move", at = @At(value = "HEAD"))
  private void steppy(MovementType movementType, Vec3d movement, CallbackInfo callback) {
    Entity thisEntity = ((Entity) (Object) this);
    if (this.shouldEnableSteppy()) {
      if (originalStepHeight < 0) {
        originalStepHeight = thisEntity.getStepHeight();
      }
      thisEntity.setStepHeight(ModConfig.get().stepHeight);
    } else {
      thisEntity.setStepHeight(originalStepHeight);
    }
  }

  @Inject(method = "shouldAutoJump", at = @At(value = "HEAD"), cancellable = true)
  private void disableAutoJump(CallbackInfoReturnable<Boolean> callback) {
    if (this.shouldEnableSteppy()) {
      callback.setReturnValue(false);
      callback.cancel();
    }
  }
}
