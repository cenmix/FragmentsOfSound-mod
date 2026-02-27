package org.fuzhou.fragmentsofsound.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.fuzhou.fragmentsofsound.menu.ChiselStoneForgingTableMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ChiselStoneForgingTableBlockEntity extends BlockEntity implements MenuProvider {

    private final ItemStackHandler itemHandler = new ItemStackHandler(7) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    public static final int WEAPON_SLOT = 0;
    public static final int RUNE_SLOT = 1;
    public static final int OUTPUT_SLOT = 2;
    public static final int LINK_SLOT_START = 3;
    public static final int LINK_SLOT_1 = 3;
    public static final int LINK_SLOT_2 = 4;
    public static final int LINK_SLOT_3 = 5;
    public static final int LINK_SLOT_4 = 6;

    private Map<Integer, Integer> linkKeyBindings = new HashMap<>();

    public ChiselStoneForgingTableBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityTypes.CHISEL_STONE_FORGING_TABLE.get(), pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.fragmentsofsound.chisel_stone_forging_table");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new ChiselStoneForgingTableMenu(id, inventory, this);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    public ItemStack getWeaponStack() {
        return itemHandler.getStackInSlot(WEAPON_SLOT);
    }

    public ItemStack getRuneStack() {
        return itemHandler.getStackInSlot(RUNE_SLOT);
    }

    public ItemStack getOutputStack() {
        return itemHandler.getStackInSlot(OUTPUT_SLOT);
    }

    public ItemStack getLinkSlotStack(int slot) {
        if (slot >= LINK_SLOT_1 && slot <= LINK_SLOT_4) {
            return itemHandler.getStackInSlot(slot);
        }
        return ItemStack.EMPTY;
    }

    public void setLinkSlotStack(int slot, ItemStack stack) {
        if (slot >= LINK_SLOT_1 && slot <= LINK_SLOT_4) {
            itemHandler.setStackInSlot(slot, stack);
        }
    }

    public int getLinkKeyBinding(int slot) {
        return linkKeyBindings.getOrDefault(slot, -1);
    }

    public void setLinkKeyBinding(int slot, int keyCode) {
        linkKeyBindings.put(slot, keyCode);
        setChanged();
    }

    public Map<Integer, Integer> getLinkKeyBindings() {
        return linkKeyBindings;
    }

    public void setLinkKeyBindings(Map<Integer, Integer> bindings) {
        this.linkKeyBindings = new HashMap<>(bindings);
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        
        CompoundTag keyBindingsTag = new CompoundTag();
        for (Map.Entry<Integer, Integer> entry : linkKeyBindings.entrySet()) {
            keyBindingsTag.putInt("slot_" + entry.getKey(), entry.getValue());
        }
        tag.put("linkKeyBindings", keyBindingsTag);
        
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("inventory")) {
            itemHandler.deserializeNBT(tag.getCompound("inventory"));
        }
        
        linkKeyBindings.clear();
        if (tag.contains("linkKeyBindings")) {
            CompoundTag keyBindingsTag = tag.getCompound("linkKeyBindings");
            for (String key : keyBindingsTag.getAllKeys()) {
                if (key.startsWith("slot_")) {
                    try {
                        int slot = Integer.parseInt(key.substring(5));
                        int keyCode = keyBindingsTag.getInt(key);
                        linkKeyBindings.put(slot, keyCode);
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.put("inventory", itemHandler.serializeNBT());
        
        CompoundTag keyBindingsTag = new CompoundTag();
        for (Map.Entry<Integer, Integer> entry : linkKeyBindings.entrySet()) {
            keyBindingsTag.putInt("slot_" + entry.getKey(), entry.getValue());
        }
        tag.put("linkKeyBindings", keyBindingsTag);
        
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        if (tag.contains("inventory")) {
            itemHandler.deserializeNBT(tag.getCompound("inventory"));
        }
        
        linkKeyBindings.clear();
        if (tag.contains("linkKeyBindings")) {
            CompoundTag keyBindingsTag = tag.getCompound("linkKeyBindings");
            for (String key : keyBindingsTag.getAllKeys()) {
                if (key.startsWith("slot_")) {
                    try {
                        int slot = Integer.parseInt(key.substring(5));
                        int keyCode = keyBindingsTag.getInt(key);
                        linkKeyBindings.put(slot, keyCode);
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }
}
