package za.co.natashadraper.steppy.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import za.co.natashadraper.steppy.config.SteppyConfig;

@Mixin(ClientPlayerEntity.class)
public abstract class SteppyPlayerMixin {
    @Shadow
    public abstract boolean isSneaking();

    @Unique
    private static final Identifier STEP_HEIGHT_ATTRIBUTE_ID = Identifier.ofVanilla("step_height");
    @Unique
    private static final RegistryEntry<EntityAttribute> STEP_HEIGHT_ATTRIBUTE = Registries.ATTRIBUTE.getEntry(STEP_HEIGHT_ATTRIBUTE_ID).get();

    // Captured lazily the first time we are about to modify the step height, so it
    // reflects the real vanilla base value (0.6). We deliberately do NOT capture this
    // in a mixin constructor: mixin constructor injection is unreliable and could leave
    // a final field at 0.0, which would make disableSteppy() set the step height to 0 and
    // trap the player while sneaking (vanilla's maybeBackOffFromEdge uses step height as
    // the ledge-detection fall distance, so a 0 step height reads every drop as a cliff).
    @Unique
    private double steppy$defaultStepHeight;
    @Unique
    private boolean steppy$defaultStepHeightCaptured = false;

    @Unique
    private void steppy$captureDefaultStepHeight() {
        if (steppy$defaultStepHeightCaptured) {
            return;
        }
        var stepHeightAttribute = steppy$getStepHeightAttribute();
        assert stepHeightAttribute != null;
        this.steppy$defaultStepHeight = stepHeightAttribute.getBaseValue();
        this.steppy$defaultStepHeightCaptured = true;
    }

    @Unique
    private EntityAttributeInstance steppy$getStepHeightAttribute() {
        return ((ClientPlayerEntity) (Object) this).getAttributeInstance(STEP_HEIGHT_ATTRIBUTE);
    }

    @Unique
    private boolean steppy$shouldEnableSteppy() {
        var config = SteppyConfig.get();
        if (!config.enableSteppy) {
            return false;
        }
        return config.enableSteppyWhenSneaking || !isSneaking();
    }

    @Unique
    private boolean steppy$active = false;

    @Unique
    private void steppy$disableSteppy() {
        // If we never captured the vanilla default there is nothing meaningful to restore
        // to, and writing an uninitialised 0.0 would trap the player while sneaking.
        if (!steppy$defaultStepHeightCaptured) {
            return;
        }
        var stepHeightAttribute = steppy$getStepHeightAttribute();
        assert stepHeightAttribute != null;
        stepHeightAttribute.setBaseValue(steppy$defaultStepHeight);
    }

    @Unique
    private void steppy$setSteppyHeight(double stepHeight) {
        steppy$captureDefaultStepHeight();
        var stepHeightAttribute = steppy$getStepHeightAttribute();
        assert stepHeightAttribute != null;
        stepHeightAttribute.setBaseValue(stepHeight);
    }

    @Inject(method = "move", at = @At("HEAD"))
    private void steppy(MovementType movementType, Vec3d movement, CallbackInfo ci) {
        if (!steppy$shouldEnableSteppy()) {
            // Restore the vanilla step height once when transitioning from enabled to
            // disabled, so vanilla movement (including sneaking off non-full blocks such
            // as slabs) behaves exactly as it would without the mod.
            if (steppy$active) {
                steppy$disableSteppy();
                steppy$active = false;
            }
            return;
        }
        steppy$setSteppyHeight(SteppyConfig.get().stepHeight);
        steppy$active = true;
    }

    @Inject(method = "shouldAutoJump", at = @At(value = "HEAD"), cancellable = true)
    private void disableAutoJump(CallbackInfoReturnable<Boolean> callback) {
        if (this.steppy$shouldEnableSteppy()) {
            callback.setReturnValue(false);
            callback.cancel();
        }
    }
}
