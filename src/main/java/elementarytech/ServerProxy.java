package elementarytech;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import elementarytech.interfaces.INetworkListener;
import elementarytech.machinebase.ETMachineBaseBlock.MachineType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;

public class ServerProxy {

    protected static FMLEventChannel channel;
    protected Map<Integer, INetworkListener> entityList = new HashMap<Integer, INetworkListener>();
    protected Set<INetworkListener> entityServerList = new HashSet<INetworkListener>();
    protected Map<Integer, ByteBuf> delayedEntityDataPacket = new HashMap<Integer, ByteBuf>();

    public ServerProxy() {}

    public void load() {
        if (channel == null) {
            channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(ModInfo.MODID);
            channel.register(this);
        }
    }

    public void spawnParticle(int particle, World world, double x, double y, double z, double mx, double my, double mz, float particleScale) {}

    public void spawnParticleFromServer(int particle, World world, double x, double y, double z, double mx, double my, double mz, float particleScale) {
        ByteBuf bb = Unpooled.buffer(36);
        ByteBufOutputStream bbos = new ByteBufOutputStream(bb);
        try {
            bbos.write(0);
            bbos.write(particle);
            bbos.writeFloat((float) x);
            bbos.writeFloat((float) y);
            bbos.writeFloat((float) z);
            bbos.writeFloat((float) mx);
            bbos.writeFloat((float) my);
            bbos.writeFloat((float) mz);
            bbos.writeFloat(particleScale);
            channel.sendToAllAround(new FMLProxyPacket(bbos.buffer(), ModInfo.MODID), new TargetPoint(world.provider.dimensionId, x, y, z, 32d));
            bbos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int shareBlockRendererByMachineType(MachineType type) { return 0; }
    public void initBlockRenderer() {}
    public int getGLDisplayList() { return -1; }
    public File getMinecraftDir() { return new File("."); }

    public void addEntityToList(INetworkListener entity) {
        this.entityList.put(entity.getId(), entity);
    }

    public void addEntityToServerList(INetworkListener entity) {
        this.entityServerList.add(entity);
    }

    @SubscribeEvent
    public void onPacketFromClientToServer(cpw.mods.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent event) throws IOException {
        ByteBuf data = event.packet.payload();
        ByteBufInputStream bbis = new ByteBufInputStream(data);
        switch (bbis.read()) {
            case 0:
                int playerEntityId = bbis.readInt();
                int worldDimensionId = bbis.readInt();
                int containerSlotNumber = bbis.readInt();
                int fieldValue = bbis.readInt();
                String fieldName = bbis.readUTF();
                EntityPlayerMP player = (EntityPlayerMP) MinecraftServer.getServer().worldServerForDimension(worldDimensionId).getEntityByID(playerEntityId);
                ItemStack stack = ((Slot) player.openContainer.inventorySlots.get(containerSlotNumber)).getStack();
                if (stack.stackTagCompound == null) stack.stackTagCompound = new NBTTagCompound();
                stack.stackTagCompound.setInteger(fieldName, fieldValue);
                player.openContainer.detectAndSendChanges();
                break;
            case 1:
                playerEntityId = bbis.readInt();
                worldDimensionId = bbis.readInt();
                int x = bbis.readInt();
                int y = bbis.readInt();
                int z = bbis.readInt();
                World world = MinecraftServer.getServer().worldServerForDimension(worldDimensionId);
                TileEntity te = world.getTileEntity(x, y, z);
                if (te != null && !te.isInvalid()) {
                    NBTTagCompound nbt = new NBTTagCompound();
                    te.writeToNBT(nbt);
                    player = (EntityPlayerMP) world.getEntityByID(playerEntityId);
                    player.playerNetServerHandler.sendPacket(new S35PacketUpdateTileEntity(x, y, z, 6, nbt));
                }
                break;
            case 2:
                worldDimensionId = bbis.readInt();
                x = bbis.readInt();
                y = bbis.readInt();
                z = bbis.readInt();
                world = MinecraftServer.getServer().worldServerForDimension(worldDimensionId);
                te = world.getTileEntity(x, y, z);
                if (te != null && !te.isInvalid()) {
                    int value = bbis.readInt();
                    fieldName = bbis.readUTF();
                    try {
                        te.getClass().getDeclaredField(fieldName).set(te, value);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
        bbis.close();
    }

    @SubscribeEvent
    public void onPlayerConnectedToServer(PlayerLoggedInEvent event) {
        Iterator<INetworkListener> inli = this.entityServerList.iterator();
        while (inli.hasNext()) {
            INetworkListener inl = inli.next();
            if (inl.isInvalid()) {
                inli.remove();
            } else if (event.player instanceof EntityPlayerMP) {
                inl.registerAndSendData((EntityPlayerMP) event.player);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTeleport(PlayerChangedDimensionEvent event) {
        Iterator<INetworkListener> inli = this.entityServerList.iterator();
        while (inli.hasNext()) {
            INetworkListener inl = inli.next();
            if (inl.isInvalid()) {
                inli.remove();
            } else if (event.player instanceof EntityPlayerMP) {
                inl.registerAndSendData((EntityPlayerMP) event.player);
            }
        }
    }

    public boolean renderTESpecialSelectionBox(TileEntity te, EntityPlayer player, ItemStack currentItem, MovingObjectPosition target, float partialTicks) {
        return false;
    }

    public void sendItemStackNBTTagFromClientToServerPlayer(EntityPlayer player, int slotNumber, String fieldName, int fieldValue) {}
    public void createExplosionEffect(World world, int x, int y, int z, float radius) {}
    public void requestTileEntityInitdataFromClientToServer(int x, int y, int z) {}
}
