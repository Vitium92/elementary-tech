package elementarytech.interfaces;

import net.minecraft.entity.player.EntityPlayerMP;
import java.io.DataInputStream;
import java.io.IOException;

public interface INetworkListener {
    int getId();

    void recieveData(DataInputStream data) throws IOException;

    void registerAndSendData(EntityPlayerMP player);

    boolean isInvalid();
}
