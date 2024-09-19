package za.co.natashadraper.steppy.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import za.co.natashadraper.steppy.SteppyMod;
import za.co.natashadraper.steppy.config.SteppyConfig;
import za.co.natashadraper.steppy.neoforge.config.SteppyConfigScreenFactory;

@Mod(value = SteppyMod.MOD_ID, dist = Dist.CLIENT)
public class SteppyModNeoForgeClient {
   public SteppyModNeoForgeClient(ModContainer container) {
       SteppyConfig.register();
       container.registerExtensionPoint(IConfigScreenFactory.class, new SteppyConfigScreenFactory());
   }
}
