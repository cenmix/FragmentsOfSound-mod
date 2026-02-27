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
        
        INSTANCE.messageBuilder(PlaceOutpostPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
            .encoder(PlaceOutpostPacket::encode)
            .decoder(PlaceOutpostPacket::decode)
            .consumerMainThread(PlaceOutpostPacket::handle)
            .add();
        
        INSTANCE.messageBuilder(RemoveOutpostPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
            .encoder(RemoveOutpostPacket::encode)
            .decoder(RemoveOutpostPacket::decode)
            .consumerMainThread(RemoveOutpostPacket::handle)
            .add();
        
        INSTANCE.messageBuilder(SetLinkKeyPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
            .encoder(SetLinkKeyPacket::encode)
            .decoder(SetLinkKeyPacket::decode)
            .consumerMainThread(SetLinkKeyPacket::handle)
            .add();
        
        INSTANCE.messageBuilder(SyncWeaponLinkPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
            .encoder(SyncWeaponLinkPacket::encode)
            .decoder(SyncWeaponLinkPacket::decode)
            .consumerMainThread(SyncWeaponLinkPacket::handle)
            .add();
    }
}
