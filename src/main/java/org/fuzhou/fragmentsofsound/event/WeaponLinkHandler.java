package org.fuzhou.fragmentsofsound.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.fuzhou.fragmentsofsound.Fragmentsofsound;
import org.fuzhou.fragmentsofsound.block.entity.ChiselStoneForgingTableBlockEntity;
import org.fuzhou.fragmentsofsound.compat.EpicFightCompat;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = Fragmentsofsound.MODID, value = Dist.CLIENT)
public class WeaponLinkHandler {

    private static final Map<Integer, WeaponLinkData> playerLinkData = new HashMap<>();

    public static class WeaponLinkData {
        public Map<Integer, Integer> keyBindings = new HashMap<>();
        public Map<Integer, ItemStack> linkedWeapons = new HashMap<>();
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (event.getAction() != GLFW.GLFW_PRESS) return;
        
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;
        
        int keyCode = event.getKey();
        
        WeaponLinkData linkData = playerLinkData.get(player.getId());
        if (linkData == null) return;
        
        for (Map.Entry<Integer, Integer> entry : linkData.keyBindings.entrySet()) {
            if (entry.getValue() == keyCode) {
                int slotIndex = entry.getKey();
                ItemStack targetWeapon = linkData.linkedWeapons.get(slotIndex);
                
                if (targetWeapon != null && !targetWeapon.isEmpty()) {
                    switchToWeapon(player, targetWeapon);
                }
                break;
            }
        }
    }

    private static void switchToWeapon(Player player, ItemStack targetWeapon) {
        Inventory inventory = player.getInventory();
        
        for (int i = 0; i < Inventory.INVENTORY_SIZE; i++) {
            ItemStack stack = inventory.getItem(i);
            if (ItemStack.isSameItemSameTags(stack, targetWeapon)) {
                int hotbarSlot = findEmptyOrMatchingHotbarSlot(inventory, stack);
                if (hotbarSlot != -1) {
                    if (hotbarSlot != inventory.selected) {
                        inventory.setItem(hotbarSlot, inventory.items.get(inventory.selected));
                        inventory.selected = hotbarSlot;
                    }
                }
                break;
            }
        }
    }

    private static int findEmptyOrMatchingHotbarSlot(Inventory inventory, ItemStack target) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = inventory.getItem(i);
            if (ItemStack.isSameItemSameTags(stack, target)) {
                return i;
            }
        }
        
        for (int i = 0; i < 9; i++) {
            if (inventory.getItem(i).isEmpty()) {
                return i;
            }
        }
        
        return inventory.selected;
    }

    public static void updatePlayerLinkData(Player player, Map<Integer, Integer> keyBindings, Map<Integer, ItemStack> linkedWeapons) {
        WeaponLinkData data = playerLinkData.computeIfAbsent(player.getId(), k -> new WeaponLinkData());
        data.keyBindings.clear();
        data.keyBindings.putAll(keyBindings);
        data.linkedWeapons.clear();
        data.linkedWeapons.putAll(linkedWeapons);
    }

    public static void clearPlayerLinkData(Player player) {
        playerLinkData.remove(player.getId());
    }

    public static boolean hasActiveAnimation(Player player) {
        return EpicFightCompat.isPlayerInAnimation(player);
    }
}
