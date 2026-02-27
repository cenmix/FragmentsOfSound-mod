package org.fuzhou.fragmentsofsound.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
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
}
