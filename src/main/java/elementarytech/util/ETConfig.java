package elementarytech.util;

import java.io.File;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.config.Configuration;

public class ETConfig {
    public int handpumpTier = 1;
    public int handpumpMaxCharge = 30000;
    public int handpumpOperationEUCost = 180;

    public int advancedHandpumpTier = 3;
    public int advancedHandpumpMaxCharge = 1000000;
    public int advancedHandpumpOperationEUCost = 10000;

    public boolean enableHandpump = true;
    public boolean enableRubberTreeSack = true;

    public boolean skipRecipeLoad = false;

    public ETConfig(File configFile) {
        Configuration config = new Configuration(configFile);
        config.load();

        handpumpTier = config.getInt("handpumpTier", Configuration.CATEGORY_GENERAL, 1, 1, 5, "Tier of the hand pump");
        handpumpMaxCharge = config.getInt("handpumpMaxCharge", Configuration.CATEGORY_GENERAL, 30000, 1, 1000000, "Max EU charge of the hand pump");
        handpumpOperationEUCost = config.getInt("handpumpOperationEUCost", Configuration.CATEGORY_GENERAL, 180, 1, 100000, "EU cost per hand pump operation");

        advancedHandpumpTier = config.getInt("advancedHandpumpTier", Configuration.CATEGORY_GENERAL, 3, 1, 5, "Tier of the advanced hand pump");
        advancedHandpumpMaxCharge = config.getInt("advancedHandpumpMaxCharge", Configuration.CATEGORY_GENERAL, 1000000, 1, 10000000, "Max EU charge of the advanced hand pump");
        advancedHandpumpOperationEUCost = config.getInt("advancedHandpumpOperationEUCost", Configuration.CATEGORY_GENERAL, 10000, 1, 1000000, "EU cost per advanced hand pump operation");

        enableHandpump = config.getBoolean("enableHandpump", Configuration.CATEGORY_GENERAL, true, "Enable the hand pump");
        enableRubberTreeSack = config.getBoolean("enableRubberTreeSack", Configuration.CATEGORY_GENERAL, true, "Enable the rubber tree sack");

        skipRecipeLoad = config.getBoolean("skipRecipeLoad", Configuration.CATEGORY_GENERAL, false, "Skip loading recipes (for debugging)");

        if (config.hasChanged()) {
            config.save();
        }
    }

    public ETConfig(FMLPreInitializationEvent event) {
        this(event.getSuggestedConfigurationFile());
    }
}
