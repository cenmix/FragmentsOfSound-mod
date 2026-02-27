package org.fuzhou.fragmentsofsound.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import org.fuzhou.fragmentsofsound.block.entity.ChiselStoneForgingTableBlockEntity;
import org.fuzhou.fragmentsofsound.event.WeaponLinkHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SyncWeaponLinkPacket {

    private final Map<Integer, Integer> keyBindings;
    private final Map<Integer, ItemStack> linkedWeapons;

    public SyncWeaponLinkPacket(Map<Integer, Integer> keyBindings, Map<Integer, ItemStack> linkedWeapons) {
        this.keyBindings = keyBindings;
        this.linkedWeapons = linkedWeapons;
    }

    public static void encode(SyncWeaponLinkPacket msg, FriendlyByteBuf buf) {
        CompoundTag tag = new CompoundTag();
        
        CompoundTag keyBindingsTag = new CompoundTag();
        for (Map.Entry<Integer, Integer> entry : msg.keyBindings.entrySet()) {
            keyBindingsTag.putInt("slot_" + entry.getKey(), entry.getValue());
        }
        tag.put("keyBindings", keyBindingsTag);
        
        ListTag weaponsList = new ListTag();
        for (Map.Entry<Integer, ItemStack> entry : msg.linkedWeapons.entrySet()) {
            CompoundTag weaponTag = new CompoundTag();
            weaponTag.putInt("slot", entry.getKey());
            weaponTag.put("item", entry.getValue().save(new CompoundTag()));
            weaponsList.add(weaponTag);
        }
        tag.put("linkedWeapons", weaponsList);
        
        buf.writeNbt(tag);
    }

    public static SyncWeaponLinkPacket decode(FriendlyByteBuf buf) {
        CompoundTag tag = buf.readNbt();
        
        Map<Integer, Integer> keyBindings = new HashMap<>();
        if (tag != null && tag.contains("keyBindings")) {
            CompoundTag keyBindingsTag = tag.getCompound("keyBindings");
            for (String key : keyBindingsTag.getAllKeys()) {
                if (key.startsWith("slot_")) {
                    try {
                        int slot = Integer.parseInt(key.substring(5));
                        int keyCode = keyBindingsTag.getInt(key);
                        keyBindings.put(slot, keyCode);
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }
        
        Map<Integer, ItemStack> linkedWeapons = new HashMap<>();
        if (tag != null && tag.contains("linkedWeapons")) {
            ListTag weaponsList = tag.getList("linkedWeapons", 10);
            for (int i = 0; i < weaponsList.size(); i++) {
                CompoundTag weaponTag = weaponsList.getCompound(i);
                int slot = weaponTag.getInt("slot");
                ItemStack stack = ItemStack.of(weaponTag.getCompound("item"));
                linkedWeapons.put(slot, stack);
            }
        }
        
        return new SyncWeaponLinkPacket(keyBindings, linkedWeapons);
    }

    public static void handle(SyncWeaponLinkPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            WeaponLinkHandler.updatePlayerLinkData(
                net.minecraft.client.Minecraft.getInstance().player,
                msg.keyBindings,
                msg.linkedWeapons
            );
        });
        ctx.get().setPacketHandled(true);
    }
}
