package com.natashadraper.steppy.config;
import com.natashadraper.steppy.SteppyMod;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;

@Config(name = SteppyMod.MOD_ID)
public class ModConfig implements ConfigData {
  public boolean enableSteppy = true;

  public boolean enableSteppyWhenSneaking = false;

  public float stepHeight = 1.25f;

  private static ModConfig instance;

  public static void register() {
    AutoConfig.register(ModConfig.class, Toml4jConfigSerializer::new);
  }

  public static ModConfig get() {
    if (instance == null) {
      instance = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }
    return instance;
  }
}
