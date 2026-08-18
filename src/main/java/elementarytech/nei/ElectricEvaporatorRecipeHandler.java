package elementarytech.nei;

import java.awt.Rectangle;
import java.util.Map;

import elementarytech.machines.evaporator.ElectricEvaporatorGui;
import elementarytech.machines.evaporator.EvaporatorTileEntity;
import elementarytech.recipe.UniversalRecipeInput;
import elementarytech.recipe.UniversalRecipeOutput;
import net.minecraft.client.gui.inventory.GuiContainer;

public class ElectricEvaporatorRecipeHandler extends EvaporatorRecipeHandler {
    @Override
    public Class<? extends GuiContainer> getGuiClass() {
        return ElectricEvaporatorGui.class;
    }

    @Override
    public String getRecipeId() {
        return "elementarytech.electricEvaporator";
    }

    @Override
    public String getGuiTexture() {
        return "elementarytech:textures/gui/GUIElectricEvaporator.png";
    }

    @Override
    public String getOverlayIdentifier() {
        return "electricEvaporator";
    }

    @Override
    public void loadTransferRects() {
        this.transferRects.add(new RecipeTransferRect(new Rectangle(99 - 5, 34 - 10, 17, 13), this.getRecipeId(), new Object[0]));
    }

    @Override
    public Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipeList() {
        return EvaporatorTileEntity.getRecipes();
    }
}
