package com.natashadraper.steppy.forge;

import dev.architectury.platform.forge.EventBuses;
import com.natashadraper.steppy.SteppyMod;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SteppyMod.MOD_ID)
public class SteppyModForge {
    public SteppyModForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(SteppyMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        SteppyMod.init();
    }
}
