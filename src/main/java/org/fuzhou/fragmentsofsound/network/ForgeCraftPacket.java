package org.fuzhou.fragmentsofsound.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;
import org.fuzhou.fragmentsofsound.menu.ChiselStoneForgingTableMenu;

import java.util.function.Supplier;

public class ForgeCraftPacket {

    private final BlockPos pos;

    public ForgeCraftPacket(BlockPos pos) {
        this.pos = pos;
    }

    public static void encode(ForgeCraftPacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
    }

    public static ForgeCraftPacket decode(FriendlyByteBuf buf) {
        return new ForgeCraftPacket(buf.readBlockPos());
    }

    public static void handle(ForgeCraftPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                AbstractContainerMenu menu = player.containerMenu;
                if (menu instanceof ChiselStoneForgingTableMenu forgeMenu) {
                    if (forgeMenu.canCraft()) {
                        forgeMenu.craft();
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
