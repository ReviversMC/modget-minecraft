package com.github.reviversmc.modget.minecraft.compat.command;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.entity.player.PlayerEntity;

public class VersionAgnosticClientCommandManager116 extends VersionAgnosticClientCommandManager {

    public VersionAgnosticClientCommandManager116() {
    }


    @Override
    public void registerLiteralCommand(List<String> commandParts, Consumer<PlayerEntity> consumer) {
        ArgumentBuilder<FabricClientCommandSource, ?>[] builders = new ArgumentBuilder[commandParts.size()];
        ArgumentBuilder<FabricClientCommandSource, ?> currentBuilder;

        // Build the command chain from the inside out
        for (int i = commandParts.size() - 1; i >= 0; i--) {
            currentBuilder = ClientCommandManager.literal(commandParts.get(i));

            if (i == commandParts.size() - 1) {
                currentBuilder.executes(context -> {
                    consumer.accept(context.getSource().getPlayer());
                    return 1;
                });
            } else {
                currentBuilder.then(builders[i + 1]);
            }

            builders[i] = currentBuilder;
        }

        ClientCommandManager.DISPATCHER.register((LiteralArgumentBuilder<FabricClientCommandSource>) builders[0]);
    }

    @Override
    public void registerArgumentCommand(List<String> commandParts, ArgumentTypeType argType, BiConsumer<PlayerEntity, String> consumer) {
        ArgumentBuilder<FabricClientCommandSource, ?>[] builders = new ArgumentBuilder[commandParts.size()];
        ArgumentBuilder<FabricClientCommandSource, ?> currentBuilder;

        // Build the command chain from the inside out
        for (int i = commandParts.size() - 1; i >= 0; i--) {
            ArgumentType<?> argumentType;
            switch (argType) {
                case GREEDY_STRING:
                    argumentType = StringArgumentType.greedyString();
                    break;
                case STRING:
                    argumentType = StringArgumentType.string();
                    break;
                case INTEGER:
                    argumentType = IntegerArgumentType.integer();
                    break;
                default:
                    throw new IllegalStateException();
            }

            if (i == commandParts.size() - 1) {
                currentBuilder = ClientCommandManager.argument(commandParts.get(i), argumentType);
                currentBuilder.executes(context -> {
                    String string;
                    switch (argType) {
                        case GREEDY_STRING:
                        case STRING:
                            string = StringArgumentType.getString(context, commandParts.get(commandParts.size() - 1));
                            break;
                        case INTEGER:
                            string = String.valueOf(IntegerArgumentType.getInteger(context, commandParts.get(commandParts.size() - 1)));
                            break;
                        default:
                            throw new IllegalStateException();
                    }
                    consumer.accept(context.getSource().getPlayer(), string);
                    return 1;
                });
            } else {
                currentBuilder = ClientCommandManager.literal(commandParts.get(i));
                currentBuilder.then(builders[i + 1]);
            }

            builders[i] = currentBuilder;
        }

        ClientCommandManager.DISPATCHER.register((LiteralArgumentBuilder<FabricClientCommandSource>) builders[0]);
    }

}
