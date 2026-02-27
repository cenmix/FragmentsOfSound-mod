package org.fuzhou.fragmentsofsound.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.ShieldItem;
import org.fuzhou.fragmentsofsound.block.entity.ChiselStoneForgingTableBlockEntity;
import org.fuzhou.fragmentsofsound.menu.ChiselStoneForgingTableMenu;
import org.fuzhou.fragmentsofsound.network.AllocatePotentialPacket;
import org.fuzhou.fragmentsofsound.network.ForgeCraftPacket;
import org.fuzhou.fragmentsofsound.network.NetworkHandler;
import org.fuzhou.fragmentsofsound.network.SetLinkKeyPacket;
import org.fuzhou.fragmentsofsound.rune.Rune;
import org.fuzhou.fragmentsofsound.rune.RuneData;
import org.fuzhou.fragmentsofsound.item.ChiselStoneItem;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChiselStoneForgingTableScreen extends AbstractContainerScreen<ChiselStoneForgingTableMenu> {

    private static final ResourceLocation ACTIVATED_POTENTIAL = ResourceLocation.tryParse("fragmentsofsound:textures/gui/unlock_potential.png");
    private static final ResourceLocation UNACTIVATED_POTENTIAL = ResourceLocation.tryParse("fragmentsofsound:textures/gui/unactivated_potential.png");

    private static final int INFO_AREA_X = 90;
    private static final int INFO_AREA_WIDTH = 156;
    private static final int MAX_POTENTIAL_ICONS_PER_ROW = 17;
    private static final int LINE_HEIGHT = 10;
    private static final int MAX_INFO_LINES = 7;

    private Button craftButton;
    private Button prevPageButton;
    private Button nextPageButton;
    private Map<String, int[]> runeButtonPositions = new HashMap<>();

    private int currentPage = 0;
    private int infoPage = 0;
    private List<Rune> allRunes = new ArrayList<>();
    private List<String> currentInfoLines = new ArrayList<>();

    private int waitingForKeySlot = -1;
    private int[] linkKeyBindings = new int[4];

    public ChiselStoneForgingTableScreen(ChiselStoneForgingTableMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 256;
        this.imageHeight = 200;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();

        craftButton = Button.builder(Component.literal("§e刻入契纹"), button -> {
            NetworkHandler.INSTANCE.sendToServer(new ForgeCraftPacket(menu.getBlockEntity().getBlockPos()));
        }).bounds(leftPos + 170, topPos + 17, 60, 18).build();

        addRenderableWidget(craftButton);

        prevPageButton = Button.builder(Component.literal("◀"), button -> {
            if (currentPage > 0) {
                currentPage--;
                infoPage = 0;
            } else if (infoPage > 0) {
                infoPage--;
            }
        }).bounds(leftPos + 90, topPos + 95, 20, 16).build();

        nextPageButton = Button.builder(Component.literal("▶"), button -> {
            if (currentPage == 0 && currentInfoLines.size() > MAX_INFO_LINES) {
                int maxInfoPages = (currentInfoLines.size() + MAX_INFO_LINES - 1) / MAX_INFO_LINES;
                if (infoPage < maxInfoPages - 1) {
                    infoPage++;
                    return;
                }
            }
            currentPage++;
            infoPage = 0;
        }).bounds(leftPos + 126, topPos + 95, 20, 16).build();

        addRenderableWidget(prevPageButton);
        addRenderableWidget(nextPageButton);

        loadLinkKeyBindings();
    }

    private void loadLinkKeyBindings() {
        Map<Integer, Integer> bindings = menu.getLinkKeyBindings();
        for (int i = 0; i < 4; i++) {
            int slot = ChiselStoneForgingTableBlockEntity.LINK_SLOT_1 + i;
            linkKeyBindings[i] = bindings.getOrDefault(slot, -1);
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);

        runeButtonPositions.clear();

        ItemStack displayStack = ItemStack.EMPTY;
        ItemStack weaponStack = ItemStack.EMPTY;

        Slot outputSlot = menu.getSlot(38);
        Slot weaponSlot = menu.getSlot(36);

        if (outputSlot != null && outputSlot.hasItem()) {
            displayStack = outputSlot.getItem();
        } else if (weaponSlot != null && weaponSlot.hasItem()) {
            displayStack = weaponSlot.getItem();
            weaponStack = weaponSlot.getItem();
        }

        int totalPages = 1;
        
        if (!weaponStack.isEmpty() || !displayStack.isEmpty()) {
            boolean isChisel = displayStack.getItem() instanceof ChiselStoneItem;

            if (isChisel && !RuneData.hasRune(displayStack)) {
                renderChiselInfo(guiGraphics, displayStack);
                prevPageButton.active = false;
                nextPageButton.active = false;
            } else if (RuneData.hasRune(displayStack)) {
                allRunes.clear();
                List<net.minecraft.nbt.CompoundTag> runeTags = RuneData.getRunes(displayStack);
                for (net.minecraft.nbt.CompoundTag tag : runeTags) {
                    Rune rune = Rune.getRuneByType(tag.getString("RuneType"));
                    if (rune != null) {
                        allRunes.add(rune);
                    }
                }

                totalPages = allRunes.size() + 1;
                
                if (!weaponStack.isEmpty()) {
                    totalPages += 1;
                }

                if (currentPage == 0) {
                    renderWeaponInfo(guiGraphics, displayStack);
                } else if (currentPage <= allRunes.size()) {
                    int runeIndex = currentPage - 1;
                    if (runeIndex < allRunes.size()) {
                        Rune rune = allRunes.get(runeIndex);
                        renderRunePage(guiGraphics, displayStack, rune);
                    }
                } else {
                    renderWeaponLinkPage(guiGraphics, weaponStack);
                }

                updatePageButtons(guiGraphics, totalPages);
            } else if (!weaponStack.isEmpty()) {
                totalPages = 2;
                if (currentPage == 0) {
                    renderWeaponBasicInfo(guiGraphics, weaponStack);
                } else {
                    renderWeaponLinkPage(guiGraphics, weaponStack);
                }
                updatePageButtons(guiGraphics, totalPages);
            }
        } else {
            prevPageButton.active = false;
            nextPageButton.active = false;
        }
    }

    private void renderWeaponBasicInfo(GuiGraphics guiGraphics, ItemStack stack) {
        currentInfoLines.clear();
        
        currentInfoLines.add("§d✦ 武器信息");
        currentInfoLines.add("§f" + stack.getDisplayName().getString());

        String weaponType = getWeaponType(stack);
        if (!weaponType.isEmpty()) {
            currentInfoLines.add("§c类型: §f" + weaponType);
        }

        List<String> enchants = getEnchants(stack);
        if (!enchants.isEmpty()) {
            currentInfoLines.add("§5附魔:");
            for (String ench : enchants) {
                currentInfoLines.add("§7  " + ench);
            }
        }

        int startLine = infoPage * MAX_INFO_LINES;
        int endLine = Math.min(startLine + MAX_INFO_LINES, currentInfoLines.size());
        
        int infoY = 17;
        for (int i = startLine; i < endLine; i++) {
            String line = currentInfoLines.get(i);
            guiGraphics.drawString(this.font, line, INFO_AREA_X, infoY, 0xFFFFFF, true);
            infoY += LINE_HEIGHT;
        }
    }

    private void renderWeaponLinkPage(GuiGraphics guiGraphics, ItemStack mainWeapon) {
        guiGraphics.drawString(this.font, "§d✦ 攻击链接", INFO_AREA_X, 17, 0xFFFFFF, true);
        
        String[] slotLabels = {"主武器", "链接1", "链接2", "链接3"};
        for (int i = 0; i < 4; i++) {
            int y = 35 + i * 30;
            
            guiGraphics.drawString(this.font, "§e" + slotLabels[i] + ":", INFO_AREA_X, y, 0xFFFFFF, true);
            
            ItemStack linkStack;
            if (i == 0) {
                linkStack = mainWeapon;
            } else {
                linkStack = menu.getLinkSlotStack(ChiselStoneForgingTableBlockEntity.LINK_SLOT_1 + i);
            }
            
            if (!linkStack.isEmpty()) {
                String itemName = linkStack.getDisplayName().getString();
                if (itemName.length() > 12) {
                    itemName = itemName.substring(0, 12) + "...";
                }
                guiGraphics.drawString(this.font, "§f" + itemName, INFO_AREA_X + 50, y, 0xFFFFFF, true);
            } else {
                guiGraphics.drawString(this.font, "§7空", INFO_AREA_X + 50, y, 0x888888, true);
            }
            
            int keyCode = linkKeyBindings[i];
            String keyText = keyCode >= 0 ? GLFW.glfwGetKeyName(keyCode, 0) : null;
            if (keyText != null) {
                guiGraphics.drawString(this.font, "§a[" + keyText.toUpperCase() + "]", INFO_AREA_X + 130, y, 0xFFFFFF, true);
            } else {
                guiGraphics.drawString(this.font, "§7[未设置]", INFO_AREA_X + 130, y, 0x888888, true);
            }
            
            if (waitingForKeySlot == i) {
                guiGraphics.drawString(this.font, "§e<点击设置按键>", INFO_AREA_X + 50, y + 12, 0xFFFF00, true);
            }
        }
        
        guiGraphics.drawString(this.font, "§7提示: 点击此处设置按键", INFO_AREA_X, 160, 0x888888, true);
    }

    private void renderChiselInfo(GuiGraphics guiGraphics, ItemStack displayStack) {
        int infoY = 17;
        
        guiGraphics.drawString(this.font, "§b✦ 锲石", INFO_AREA_X, infoY, 0xFFFFFF, true);
        infoY += LINE_HEIGHT;
        
        ChiselStoneItem chiselItem = (ChiselStoneItem) displayStack.getItem();
        int level = chiselItem.getChiselLevel();
        int purity = ChiselStoneItem.getPurity(displayStack);
        
        guiGraphics.drawString(this.font, "§e等级: §f" + level, INFO_AREA_X, infoY, 0xFFFFFF, true);
        infoY += LINE_HEIGHT;
        guiGraphics.drawString(this.font, "§e纯度: §f" + purity, INFO_AREA_X, infoY, 0xFFFFFF, true);
    }

    private void updatePageButtons(GuiGraphics guiGraphics, int totalPages) {
        if (currentPage == 0 && currentInfoLines.size() > MAX_INFO_LINES) {
            int maxInfoPages = (currentInfoLines.size() + MAX_INFO_LINES - 1) / MAX_INFO_LINES;
            prevPageButton.active = infoPage > 0;
            nextPageButton.active = infoPage < maxInfoPages - 1 || currentPage < totalPages - 1;
        } else {
            prevPageButton.active = currentPage > 0;
            nextPageButton.active = currentPage < totalPages - 1;
        }
        
        if (totalPages > 1 || currentInfoLines.size() > MAX_INFO_LINES) {
            int displayPage = currentPage + 1;
            int displayTotal = totalPages;
            if (currentPage == 0 && currentInfoLines.size() > MAX_INFO_LINES) {
                int maxInfoPages = (currentInfoLines.size() + MAX_INFO_LINES - 1) / MAX_INFO_LINES;
                displayPage = infoPage + 1;
                displayTotal = maxInfoPages;
            }
            String pageText = "§7" + displayPage + "/" + displayTotal;
            int textWidth = this.font.width(pageText.replaceAll("§[0-9a-fk-or]", ""));
            guiGraphics.drawString(this.font, pageText, 113 - textWidth / 2, 97, 0xFFFFFF, true);
        }
    }

    private void renderWeaponInfo(GuiGraphics guiGraphics, ItemStack stack) {
        currentInfoLines.clear();
        
        currentInfoLines.add("§d✦ 已刻入契纹");
        currentInfoLines.add("§f" + stack.getDisplayName().getString());

        String weaponType = getWeaponType(stack);
        if (!weaponType.isEmpty()) {
            currentInfoLines.add("§c类型: §f" + weaponType);
        }

        List<String> enchants = getEnchants(stack);
        if (!enchants.isEmpty()) {
            currentInfoLines.add("§5附魔:");
            for (String ench : enchants) {
                currentInfoLines.add("§7  " + ench);
            }
        }

        currentInfoLines.add("§e契纹: §f" + allRunes.size() + "个");

        if (RuneData.hasEmbeddedChisel(stack)) {
            int level = RuneData.getChiselLevel(stack);
            int unallocated = RuneData.getUnallocatedPurity(stack);
            currentInfoLines.add("§a锲石 Lv." + level);
            currentInfoLines.add("§e未分配: §f" + unallocated);
        } else {
            currentInfoLines.add("§c无锲石");
        }

        int startLine = infoPage * MAX_INFO_LINES;
        int endLine = Math.min(startLine + MAX_INFO_LINES, currentInfoLines.size());
        
        int infoY = 17;
        for (int i = startLine; i < endLine; i++) {
            String line = currentInfoLines.get(i);
            guiGraphics.drawString(this.font, line, INFO_AREA_X, infoY, 0xFFFFFF, true);
            infoY += LINE_HEIGHT;
        }
    }

    private String getWeaponType(ItemStack stack) {
        if (stack.getItem() instanceof SwordItem) return "剑";
        if (stack.getItem() instanceof TieredItem) return "工具";
        if (stack.getItem() instanceof BowItem) return "弓";
        if (stack.getItem() instanceof CrossbowItem) return "弩";
        if (stack.getItem() instanceof TridentItem) return "三叉戟";
        if (stack.getItem() instanceof ShieldItem) return "盾牌";
        return "";
    }

    private void renderRunePage(GuiGraphics guiGraphics, ItemStack stack, Rune rune) {
        currentInfoLines.clear();
        int infoY = 17;

        guiGraphics.drawString(this.font, "§d✦ " + rune.getName(), INFO_AREA_X, infoY, 0xFFFFFF, true);
        infoY += LINE_HEIGHT;

        int maxPotential = RuneData.getMaxPotential(stack);
        int allocated = RuneData.getAllocatedPotential(stack, rune.getType());
        int unallocated = RuneData.getUnallocatedPurity(stack);

        guiGraphics.drawString(this.font, "§e潜力: §f" + allocated + "/" + maxPotential, INFO_AREA_X, infoY, 0xFFFFFF, true);
        infoY += LINE_HEIGHT;

        renderPotentialIcons(guiGraphics, maxPotential, allocated, INFO_AREA_X, infoY);

        int plusX = INFO_AREA_X + Math.min(maxPotential, MAX_POTENTIAL_ICONS_PER_ROW) * 9 + 4;
        int plusY = infoY + ((maxPotential - 1) / MAX_POTENTIAL_ICONS_PER_ROW) * 10;
        if (unallocated > 0 && allocated < maxPotential) {
            guiGraphics.drawString(this.font, "§a+", plusX, plusY, 0xFFFFFF, true);
            runeButtonPositions.put(rune.getType(), new int[]{plusX, plusY});
        }
    }

    private void renderPotentialIcons(GuiGraphics guiGraphics, int maxPotential, int allocated, int x, int y) {
        for (int i = 0; i < maxPotential; i++) {
            int row = i / MAX_POTENTIAL_ICONS_PER_ROW;
            int col = i % MAX_POTENTIAL_ICONS_PER_ROW;
            
            int iconX = x + col * 9;
            int iconY = y + row * 10;

            ResourceLocation texture = i < allocated ? ACTIVATED_POTENTIAL : UNACTIVATED_POTENTIAL;
            guiGraphics.blit(texture, iconX, iconY, 0, 0, 8, 8, 8, 8);
        }
    }

    private List<String> getEnchants(ItemStack stack) {
        List<String> enchants = new ArrayList<>();
        if (stack.getEnchantmentTags() != null) {
            net.minecraft.nbt.ListTag enchantsList = stack.getEnchantmentTags();
            for (int i = 0; i < enchantsList.size(); i++) {
                net.minecraft.nbt.CompoundTag enchTag = enchantsList.getCompound(i);
                int enchId = enchTag.getShort("id");
                int enchLevel = enchTag.getShort("lvl");
                net.minecraft.world.item.enchantment.Enchantment ench = net.minecraft.world.item.enchantment.Enchantment.byId(enchId);
                if (ench != null) {
                    enchants.add(ench.getFullname(enchLevel).getString());
                }
            }
        }
        return enchants;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);

        craftButton.active = menu.canCraft();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (menu.canAllocate() && currentPage > 0) {
            double relX = mouseX - leftPos;
            double relY = mouseY - topPos;

            for (Map.Entry<String, int[]> entry : runeButtonPositions.entrySet()) {
                String runeType = entry.getKey();
                int[] pos = entry.getValue();

                if (relX >= pos[0] && relX <= pos[0] + 12 && relY >= pos[1] && relY <= pos[1] + 10) {
                    NetworkHandler.INSTANCE.sendToServer(new AllocatePotentialPacket(menu.getBlockEntity().getBlockPos(), runeType));
                    return true;
                }
            }
        }

        ItemStack weaponStack = menu.getWeaponStack();
        if (!weaponStack.isEmpty()) {
            int totalPages = allRunes.size() + 2;
            if (currentPage == totalPages - 1) {
                double relY = mouseY - topPos;
                
                for (int i = 0; i < 4; i++) {
                    int y = 35 + i * 30;
                    if (relY >= y && relY <= y + 25) {
                        waitingForKeySlot = i;
                        return true;
                    }
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (waitingForKeySlot >= 0) {
            linkKeyBindings[waitingForKeySlot] = keyCode;
            
            int slot = ChiselStoneForgingTableBlockEntity.LINK_SLOT_1 + waitingForKeySlot;
            NetworkHandler.INSTANCE.sendToServer(new SetLinkKeyPacket(menu.getBlockEntity().getBlockPos(), slot, keyCode));
            
            waitingForKeySlot = -1;
            return true;
        }
        
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return super.keyReleased(keyCode, scanCode, modifiers);
    }
}
