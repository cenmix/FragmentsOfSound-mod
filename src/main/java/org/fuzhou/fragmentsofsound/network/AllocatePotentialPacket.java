package org.fuzhou.fragmentsofsound.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;
import org.fuzhou.fragmentsofsound.menu.ChiselStoneForgingTableMenu;

import java.util.function.Supplier;

public class AllocatePotentialPacket {

    private final BlockPos pos;
    private final String runeType;

    public AllocatePotentialPacket(BlockPos pos, String runeType) {
        this.pos = pos;
        this.runeType = runeType;
    }

    public static void encode(AllocatePotentialPacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
        buf.writeUtf(msg.runeType);
    }

    public static AllocatePotentialPacket decode(FriendlyByteBuf buf) {
        return new AllocatePotentialPacket(buf.readBlockPos(), buf.readUtf());
    }

    public static void handle(AllocatePotentialPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                AbstractContainerMenu menu = player.containerMenu;
                if (menu instanceof ChiselStoneForgingTableMenu forgeMenu) {
                    forgeMenu.allocatePotential(msg.runeType);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
