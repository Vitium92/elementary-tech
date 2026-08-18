package elementarytech.util;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ic2.core.util.DrawUtil;
import elementarytech.world.ETFluid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

@SideOnly(Side.CLIENT)
public class ETRenderUtils {
    public static ETRenderUtils instance;

    private FloatBuffer colorBuffer;
    private Map<Long, Integer> frameTooltipMap;
    private int displayScaledWidth = -1;
    private int displayScaledHeight = -1;
    public int guiXPos = -1;
    public int guiYPos = -1;
    private int prevDisplayWidth = -1;
    private int prevDisplayHeight = -1;
    private final int guiContainerWidth = 166;
    private final int guiContainerHeight = 176;

    public ETRenderUtils() {
        instance = this;
        colorBuffer = GLAllocation.createDirectFloatBuffer(16);
        frameTooltipMap = new HashMap<Long, Integer>();
    }

    public void renderIHLFluidTank(ETFluidTank fluidTank, int x1, int y1, int x2, int y2, float zLevel, int par1, int par2, int xOffset, int yOffset) {
        int liquidHeight = 0;
        int prevLiquidHeight = 0;
        int i = y2 - y1;
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(1f, 1f, 1f, 1f);
        for (int i2 = 0; i2 < fluidTank.getNumberOfFluids(); i2++) {
            FluidStack fluidStack = fluidTank.getFluid(i2);
            if (fluidStack != null) {
                Fluid fluid = fluidStack.getFluid();
                if (fluid != null) {
                    IIcon fluidIcon = fluid.getIcon();
                    if (fluidIcon != null) {
                        liquidHeight = fluidTank.getFluidAmount(i2) * i / fluidTank.getCapacity();
                        DrawUtil.drawRepeated(fluidIcon, x1, y2 - liquidHeight - prevLiquidHeight, x2 - x1, liquidHeight, zLevel);
                        prevLiquidHeight += liquidHeight;
                    }
                }
            }
        }
        drawIHLFluidTankTooltip(par1, par2, x1 + guiXPos - 6, y1 + guiYPos + 6, x2 + guiXPos - 6, y2 + guiYPos + 6, fluidTank);
    }

    public void drawIHLFluidTankTooltip(int par1, int par2, int x1, int y1, int x2, int y2, ETFluidTank fluidTank) {
        String fluidListNames = "";
        List<FluidStack> fli = fluidTank.getFluidList();
        for (int i = fli.size() - 1; i >= 0; i--) {
            FluidStack fluidStack = fli.get(i);
            fluidListNames += StatCollector.translateToLocal(fluidStack.getUnlocalizedName()) + ": " + fluidStack.amount + "mB (d:" + ETFluid.getRealDensity(fluidStack.getFluid()) + ") /n ";
        }
        drawTooltip(par1, par2, x2 - x1, y2 - y1, x1, y1, fluidListNames);
    }

    public boolean drawTooltip(int cursorPosX, int cursorPosY, int width, int height, int xPos, int yPos, String text) {
        updateScreenSize();
        long key = xPos + yPos * 1024;
        Integer frame = 0;
        if (frameTooltipMap.containsKey(key)) {
            frame = frameTooltipMap.get(key);
        }
        boolean showString = true;
        if (cursorPosX < xPos || cursorPosX > xPos + width ||
                cursorPosY < yPos || cursorPosY > yPos + height) {
            if (frame > 0) {
                frame -= 20;
                frameTooltipMap.put(key, frame);
            }
            showString = false;
        } else {
            frame += 10;
            frameTooltipMap.put(key, frame);
        }
        if (frame > 0) {
            int strokeHeight = 15;
            int i, x1, x2, y1, y2, tooltipWidth, tooltipHeight;
            tooltipWidth = tooltipHeight = 0;
            String[] splittedText = text.split(" /n ");
            for (i = 0; i < splittedText.length; i++) {
                if (Minecraft.getMinecraft().fontRenderer.getStringWidth(splittedText[i]) + 8 > tooltipWidth) {
                    tooltipWidth = Math.min(frame, Minecraft.getMinecraft().fontRenderer.getStringWidth(splittedText[i]) + 8);
                }
            }
            tooltipHeight = Math.min(Math.max(frame - tooltipWidth, strokeHeight), strokeHeight * splittedText.length);
            x1 = cursorPosX - xPos;
            x2 = x1 + tooltipWidth;
            y1 = cursorPosY - guiYPos + 18;
            y2 = y1 + tooltipHeight;
            GL11.glPushAttrib(16704);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            if (showString) {
                drawRectangle(Tessellator.instance, x1, y1, x2, y2, 128);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                for (i = 0; i < splittedText.length; i++) {
                    if (i < tooltipHeight / strokeHeight) {
                        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(splittedText[i], x1 + 4, y1 + i * strokeHeight + 4, 16768125);
                    }
                }
            } else {
                drawRectangle(Tessellator.instance, x1, y1, x2, y2, Math.min(128, frame / 2));
                GL11.glEnable(GL11.GL_TEXTURE_2D);
            }
            GL11.glPopAttrib();
            frame = Math.min(tooltipWidth + tooltipHeight, frame);
            frameTooltipMap.put(key, frame);
            return true;
        }
        return false;
    }

    public void drawMissingEngineTooltip(GuiContainer gui, int par1, int par2, int xPos, int yPos, int xOffset, int yOffset) {
        gui.drawTexturedModalRect(xPos, yPos, 194, 0, 3, 14);
        drawTooltip(par1, par2, 3, 14, xPos + xOffset, yPos + yOffset, StatCollector.translateToLocal("elementarytech.gui.missing.engine"));
    }

    public void drawWorkspaceElementTooltip(int par1, int par2, int xPos, int yPos, net.minecraft.item.ItemStack workSpaceElement) {
        drawTooltip(par1, par2, 16, 16, xPos, yPos, StatCollector.translateToLocal(workSpaceElement.getUnlocalizedName() + ".tooltip"));
    }

    private void drawRectangle(Tessellator tessellator, int x1, int y1, int x2, int y2, int color) {
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA(color >>> 24 & 255, color >>> 16 & 255, color >>> 8 & 255, color & 255);
        tessellator.addVertex(x2, y1, 300.0D);
        tessellator.addVertex(x1, y1, 300.0D);
        tessellator.addVertex(x1, y2, 300.0D);
        tessellator.addVertex(x2, y2, 300.0D);
        tessellator.draw();
    }

    private void updateScreenSize() {
        ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
        if (prevDisplayWidth != Minecraft.getMinecraft().displayWidth || prevDisplayHeight != Minecraft.getMinecraft().displayHeight) {
            displayScaledWidth = scaledresolution.getScaledWidth();
            displayScaledHeight = scaledresolution.getScaledHeight();
            prevDisplayWidth = Minecraft.getMinecraft().displayWidth;
            prevDisplayHeight = Minecraft.getMinecraft().displayHeight;
        }
        guiXPos = (Minecraft.getMinecraft().currentScreen.width - guiContainerWidth) / 2;
        guiYPos = (Minecraft.getMinecraft().currentScreen.height - guiContainerHeight) / 2;
    }

    private FloatBuffer setColorBuffer(float par0, float par1, float par2, float par3) {
        colorBuffer.clear();
        colorBuffer.put(par0).put(par1).put(par2).put(par3);
        colorBuffer.flip();
        return colorBuffer;
    }
}
