package com.natashadraper.steppy.fabric;

import com.natashadraper.steppy.SteppyMod;
import net.fabricmc.api.ModInitializer;

public class SteppyModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        SteppyMod.init();
    }
}
