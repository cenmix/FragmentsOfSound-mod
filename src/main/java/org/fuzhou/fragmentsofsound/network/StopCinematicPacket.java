package org.fuzhou.fragmentsofsound.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.fuzhou.fragmentsofsound.cinematic.CinematicRenderer;

import java.util.function.Supplier;

public class StopCinematicPacket {
    
    public StopCinematicPacket() {
    }
    
    public static void encode(StopCinematicPacket packet, FriendlyByteBuf buffer) {
    }
    
    public static StopCinematicPacket decode(FriendlyByteBuf buffer) {
        return new StopCinematicPacket();
    }
    
    public static void handle(StopCinematicPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            CinematicRenderer.stopCinematic();
        });
        context.setPacketHandled(true);
    }
}
