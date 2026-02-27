package org.fuzhou.fragmentsofsound.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.fuzhou.fragmentsofsound.Fragmentsofsound;

public class NetworkHandler {

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
        ResourceLocation.tryParse(Fragmentsofsound.MODID + ":main"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );

    private static int id = 0;

    public static void register() {
        INSTANCE.messageBuilder(ForgeCraftPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
            .encoder(ForgeCraftPacket::encode)
            .decoder(ForgeCraftPacket::decode)
            .consumerMainThread(ForgeCraftPacket::handle)
            .add();
        
        INSTANCE.messageBuilder(AllocatePotentialPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
            .encoder(AllocatePotentialPacket::encode)
            .decoder(AllocatePotentialPacket::decode)
            .consumerMainThread(AllocatePotentialPacket::handle)
            .add();
        
        INSTANCE.messageBuilder(StartCinematicPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
            .encoder(StartCinematicPacket::encode)
            .decoder(StartCinematicPacket::decode)
            .consumerMainThread(StartCinematicPacket::handle)
            .add();
        
        INSTANCE.messageBuilder(StopCinematicPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
            .encoder(StopCinematicPacket::encode)
            .decoder(StopCinematicPacket::decode)
            .consumerMainThread(StopCinematicPacket::handle)
            .add();
    }
}
