package za.co.natashadraper.steppy.neoforge.config;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gui.screen.Screen;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.jetbrains.annotations.NotNull;
import za.co.natashadraper.steppy.config.SteppyConfig;

public class SteppyConfigScreenFactory implements IConfigScreenFactory {
    @Override
    public @NotNull Screen createScreen(@NotNull ModContainer modContainer, @NotNull Screen arg) {
        return AutoConfig.getConfigScreen(SteppyConfig.class, arg).get();
    }
}
