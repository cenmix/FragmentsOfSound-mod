package org.fuzhou.fragmentsofsound.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.fuzhou.fragmentsofsound.Fragmentsofsound;

import java.util.function.Supplier;

public class RemoveOutpostPacket {
    
    private final BlockPos pos;
    
    public RemoveOutpostPacket(BlockPos pos) {
        this.pos = pos;
    }
    
    public static void encode(RemoveOutpostPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos);
    }
    
    public static RemoveOutpostPacket decode(FriendlyByteBuf buffer) {
        return new RemoveOutpostPacket(buffer.readBlockPos());
    }
    
    public static void handle(RemoveOutpostPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (context.getSender() instanceof ServerPlayer) {
                ServerPlayer player = (ServerPlayer) context.getSender();
                ServerLevel level = player.serverLevel();
                level.setBlock(packet.pos, Fragmentsofsound.MONSTER_OUTPOST_1.get().defaultBlockState().getFluidState().createLegacyBlock(), 2);
            }
        });
        context.setPacketHandled(true);
    }
}