package elementarytech.machines.bronzetub;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import elementarytech.util.ETRenderUtils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class BronzeTubGui extends GuiContainer {
	private static final ResourceLocation background = new ResourceLocation("elementarytech", "textures/gui/GUITubBronze.png");
	private BronzeTubContainer container;

	public BronzeTubGui(BronzeTubContainer container1) {
		super(container1);
		this.container = container1;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		int xOffset = (this.width - xSize) / 2;
		int yOffset = (this.height - ySize) / 2;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
		this.mc.renderEngine.bindTexture(background);
		int i1;
		if (this.container.tileEntity.progress > 0) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			i1 = Math.min(this.container.tileEntity.gaugeProgressScaled(27), 27);
			this.drawTexturedModalRect(138, 28, getFrameX(i1), getFrameY(i1), 24, 24);
		}
		if (this.container.tileEntity.getFluidTank().getFluidAmount() > 0) {
			ETRenderUtils.instance.renderIHLFluidTank(this.container.tileEntity.getFluidTank(), 50, 11, 107, 72, zLevel,
					par1, par2, xOffset, yOffset);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(background);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}

	private int getFrameY(int number) {
		return (number % 10) * 24 + 14;
	}

	private int getFrameX(int number) {
		return (number / 10) * 24 + 176;
	}
}
