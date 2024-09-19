package za.co.natashadraper.steppy.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import za.co.natashadraper.steppy.config.SteppyConfig;

public final class SteppyModFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SteppyConfig.register();
    }
}
