package za.co.natashadraper.steppy.mixin;

import com.mojang.logging.LogUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
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
    @Unique
    private static final Logger LOGGER = LogUtils.getLogger();

    @Unique
    private final double steppy$defaultStepHeight;

    public SteppyPlayerMixin() {
        LOGGER.info("[SteppyPlayerMixin] Initializing SteppyPlayerMixin");
        var stepHeightAttribute = steppy$getStepHeightAttribute();
        assert stepHeightAttribute != null;
        this.steppy$defaultStepHeight = stepHeightAttribute.getBaseValue();
        LOGGER.info("[SteppyPlayerMixin] Storing default step height of {}", steppy$defaultStepHeight);
        LOGGER.info("[SteppyPlayerMixin] SteppyPlayerMixin initialised");
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
    private void steppy$disableSteppy() {
        var stepHeightAttribute = steppy$getStepHeightAttribute();
        assert stepHeightAttribute != null;
        stepHeightAttribute.setBaseValue(steppy$defaultStepHeight);
    }

    @Unique
    private void steppy$setSteppyHeight(double stepHeight) {
        var stepHeightAttribute = steppy$getStepHeightAttribute();
        assert stepHeightAttribute != null;
        stepHeightAttribute.setBaseValue(stepHeight);
    }

    @Inject(method = "move", at = @At("HEAD"))
    private void steppy(MovementType movementType, Vec3d movement, CallbackInfo ci) {
        if (!steppy$shouldEnableSteppy()) {
            steppy$disableSteppy();
            return;
        }
        steppy$setSteppyHeight(SteppyConfig.get().stepHeight);
    }

    @Inject(method = "shouldAutoJump", at = @At(value = "HEAD"), cancellable = true)
    private void disableAutoJump(CallbackInfoReturnable<Boolean> callback) {
        if (this.steppy$shouldEnableSteppy()) {
            callback.setReturnValue(false);
            callback.cancel();
        }
    }
}
