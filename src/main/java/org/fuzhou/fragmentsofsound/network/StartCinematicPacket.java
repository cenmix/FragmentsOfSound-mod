package org.fuzhou.fragmentsofsound.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.fuzhou.fragmentsofsound.cinematic.CinematicData;
import org.fuzhou.fragmentsofsound.cinematic.CinematicRenderer;

import java.util.function.Supplier;

public class StartCinematicPacket {
    
    private final CinematicData cinematicData;
    
    public StartCinematicPacket(CinematicData data) {
        this.cinematicData = data;
    }
    
    public static void encode(StartCinematicPacket packet, FriendlyByteBuf buffer) {
        buffer.writeNbt(packet.cinematicData.save());
    }
    
    public static StartCinematicPacket decode(FriendlyByteBuf buffer) {
        return new StartCinematicPacket(CinematicData.load(buffer.readNbt()));
    }
    
    public static void handle(StartCinematicPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            CinematicRenderer.startCinematic(packet.cinematicData);
        });
        context.setPacketHandled(true);
    }
}
