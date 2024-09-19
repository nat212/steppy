package za.co.natashadraper.steppy.fabric;

import net.fabricmc.api.ModInitializer;

import za.co.natashadraper.steppy.SteppyMod;

public final class SteppyModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        SteppyMod.init();
    }
}
