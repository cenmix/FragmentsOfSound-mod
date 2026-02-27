package org.fuzhou.fragmentsofsound.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.fuzhou.fragmentsofsound.Fragmentsofsound;

import java.util.function.Supplier;

public class PlaceOutpostPacket {
    
    private final BlockPos pos;
    private final int outpostLevel;
    
    public PlaceOutpostPacket(BlockPos pos, int outpostLevel) {
        this.pos = pos;
        this.outpostLevel = outpostLevel;
    }
    
    public static void encode(PlaceOutpostPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos);
        buffer.writeInt(packet.outpostLevel);
    }
    
    public static PlaceOutpostPacket decode(FriendlyByteBuf buffer) {
        return new PlaceOutpostPacket(buffer.readBlockPos(), buffer.readInt());
    }
    
    public static void handle(PlaceOutpostPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (context.getSender() instanceof ServerPlayer) {
                ServerPlayer player = (ServerPlayer) context.getSender();
                ServerLevel level = player.serverLevel();
                
                var outpostState = switch (packet.outpostLevel) {
                    case 1 -> Fragmentsofsound.MONSTER_OUTPOST_1.get().defaultBlockState();
                    case 2 -> Fragmentsofsound.MONSTER_OUTPOST_2.get().defaultBlockState();
                    case 3 -> Fragmentsofsound.MONSTER_OUTPOST_3.get().defaultBlockState();
                    case 4 -> Fragmentsofsound.MONSTER_OUTPOST_4.get().defaultBlockState();
                    case 5 -> Fragmentsofsound.MONSTER_OUTPOST_5.get().defaultBlockState();
                    case 6 -> Fragmentsofsound.MONSTER_OUTPOST_6.get().defaultBlockState();
                    case 7 -> Fragmentsofsound.MONSTER_OUTPOST_7.get().defaultBlockState();
                    default -> Fragmentsofsound.MONSTER_OUTPOST_1.get().defaultBlockState();
                };
                
                if (outpostState != null) {
                    level.setBlock(packet.pos, outpostState, 2);
                }
            }
        });
        context.setPacketHandled(true);
    }
}