package za.co.natashadraper.steppy.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import za.co.natashadraper.steppy.SteppyMod;

@Config(name = SteppyMod.MOD_ID)
public class SteppyConfig implements ConfigData {
    public boolean enableSteppy = true;
    public boolean enableSteppyWhenSneaking = false;
    public double stepHeight = 1.25;

    public static void register() {
        AutoConfig.register(SteppyConfig.class, Toml4jConfigSerializer::new);
    }

    public static SteppyConfig get() {
        return AutoConfig.getConfigHolder(SteppyConfig.class).getConfig();
    }
}
