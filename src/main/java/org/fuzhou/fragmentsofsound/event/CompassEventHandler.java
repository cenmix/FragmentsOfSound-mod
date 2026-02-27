package org.fuzhou.fragmentsofsound.event;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.fuzhou.fragmentsofsound.Fragmentsofsound;
import org.fuzhou.fragmentsofsound.item.ChiselStoneItem;

import java.util.List;

@Mod.EventBusSubscriber(modid = Fragmentsofsound.MODID)
public class CompassEventHandler {

    private static final String CHISEL_STONE_TAG = "fragmentsofsound.chisel_stone";
    private static final String LAST_USE_TAG = "LastUseTime";
    private static final int TICKS_PER_MINUTE = 20 * 60;
    private static final int SEARCH_RADIUS = 3 * 16;
    private static final int CLUSTER_RADIUS = 3;

    @SubscribeEvent
    public static void onItemRightClick(PlayerInteractEvent.RightClickItem event) {
        ItemStack stack = event.getItemStack();
        
        if (stack.getItem() != Items.COMPASS) return;
        if (event.getLevel().isClientSide) return;
        
        ServerPlayer player = (ServerPlayer) event.getEntity();
        ItemStack offhand = player.getOffhandItem();
        
        if (offhand.getItem() instanceof ChiselStoneItem chiselStone) {
            int level = chiselStone.getChiselLevel();
            
            if (level >= 1 && level <= 8) {
                CompoundTag tag = stack.getOrCreateTag();
                CompoundTag chiselTag = tag.getCompound(CHISEL_STONE_TAG);
                int boundLevel = chiselTag.getInt("Level");
                
                if (boundLevel == 0) {
                    chiselTag.putInt("Level", level);
                    tag.put(CHISEL_STONE_TAG, chiselTag);
                    
                    offhand.shrink(1);
                    
                    player.sendSystemMessage(Component.translatable("item.fragmentsofsound.compass.bound", level));
                    event.setCanceled(true);
                    return;
                }
                
                if (boundLevel == level) {
                    if (!canDetect(player, stack)) {
                        event.setCanceled(true);
                        return;
                    }
                    
                    BlockPos nearestOre = findNearestOre(player.level(), player.blockPosition(), level);
                    
                    if (nearestOre == null) {
                        player.sendSystemMessage(Component.translatable("item.fragmentsofsound.compass.not_found", level + 1));
                        event.setCanceled(true);
                        return;
                    }
                    
                    int oreCount = countOresNearPosition(player.level(), nearestOre, level);
                    int costCount = oreCount * 2;
                    
                    if (offhand.getCount() < costCount) {
                        player.sendSystemMessage(Component.translatable("item.fragmentsofsound.compass.insufficient_stones"));
                        event.setCanceled(true);
                        return;
                    }
                    
                    offhand.shrink(costCount);
                    
                    chiselTag.putLong(LAST_USE_TAG, player.level().getGameTime());
                    tag.put(CHISEL_STONE_TAG, chiselTag);
                    
                    player.sendSystemMessage(Component.translatable(
                        "item.fragmentsofsound.compass.found",
                        level + 1,
                        nearestOre.getX(),
                        nearestOre.getY(),
                        nearestOre.getZ(),
                        costCount
                    ));
                    
                    event.setCanceled(true);
                    return;
                }
            }
        }
        
        if (stack.hasTag() && stack.getTag().contains(CHISEL_STONE_TAG)) {
            CompoundTag chiselTag = stack.getTag().getCompound(CHISEL_STONE_TAG);
            int level = chiselTag.getInt("Level");
            
            if (level >= 1 && level <= 8) {
                if (!canDetect(player, stack)) {
                    event.setCanceled(true);
                    return;
                }
                
                player.sendSystemMessage(Component.translatable("item.fragmentsofsound.compass.need_chisel", level));
                event.setCanceled(true);
            }
        }
    }

    private static boolean canDetect(ServerPlayer player, ItemStack stack) {
        CompoundTag chiselTag = stack.getTag().getCompound(CHISEL_STONE_TAG);
        int level = chiselTag.getInt("Level");
        long lastUseTime = chiselTag.getLong(LAST_USE_TAG);
        long cooldownTicks = (long) level * 2 * TICKS_PER_MINUTE;
        long currentTime = player.level().getGameTime();
        
        if (lastUseTime > 0) {
            long elapsedTicks = currentTime - lastUseTime;
            if (elapsedTicks < cooldownTicks) {
                long remainingTicks = cooldownTicks - elapsedTicks;
                int remainingMinutes = (int) (remainingTicks / TICKS_PER_MINUTE);
                int remainingSeconds = (int) ((remainingTicks % TICKS_PER_MINUTE) / 20);
                
                player.sendSystemMessage(Component.translatable(
                    "item.fragmentsofsound.compass.cooldown",
                    remainingMinutes,
                    remainingSeconds
                ));
                return false;
            }
        }
        return true;
    }

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        
        if (stack.getItem() != Items.COMPASS) return;
        if (!stack.hasTag() || !stack.getTag().contains(CHISEL_STONE_TAG)) return;
        
        CompoundTag chiselTag = stack.getTag().getCompound(CHISEL_STONE_TAG);
        int level = chiselTag.getInt("Level");
        
        if (level < 1 || level > 8) return;
        
        List<Component> tooltip = event.getToolTip();
        
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("══════════════════════").withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.translatable("item.fragmentsofsound.compass.tooltip.title").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
        tooltip.add(Component.literal("══════════════════════").withStyle(ChatFormatting.DARK_GRAY));
        
        MutableComponent levelComponent = Component.literal("◆ ")
            .withStyle(ChatFormatting.AQUA)
            .append(Component.translatable("item.fragmentsofsound.compass.tooltip.bound_level", level)
                .withStyle(ChatFormatting.YELLOW));
        tooltip.add(levelComponent);
        
        long lastUseTime = chiselTag.getLong(LAST_USE_TAG);
        if (lastUseTime > 0) {
            long cooldownTicks = (long) level * 2 * TICKS_PER_MINUTE;
            long currentTime = event.getEntity().level().getGameTime();
            long elapsedTicks = currentTime - lastUseTime;
            
            if (elapsedTicks < cooldownTicks) {
                long remainingTicks = cooldownTicks - elapsedTicks;
                int remainingMinutes = (int) (remainingTicks / TICKS_PER_MINUTE);
                int remainingSeconds = (int) ((remainingTicks % TICKS_PER_MINUTE) / 20);
                
                MutableComponent cooldownComponent = Component.literal("◆ ")
                    .withStyle(ChatFormatting.RED)
                    .append(Component.translatable("item.fragmentsofsound.compass.tooltip.cooldown", remainingMinutes, remainingSeconds)
                        .withStyle(ChatFormatting.RED));
                tooltip.add(cooldownComponent);
            } else {
                MutableComponent readyComponent = Component.literal("◆ ")
                    .withStyle(ChatFormatting.GREEN)
                    .append(Component.translatable("item.fragmentsofsound.compass.tooltip.ready")
                        .withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
                tooltip.add(readyComponent);
            }
        } else {
            MutableComponent readyComponent = Component.literal("◆ ")
                .withStyle(ChatFormatting.GREEN)
                .append(Component.translatable("item.fragmentsofsound.compass.tooltip.ready")
                    .withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
            tooltip.add(readyComponent);
        }
        
        tooltip.add(Component.empty());
        tooltip.add(Component.translatable("item.fragmentsofsound.compass.tooltip.usage").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.fragmentsofsound.compass.tooltip.usage_detail").withStyle(ChatFormatting.DARK_GRAY));
    }

    private static BlockPos findNearestOre(net.minecraft.world.level.Level level, BlockPos center, int currentLevel) {
        if (currentLevel < 1 || currentLevel > 8) return null;
        
        int targetLevel = currentLevel + 1;
        
        BlockPos nearest = null;
        double nearestDist = Double.MAX_VALUE;
        
        for (int dx = -SEARCH_RADIUS; dx <= SEARCH_RADIUS; dx += 16) {
            for (int dz = -SEARCH_RADIUS; dz <= SEARCH_RADIUS; dz += 16) {
                int chunkX = (center.getX() + dx) >> 4;
                int chunkZ = (center.getZ() + dz) >> 4;
                
                if (level.hasChunk(chunkX, chunkZ)) {
                    BlockPos found = searchInChunk(level, chunkX, chunkZ, targetLevel, center, nearestDist);
                    if (found != null) {
                        double dist = found.distSqr(center);
                        if (dist < nearestDist) {
                            nearestDist = dist;
                            nearest = found;
                        }
                    }
                }
            }
        }
        
        return nearest;
    }

    private static int countOresNearPosition(net.minecraft.world.level.Level level, BlockPos center, int currentLevel) {
        if (currentLevel < 1 || currentLevel > 8) return 0;
        
        int targetLevel = currentLevel + 1;
        int count = 0;
        
        for (int dx = -CLUSTER_RADIUS; dx <= CLUSTER_RADIUS; dx++) {
            for (int dy = -CLUSTER_RADIUS; dy <= CLUSTER_RADIUS; dy++) {
                for (int dz = -CLUSTER_RADIUS; dz <= CLUSTER_RADIUS; dz++) {
                    BlockPos pos = center.offset(dx, dy, dz);
                    if (isTargetOre(level.getBlockState(pos), targetLevel)) {
                        count++;
                    }
                }
            }
        }
        
        return count;
    }

    private static BlockPos searchInChunk(net.minecraft.world.level.Level level, int chunkX, int chunkZ, int targetLevel, BlockPos center, double maxDist) {
        int startX = chunkX * 16;
        int startZ = chunkZ * 16;
        BlockPos nearest = null;
        double nearestDist = maxDist;
        
        for (int x = startX; x < startX + 16; x++) {
            for (int z = startZ; z < startZ + 16; z++) {
                for (int y = level.getMinBuildHeight(); y < level.getMaxBuildHeight(); y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    double dist = pos.distSqr(center);
                    
                    if (dist < nearestDist && isTargetOre(level.getBlockState(pos), targetLevel)) {
                        nearestDist = dist;
                        nearest = pos;
                    }
                }
            }
        }
        
        return nearest;
    }

    private static boolean isTargetOre(net.minecraft.world.level.block.state.BlockState state, int targetLevel) {
        if (targetLevel == 1) return state.is(Fragmentsofsound.CHISEL_STONE_ORE_1.get());
        if (targetLevel == 2) return state.is(Fragmentsofsound.CHISEL_STONE_ORE_2.get());
        if (targetLevel == 3) return state.is(Fragmentsofsound.CHISEL_STONE_ORE_3.get());
        if (targetLevel == 4) return state.is(Fragmentsofsound.CHISEL_STONE_ORE_4.get());
        if (targetLevel == 5) return state.is(Fragmentsofsound.CHISEL_STONE_ORE_5.get());
        if (targetLevel == 6) return state.is(Fragmentsofsound.CHISEL_STONE_ORE_6.get());
        if (targetLevel == 7) return state.is(Fragmentsofsound.CHISEL_STONE_ORE_7.get());
        if (targetLevel == 8) return state.is(Fragmentsofsound.CHISEL_STONE_ORE_8.get());
        return false;
    }
}
