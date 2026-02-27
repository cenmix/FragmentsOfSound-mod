package org.fuzhou.fragmentsofsound.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.fuzhou.fragmentsofsound.Config;
import org.fuzhou.fragmentsofsound.cinematic.CinematicManager;
import org.fuzhou.fragmentsofsound.event.CinematicEventHandler;

public class CinematicCommand {
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("fos")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("cinematic")
                .then(Commands.literal("play")
                    .executes(context -> playCinematicAtPlayer(context.getSource(), 1, "zombie", false))
                    .then(Commands.argument("level", IntegerArgumentType.integer(1, 7))
                        .executes(context -> playCinematicAtPlayer(
                            context.getSource(),
                            IntegerArgumentType.getInteger(context, "level"),
                            "zombie",
                            false
                        ))
                        .then(Commands.argument("monster", StringArgumentType.word())
                            .executes(context -> playCinematicAtPlayer(
                                context.getSource(),
                                IntegerArgumentType.getInteger(context, "level"),
                                StringArgumentType.getString(context, "monster"),
                                false
                            ))
                            .then(Commands.argument("applyChanges", BoolArgumentType.bool())
                                .executes(context -> playCinematicAtPlayer(
                                    context.getSource(),
                                    IntegerArgumentType.getInteger(context, "level"),
                                    StringArgumentType.getString(context, "monster"),
                                    BoolArgumentType.getBool(context, "applyChanges")
                                ))
                            )
                        )
                    )
                )
                .then(Commands.literal("playAt")
                    .then(Commands.argument("x", IntegerArgumentType.integer())
                        .then(Commands.argument("y", IntegerArgumentType.integer())
                            .then(Commands.argument("z", IntegerArgumentType.integer())
                                .executes(context -> playCinematicAtPosition(
                                    context.getSource(),
                                    new BlockPos(
                                        IntegerArgumentType.getInteger(context, "x"),
                                        IntegerArgumentType.getInteger(context, "y"),
                                        IntegerArgumentType.getInteger(context, "z")
                                    ),
                                    1,
                                    "zombie",
                                    false
                                ))
                                .then(Commands.argument("level", IntegerArgumentType.integer(1, 7))
                                    .executes(context -> playCinematicAtPosition(
                                        context.getSource(),
                                        new BlockPos(
                                            IntegerArgumentType.getInteger(context, "x"),
                                            IntegerArgumentType.getInteger(context, "y"),
                                            IntegerArgumentType.getInteger(context, "z")
                                        ),
                                        IntegerArgumentType.getInteger(context, "level"),
                                        "zombie",
                                        false
                                    ))
                                    .then(Commands.argument("monster", StringArgumentType.word())
                                        .executes(context -> playCinematicAtPosition(
                                            context.getSource(),
                                            new BlockPos(
                                                IntegerArgumentType.getInteger(context, "x"),
                                                IntegerArgumentType.getInteger(context, "y"),
                                                IntegerArgumentType.getInteger(context, "z")
                                            ),
                                            IntegerArgumentType.getInteger(context, "level"),
                                            StringArgumentType.getString(context, "monster"),
                                            false
                                        ))
                                        .then(Commands.argument("applyChanges", BoolArgumentType.bool())
                                            .executes(context -> playCinematicAtPosition(
                                                context.getSource(),
                                                new BlockPos(
                                                    IntegerArgumentType.getInteger(context, "x"),
                                                    IntegerArgumentType.getInteger(context, "y"),
                                                    IntegerArgumentType.getInteger(context, "z")
                                                ),
                                                IntegerArgumentType.getInteger(context, "level"),
                                                StringArgumentType.getString(context, "monster"),
                                                BoolArgumentType.getBool(context, "applyChanges")
                                            ))
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
                .then(Commands.literal("reset")
                    .executes(context -> resetCinematic(context.getSource()))
                )
            )
            .then(Commands.literal("debug")
                .then(Commands.literal("triggerDay2")
                    .executes(context -> triggerDay2Cinematic(context.getSource()))
                )
                .then(Commands.literal("enableOutpostSpawn")
                    .executes(context -> enableOutpostSpawn(context.getSource()))
                )
                .then(Commands.literal("setDay")
                    .then(Commands.argument("day", IntegerArgumentType.integer(1, 100))
                        .executes(context -> setDay(context.getSource(), IntegerArgumentType.getInteger(context, "day")))
                    )
                )
                .then(Commands.literal("clearProcessedChunks")
                    .executes(context -> clearProcessedChunks(context.getSource()))
                )
                .then(Commands.literal("spawnOutpostHere")
                    .executes(context -> spawnOutpostHere(context.getSource()))
                )
                .then(Commands.literal("spawnOutpostAt")
                    .then(Commands.argument("x", IntegerArgumentType.integer())
                        .then(Commands.argument("y", IntegerArgumentType.integer())
                            .then(Commands.argument("z", IntegerArgumentType.integer())
                                .executes(context -> spawnOutpostAt(context.getSource(),
                                    IntegerArgumentType.getInteger(context, "x"),
                                    IntegerArgumentType.getInteger(context, "y"),
                                    IntegerArgumentType.getInteger(context, "z")))
                            )
                        )
                    )
                )
                .then(Commands.literal("checkOutpostStatus")
                    .executes(context -> checkOutpostStatus(context.getSource()))
                )
            )
        );
    }
    
    private static int playCinematicAtPlayer(CommandSourceStack source, int level, String monster, boolean applyChanges) {
        if (source.getEntity() instanceof ServerPlayer player) {
            BlockPos pos = player.blockPosition().offset(
                player.getRandom().nextInt(20) - 10,
                0,
                player.getRandom().nextInt(20) - 10
            );
            
            int y = player.level().getHeight(
                net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                pos.getX(),
                pos.getZ()
            );
            pos = new BlockPos(pos.getX(), y - 1, pos.getZ());
            
            if (player.level().getBlockState(pos).canBeReplaced()) {
                pos = pos.below();
            }
            
            CinematicEventHandler.triggerCinematicForPlayer(player, pos, level, monster, applyChanges);
            
            source.sendSuccess(() -> net.minecraft.network.chat.Component.literal(
                String.format("§e[破碎余声] §a开始播放CG - 据点等级: %d, 应用更改: %s", level, applyChanges)
            ), true);
            return 1;
        }
        source.sendFailure(net.minecraft.network.chat.Component.literal("§c此指令只能由玩家执行"));
        return 0;
    }
    
    private static int playCinematicAtPosition(CommandSourceStack source, BlockPos pos, int level, String monster, boolean applyChanges) {
        if (source.getEntity() instanceof ServerPlayer player) {
            CinematicEventHandler.triggerCinematicForPlayer(player, pos, level, monster, applyChanges);
            
            source.sendSuccess(() -> net.minecraft.network.chat.Component.literal(
                String.format("§e[破碎余声] §a开始播放CG - 位置: %s, 据点等级: %d, 应用更改: %s", pos, level, applyChanges)
            ), true);
            return 1;
        }
        source.sendFailure(net.minecraft.network.chat.Component.literal("§c此指令只能由玩家执行"));
        return 0;
    }
    
    private static int resetCinematic(CommandSourceStack source) {
        if (source.getEntity() instanceof ServerPlayer player) {
            org.fuzhou.fragmentsofsound.cinematic.CinematicManager manager = 
                org.fuzhou.fragmentsofsound.cinematic.CinematicManager.get(player.serverLevel());
            manager.setDay2CinematicPlayed(false);
            
            source.sendSuccess(() -> net.minecraft.network.chat.Component.literal(
                "§e[破碎余声] §a已重置第二天CG触发状态"
            ), true);
            return 1;
        }
        source.sendFailure(net.minecraft.network.chat.Component.literal("§c此指令只能由玩家执行"));
        return 0;
    }
    
    private static int triggerDay2Cinematic(CommandSourceStack source) {
        if (source.getEntity() instanceof ServerPlayer player) {
            ServerLevel level = player.serverLevel();
            CinematicManager manager = CinematicManager.get(level);
            
            if (manager.hasDay2CinematicPlayed()) {
                source.sendFailure(net.minecraft.network.chat.Component.literal(
                    "§c[破碎余声] §c第二天CG已经播放过了，请先使用 /fos cinematic reset 重置"
                ));
                return 0;
            }
            
            CinematicEventHandler.triggerDay2Cinematic(player, level, manager);
            
            source.sendSuccess(() -> net.minecraft.network.chat.Component.literal(
                "§e[破碎余声] §a已强制触发第二天CG和据点生成"
            ), true);
            return 1;
        }
        source.sendFailure(net.minecraft.network.chat.Component.literal("§c此指令只能由玩家执行"));
        return 0;
    }
    
    private static int enableOutpostSpawn(CommandSourceStack source) {
        if (source.getEntity() instanceof ServerPlayer player) {
            ServerLevel level = player.serverLevel();
            
            long currentDayTime = level.getDayTime();
            long day2Ticks = 2 * 24000L;
            
            if (currentDayTime < day2Ticks) {
                level.setDayTime(day2Ticks);
            }
            
            source.sendSuccess(() -> net.minecraft.network.chat.Component.literal(
                "§e[破碎余声] §a已将时间设置为第二天，据点生成已启用"
            ), true);
            return 1;
        }
        source.sendFailure(net.minecraft.network.chat.Component.literal("§c此指令只能由玩家执行"));
        return 0;
    }
    
    private static int setDay(CommandSourceStack source, int day) {
        if (source.getEntity() instanceof ServerPlayer player) {
            ServerLevel level = player.serverLevel();
            
            long targetDayTime = day * 24000L;
            level.setDayTime(targetDayTime);
            
            source.sendSuccess(() -> net.minecraft.network.chat.Component.literal(
                String.format("§e[破碎余声] §a已将世界时间设置为第 %d 天", day)
            ), true);
            return 1;
        }
        source.sendFailure(net.minecraft.network.chat.Component.literal("§c此指令只能由玩家执行"));
        return 0;
    }
    
    private static int clearProcessedChunks(CommandSourceStack source) {
        if (source.getEntity() instanceof ServerPlayer player) {
            org.fuzhou.fragmentsofsound.world.OutpostWorldGen.clearProcessedChunks();
            
            source.sendSuccess(() -> net.minecraft.network.chat.Component.literal(
                "§e[破碎余声] §a已清除已处理的区块记录，据点可以在已加载的区块重新生成"
            ), true);
            return 1;
        }
        source.sendFailure(net.minecraft.network.chat.Component.literal("§c此指令只能由玩家执行"));
        return 0;
    }
    
    private static int spawnOutpostHere(CommandSourceStack source) {
        if (source.getEntity() instanceof ServerPlayer player) {
            ServerLevel level = player.serverLevel();
            BlockPos pos = player.blockPosition();
            
            int outpostLevel;
            if (Config.OUTPOST_LEVEL_BY_DISTANCE.get()) {
                BlockPos spawnPos = level.getSharedSpawnPos();
                double distance = Math.sqrt(Math.pow(pos.getX() - spawnPos.getX(), 2) + Math.pow(pos.getZ() - spawnPos.getZ(), 2));
                outpostLevel = calculateOutpostLevel(distance);
            } else {
                outpostLevel = 1;
            }
            
            net.minecraft.world.level.block.state.BlockState outpostState = getOutpostBlockState(outpostLevel);
            if (outpostState != null) {
                int y = level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ());
                BlockPos placePos = new BlockPos(pos.getX(), y, pos.getZ());
                level.setBlock(placePos, outpostState, 2);
                
                source.sendSuccess(() -> net.minecraft.network.chat.Component.literal(
                    String.format("§e[破碎余声] §a已在当前位置生成等级 %d 的据点", outpostLevel)
                ), true);
                return 1;
            }
        }
        source.sendFailure(net.minecraft.network.chat.Component.literal("§c此指令只能由玩家执行"));
        return 0;
    }
    
    private static int checkOutpostStatus(CommandSourceStack source) {
        if (source.getEntity() instanceof ServerPlayer player) {
            ServerLevel level = player.serverLevel();
            long dayTime = level.getDayTime();
            int currentDay = (int) (dayTime / 24000);
            int startDay = Config.OUTPOST_SPAWN_START_DAY.get();
            
            source.sendSuccess(() -> net.minecraft.network.chat.Component.literal(
                String.format("§e[破碎余声] §a据点生成状态:\n§7当前天数: §b%d\n§7开始生成天数: §b%d\n§7自然生成启用: §b%s\n§7生成概率: §b%.0f%%",
                    currentDay, startDay, Config.OUTPOST_NATURAL_SPAWN_ENABLED.get(), Config.OUTPOST_CHUNK_SPAWN_CHANCE.get() * 100)
            ), true);
            return 1;
        }
        source.sendFailure(net.minecraft.network.chat.Component.literal("§c此指令只能由玩家执行"));
        return 0;
    }
    
    private static int spawnOutpostAt(CommandSourceStack source, int x, int y, int z) {
        if (source.getEntity() instanceof ServerPlayer player) {
            ServerLevel level = player.serverLevel();
            BlockPos pos = new BlockPos(x, y, z);
            
            int outpostLevel;
            if (Config.OUTPOST_LEVEL_BY_DISTANCE.get()) {
                BlockPos spawnPos = level.getSharedSpawnPos();
                double distance = Math.sqrt(Math.pow(pos.getX() - spawnPos.getX(), 2) + Math.pow(pos.getZ() - spawnPos.getZ(), 2));
                outpostLevel = calculateOutpostLevel(distance);
            } else {
                outpostLevel = 1;
            }
            
            net.minecraft.world.level.block.state.BlockState outpostState = getOutpostBlockState(outpostLevel);
            if (outpostState != null) {
                level.setBlock(pos, outpostState, 2);
                
                source.sendSuccess(() -> net.minecraft.network.chat.Component.literal(
                    String.format("§e[破碎余声] §a已在位置 (%d, %d, %d) 生成等级 %d 的据点", x, y, z, outpostLevel)
                ), true);
                return 1;
            }
        }
        source.sendFailure(net.minecraft.network.chat.Component.literal("§c此指令只能由玩家执行"));
        return 0;
    }
    
    private static int calculateOutpostLevel(double distance) {
        if (distance < 1000) {
            return 1;
        } else if (distance < 3000) {
            return 2;
        } else if (distance < 6000) {
            return 3;
        } else if (distance < 10000) {
            return 4;
        } else if (distance < 15000) {
            return 5;
        } else if (distance < 20000) {
            return 6;
        } else {
            return 7;
        }
    }
    
    private static net.minecraft.world.level.block.state.BlockState getOutpostBlockState(int level) {
        return switch (level) {
            case 1 -> org.fuzhou.fragmentsofsound.Fragmentsofsound.MONSTER_OUTPOST_1.get().defaultBlockState();
            case 2 -> org.fuzhou.fragmentsofsound.Fragmentsofsound.MONSTER_OUTPOST_2.get().defaultBlockState();
            case 3 -> org.fuzhou.fragmentsofsound.Fragmentsofsound.MONSTER_OUTPOST_3.get().defaultBlockState();
            case 4 -> org.fuzhou.fragmentsofsound.Fragmentsofsound.MONSTER_OUTPOST_4.get().defaultBlockState();
            case 5 -> org.fuzhou.fragmentsofsound.Fragmentsofsound.MONSTER_OUTPOST_5.get().defaultBlockState();
            case 6 -> org.fuzhou.fragmentsofsound.Fragmentsofsound.MONSTER_OUTPOST_6.get().defaultBlockState();
            case 7 -> org.fuzhou.fragmentsofsound.Fragmentsofsound.MONSTER_OUTPOST_7.get().defaultBlockState();
            default -> org.fuzhou.fragmentsofsound.Fragmentsofsound.MONSTER_OUTPOST_1.get().defaultBlockState();
        };
    }
}
