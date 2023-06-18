package com.natashadraper.steppy;

import com.natashadraper.steppy.config.ModConfig;
import net.fabricmc.api.ClientModInitializer;

public class SteppyMod implements ClientModInitializer {
	public static final String MOD_ID = "steppy";

	@Override
	public void onInitializeClient() {
		this.registerConfig();
	}

	private void registerConfig() {
		ModConfig.register();
	}
}