package elementarytech;

import java.util.HashMap;
import java.util.Map;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import elementarytech.machinebase.ETMachineBaseBlock.MachineType;
import elementarytech.machines.bronzevat.BronzeVatTileEntity;
import elementarytech.machines.evaporator.SolarEvaporatorTileEntity;
import elementarytech.render.BronzeVatBlockRender;
import elementarytech.render.BronzeVatRender;
import elementarytech.tree.BlobEntityFX;
import elementarytech.tree.BlobRenderFX;
import elementarytech.tree.SackRender;
import elementarytech.tree.SackTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraft.client.renderer.GLAllocation;

@SideOnly(Side.CLIENT)
public class ClientProxy extends ServerProxy {

    private static Map<MachineType, Integer> blockRendererMap = new HashMap<MachineType, Integer>();

    @Override
    public void load() {
        if (channel == null) {
            channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(ModInfo.MODID);
            channel.register(this);
        }

        // Register ISBRH for BronzeVat and SolarEvaporator
        BronzeVatBlockRender bronzeVatBlockRender = new BronzeVatBlockRender();
        RenderingRegistry.registerBlockHandler(bronzeVatBlockRender);
        blockRendererMap.put(MachineType.BronzeVat, bronzeVatBlockRender.getRenderId());
        blockRendererMap.put(MachineType.SolarEvaporator, bronzeVatBlockRender.getRenderId());

        // Bind TESRs
        TileEntitySpecialRenderer tubRender = new BronzeVatRender(900f);
        ClientRegistry.bindTileEntitySpecialRenderer(BronzeVatTileEntity.class, tubRender);
        ClientRegistry.bindTileEntitySpecialRenderer(SolarEvaporatorTileEntity.class, tubRender);
        ClientRegistry.bindTileEntitySpecialRenderer(SackTileEntity.class, new SackRender());

        // Register entity renderers
        RenderingRegistry.registerEntityRenderingHandler(BlobEntityFX.class, new BlobRenderFX());
    }

    @Override
    public void spawnParticle(int particle, World world, double x, double y, double z, double mx, double my, double mz, float particleScale) {
        switch (particle) {
            case 0:
                world.spawnParticle("flame", x, y, z, mx, my, mz);
                break;
            case 1:
                BlobEntityFX blob = new BlobEntityFX(world, x, y, z, mx, my, mz, particleScale, BlobEntityFX.FluidType.RESIN);
                world.spawnEntityInWorld(blob);
                break;
            case 2:
                BlobEntityFX blob2 = new BlobEntityFX(world, x, y, z, mx, my, mz, particleScale, BlobEntityFX.FluidType.SAP);
                world.spawnEntityInWorld(blob2);
                break;
            default:
                world.spawnParticle("smoke", x, y, z, mx, my, mz);
                break;
        }
    }

    @Override
    public int shareBlockRendererByMachineType(MachineType type) {
        if (blockRendererMap.containsKey(type)) {
            return blockRendererMap.get(type);
        }
        return 0;
    }

    @Override
    public void initBlockRenderer() {
    }

    @Override
    public int getGLDisplayList() {
        return GLAllocation.generateDisplayLists(1);
    }

    @Override
    public java.io.File getMinecraftDir() {
        return Minecraft.getMinecraft().mcDataDir;
    }
}
