package za.co.natashadraper.steppy;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public final class SteppyMod {
    public static final String MOD_ID = "steppy";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        LOGGER.info("[SteppyMod] Initialising steppy");
    }
}
