package org.fuzhou.fragmentsofsound.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.fuzhou.fragmentsofsound.block.entity.ChiselStoneForgingTableBlockEntity;
import org.fuzhou.fragmentsofsound.item.ArmorBreakRuneItem;
import org.fuzhou.fragmentsofsound.item.ChiselStoneItem;
import org.fuzhou.fragmentsofsound.item.PierceRuneItem;
import org.fuzhou.fragmentsofsound.rune.Rune;
import org.fuzhou.fragmentsofsound.rune.RuneData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChiselStoneForgingTableMenu extends AbstractContainerMenu {

    private final ChiselStoneForgingTableBlockEntity blockEntity;
    private final ContainerLevelAccess levelAccess;

    public ChiselStoneForgingTableMenu(int id, Inventory inventory, FriendlyByteBuf buf) {
        this(id, inventory, inventory.player.level().getBlockEntity(buf.readBlockPos()));
    }

    public ChiselStoneForgingTableMenu(int id, Inventory inventory, BlockEntity blockEntity) {
        super(ModMenuTypes.CHISEL_STONE_FORGING_TABLE_MENU.get(), id);
        this.blockEntity = (ChiselStoneForgingTableBlockEntity) blockEntity;
        this.levelAccess = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());

        addPlayerInventory(inventory);
        addPlayerHotbar(inventory);

        this.addSlot(new WeaponToolSlot(this.blockEntity.getItemHandler(), ChiselStoneForgingTableBlockEntity.WEAPON_SLOT, 8, 17));
        this.addSlot(new RuneSlot(this.blockEntity.getItemHandler(), ChiselStoneForgingTableBlockEntity.RUNE_SLOT, 8, 53));
        this.addSlot(new OutputSlot(this.blockEntity.getItemHandler(), ChiselStoneForgingTableBlockEntity.OUTPUT_SLOT, 140, 35));
    }

    private void addPlayerInventory(Inventory inventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(inventory, l + i * 9 + 9, 8 + l * 18, 118 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory inventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(inventory, i, 8 + i * 18, 176));
        }
    }

    public boolean canCraft() {
        ItemStack weaponStack = blockEntity.getWeaponStack();
        ItemStack runeStack = blockEntity.getRuneStack();
        ItemStack outputStack = blockEntity.getOutputStack();
        
        if (weaponStack.isEmpty() || runeStack.isEmpty()) return false;
        if (!outputStack.isEmpty()) return false;
        
        if (runeStack.getItem() instanceof ArmorBreakRuneItem) {
            return !hasRuneOfType(weaponStack, "armor_break");
        }
        
        if (runeStack.getItem() instanceof PierceRuneItem) {
            return !hasRuneOfType(weaponStack, "pierce");
        }
        
        if (runeStack.getItem() instanceof ChiselStoneItem) {
            boolean weaponHasRune = RuneData.hasRune(weaponStack);
            boolean weaponHasChisel = RuneData.hasEmbeddedChisel(weaponStack);
            boolean chiselHasRune = RuneData.hasRune(runeStack);
            
            if (weaponHasRune && !weaponHasChisel) {
                return true;
            }
            
            if (!weaponHasRune && chiselHasRune) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean hasRuneOfType(ItemStack stack, String runeType) {
        List<net.minecraft.nbt.CompoundTag> runes = RuneData.getRunes(stack);
        for (net.minecraft.nbt.CompoundTag rune : runes) {
            if (rune.getString("RuneType").equals(runeType)) {
                return true;
            }
        }
        return false;
    }

    public void craft() {
        if (!canCraft()) return;
        
        ItemStack weaponStack = blockEntity.getWeaponStack().copy();
        ItemStack runeStack = blockEntity.getRuneStack();
        
        if (runeStack.getItem() instanceof ArmorBreakRuneItem) {
            Rune rune = Rune.getRuneByType("armor_break");
            if (rune != null) {
                RuneData.addRune(weaponStack, rune);
            }
        } else if (runeStack.getItem() instanceof PierceRuneItem) {
            Rune rune = Rune.getRuneByType("pierce");
            if (rune != null) {
                RuneData.addRune(weaponStack, rune);
            }
        } else if (runeStack.getItem() instanceof ChiselStoneItem chiselItem) {
            boolean weaponHasRune = RuneData.hasRune(weaponStack);
            boolean chiselHasRune = RuneData.hasRune(runeStack);
            
            if (weaponHasRune && !RuneData.hasEmbeddedChisel(weaponStack)) {
                int level = chiselItem.getChiselLevel();
                int purity = ChiselStoneItem.getPurity(runeStack);
                RuneData.embedChiselStone(weaponStack, level, purity);
            } else if (!weaponHasRune && chiselHasRune) {
                RuneData.copyFrom(weaponStack, runeStack);
            }
        }
        
        blockEntity.getItemHandler().setStackInSlot(ChiselStoneForgingTableBlockEntity.OUTPUT_SLOT, weaponStack);
        blockEntity.getItemHandler().setStackInSlot(ChiselStoneForgingTableBlockEntity.WEAPON_SLOT, ItemStack.EMPTY);
        blockEntity.getItemHandler().setStackInSlot(ChiselStoneForgingTableBlockEntity.RUNE_SLOT, ItemStack.EMPTY);
        
        syncToClient();
    }

    public boolean canAllocate() {
        ItemStack outputStack = blockEntity.getOutputStack();
        ItemStack weaponStack = blockEntity.getWeaponStack();
        
        ItemStack targetStack = !outputStack.isEmpty() ? outputStack : weaponStack;
        
        if (targetStack.isEmpty()) return false;
        if (!RuneData.hasRune(targetStack)) return false;
        
        int unallocated = RuneData.getUnallocatedPurity(targetStack);
        return unallocated > 0;
    }

    public void allocatePotential(String runeType) {
        ItemStack outputStack = blockEntity.getOutputStack();
        ItemStack weaponStack = blockEntity.getWeaponStack();
        
        ItemStack targetStack = !outputStack.isEmpty() ? outputStack : weaponStack;
        
        if (targetStack.isEmpty()) return;
        
        if (RuneData.allocatePotential(targetStack, runeType, 1)) {
            int slot = !outputStack.isEmpty() ? ChiselStoneForgingTableBlockEntity.OUTPUT_SLOT : ChiselStoneForgingTableBlockEntity.WEAPON_SLOT;
            blockEntity.getItemHandler().setStackInSlot(slot, targetStack.copy());
            syncToClient();
        }
    }
    
    private void syncToClient() {
        if (blockEntity.getLevel() != null && !blockEntity.getLevel().isClientSide) {
            blockEntity.setChanged();
            blockEntity.getLevel().sendBlockUpdated(blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity.getBlockState(), 3);
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyStack = sourceStack.copy();

        if (index < 36) {
            if (!moveItemStackTo(sourceStack, 36, 39, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index < 39) {
            if (!moveItemStackTo(sourceStack, 0, 36, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }

        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }

        sourceSlot.onTake(player, sourceStack);
        return copyStack;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(levelAccess, player, blockEntity.getBlockState().getBlock());
    }

    public ChiselStoneForgingTableBlockEntity getBlockEntity() {
        return blockEntity;
    }

    public ItemStack getWeaponStack() {
        return blockEntity.getWeaponStack();
    }
}
