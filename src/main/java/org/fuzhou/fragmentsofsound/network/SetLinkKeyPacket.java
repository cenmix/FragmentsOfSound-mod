package org.fuzhou.fragmentsofsound.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;
import org.fuzhou.fragmentsofsound.menu.ChiselStoneForgingTableMenu;

import java.util.function.Supplier;

public class SetLinkKeyPacket {

    private final BlockPos pos;
    private final int slot;
    private final int keyCode;

    public SetLinkKeyPacket(BlockPos pos, int slot, int keyCode) {
        this.pos = pos;
        this.slot = slot;
        this.keyCode = keyCode;
    }

    public static void encode(SetLinkKeyPacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
        buf.writeInt(msg.slot);
        buf.writeInt(msg.keyCode);
    }

    public static SetLinkKeyPacket decode(FriendlyByteBuf buf) {
        return new SetLinkKeyPacket(buf.readBlockPos(), buf.readInt(), buf.readInt());
    }

    public static void handle(SetLinkKeyPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                AbstractContainerMenu menu = player.containerMenu;
                if (menu instanceof ChiselStoneForgingTableMenu forgeMenu) {
                    forgeMenu.setLinkKeyBinding(msg.slot, msg.keyCode);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
