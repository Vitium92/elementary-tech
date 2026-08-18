package elementarytech.nei;

import java.awt.Rectangle;
import java.util.Map;

import codechicken.lib.gui.GuiDraw;
import elementarytech.machines.bronzevat.BronzeVatGui;
import elementarytech.machines.bronzevat.BronzeVatTileEntity;
import elementarytech.recipe.UniversalRecipeInput;
import elementarytech.recipe.UniversalRecipeOutput;
import net.minecraft.client.gui.inventory.GuiContainer;

public class BronzeVatRecipeHandler extends MachineRecipeHandler {
    @Override
    public Class<? extends GuiContainer> getGuiClass() {
        return BronzeVatGui.class;
    }

    @Override
    protected int[] getInputPosX() {
        return new int[]{116 - 5};
    }

    @Override
    protected int[] getInputPosY() {
        return new int[]{16 - 11};
    }

    @Override
    protected int[] getFluidInputPosX() {
        return new int[]{24 - 5, 6 - 5};
    }

    @Override
    protected int[] getFluidInputPosY() {
        return new int[]{16 - 11};
    }

    @Override
    protected int[] getOutputPosX() {
        return new int[]{116 - 5};
    }

    @Override
    protected int[] getOutputPosY() {
        return new int[]{41 - 11, 59 - 11};
    }

    @Override
    protected int[] getFluidOutputPosX() {
        return new int[]{24 - 5};
    }

    @Override
    protected int[] getFluidOutputPosY() {
        return new int[]{52 - 11};
    }

    @Override
    public String getRecipeId() {
        return "elementarytech.bronzeVat";
    }

    @Override
    public String getGuiTexture() {
        return "elementarytech:textures/gui/GUITubVat.png";
    }

    @Override
    public String getOverlayIdentifier() {
        return "bronzeVat";
    }

    @Override
    public void loadTransferRects() {
        this.transferRects.add(new RecipeTransferRect(new Rectangle(134 - 5, 0, 35, 65), this.getRecipeId(), new Object[0]));
    }

    @Override
    public void drawBackground(int i) {
        super.drawBackground(i);
        GuiDraw.drawTexturedModalRect(5 - 5, 15 - 11, 23, 15, 18, 18);
    }

    @Override
    public Map<UniversalRecipeInput, UniversalRecipeOutput> getRecipeList() {
        return BronzeVatTileEntity.getRecipes();
    }
}