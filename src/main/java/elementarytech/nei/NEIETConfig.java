package elementarytech.nei;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.api.ItemInfo;
import elementarytech.ModInfo;
import elementarytech.machines.bronzetub.BronzeTubGui;
import elementarytech.machines.evaporator.ElectricEvaporatorGui;
import elementarytech.machines.evaporator.EvaporatorGui;
import elementarytech.machines.leadoven.LeadOvenGui;

public class NEIETConfig implements IConfigureNEI {
    @Override
    public void loadConfig() {
        API.registerRecipeHandler(new EvaporatorRecipeHandler());
        API.registerUsageHandler(new EvaporatorRecipeHandler());
        API.registerGuiOverlay(EvaporatorGui.class, "evaporator", 5, 11);
        API.registerRecipeHandler(new ElectricEvaporatorRecipeHandler());
        API.registerUsageHandler(new ElectricEvaporatorRecipeHandler());
        API.registerGuiOverlay(ElectricEvaporatorGui.class, "electricEvaporator", 5, 11);
        API.registerRecipeHandler(new LeadOvenRecipeHandler());
        API.registerUsageHandler(new LeadOvenRecipeHandler());
        API.registerGuiOverlay(LeadOvenGui.class, "leadOven", 5, 11);
        API.registerRecipeHandler(new ImpregnatingMachineRecipeHandler());
        API.registerUsageHandler(new ImpregnatingMachineRecipeHandler());
        API.registerGuiOverlay(BronzeTubGui.class, "impregnatingMachine", 5, 11);
    }

    @Override
    public String getName() {
        return "ElementaryTech";
    }

    @Override
    public String getVersion() {
        return ModInfo.MODVERSION;
    }
}
