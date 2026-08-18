package elementarytech.render;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

@SideOnly(value=Side.CLIENT)
public class IHLBlockRenderer implements ISimpleBlockRenderingHandler {

    private static int renderId;

    public IHLBlockRenderer() {
        renderId = RenderingRegistry.getNextAvailableRenderId();
    }

    @Override
    public int getRenderId() {
        return renderId;
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderblocks) {
        renderblocks.renderStandardBlock(block, 0, 0, 0);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess blockAccess, int x, int y, int z, Block block, int meta, RenderBlocks blockRenderer) {
        blockRenderer.renderStandardBlock(block, x, y, z);
        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }
}
