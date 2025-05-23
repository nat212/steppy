package za.co.natashadraper.steppy.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import za.co.natashadraper.steppy.SteppyMod;
import za.co.natashadraper.steppy.config.SteppyConfig;

@Mixin(ClientPlayerEntity.class)
public abstract class SteppyPlayerMixin {
    @Shadow
    public abstract boolean isSneaking();

    private static final Identifier STEP_HEIGHT_ATTRIBUTE_ID = Identifier.ofVanilla("step_height");
    private static final RegistryEntry<EntityAttribute> STEP_HEIGHT_ATTRIBUTE = Registries.ATTRIBUTE.getEntry(STEP_HEIGHT_ATTRIBUTE_ID).get();
    private static final Identifier STEPPY_MODIFIER_ID = Identifier.of(SteppyMod.MOD_ID, "step_height");

    private EntityAttributeModifier getAttributeModifier(double stepHeight) {
        return new EntityAttributeModifier(STEPPY_MODIFIER_ID, stepHeight, EntityAttributeModifier.Operation.ADD_VALUE);
    }

    private boolean shouldEnableSteppy() {
        var config = SteppyConfig.get();
        boolean enableSteppy = config.enableSteppy;
        boolean enableSteppyWhileSneaking = config.enableSteppyWhenSneaking;
        return enableSteppy && (!isSneaking() || enableSteppyWhileSneaking);
    }

    private void disableSteppy() {
        EntityAttributeInstance stepHeightAttribute = ((ClientPlayerEntity) (Object) this).getAttributeInstance(STEP_HEIGHT_ATTRIBUTE);
        assert stepHeightAttribute != null;
        stepHeightAttribute.removeModifier(STEP_HEIGHT_ATTRIBUTE_ID);
    }

    private void setSteppyHeight(double stepHeight) {
        EntityAttributeInstance stepHeightAttribute = ((ClientPlayerEntity) (Object) this).getAttributeInstance(STEP_HEIGHT_ATTRIBUTE);
        assert stepHeightAttribute != null;
        var stepHeightDiff = stepHeight - stepHeightAttribute.getValue();
        if (stepHeightDiff <= 0) {
            disableSteppy();
        } else {
            stepHeightAttribute.overwritePersistentModifier(getAttributeModifier(stepHeight));
        }
    }

    @Inject(method = "move", at = @At("HEAD"))
    private void steppy(MovementType movementType, Vec3d movement, CallbackInfo ci) {
        if (!shouldEnableSteppy()) {
            disableSteppy();
            return;
        }
        setSteppyHeight(SteppyConfig.get().stepHeight);
    }

    @Inject(method = "shouldAutoJump", at = @At(value = "HEAD"), cancellable = true)
    private void disableAutoJump(CallbackInfoReturnable<Boolean> callback) {
        if (this.shouldEnableSteppy()) {
            callback.setReturnValue(false);
            callback.cancel();
        }
    }
}
