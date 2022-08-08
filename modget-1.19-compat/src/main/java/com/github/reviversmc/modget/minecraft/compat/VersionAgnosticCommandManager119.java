package com.github.reviversmc.modget.minecraft.compat;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.server.command.ServerCommandSource;

public class VersionAgnosticCommandManager119 extends VersionAgnosticCommandManager {

    public VersionAgnosticCommandManager119() {
    }

    @Override
    public void registerServerCommand(LiteralArgumentBuilder<ServerCommandSource> argumentBuilder) {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(argumentBuilder));
    }

    @Override
    public void registerClientLiteralCommand(List<String> commandParts, Consumer<ClientPlayerEntity> consumer) {
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

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register((LiteralArgumentBuilder<FabricClientCommandSource>) builders[0]);
        });
    }

    @Override
    public void registerClientArgumentCommand(List<String> commandParts, ArgumentTypeEnum argType, BiConsumer<ClientPlayerEntity, String> consumer) {
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

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register((LiteralArgumentBuilder<FabricClientCommandSource>) builders[0]);
        });
    }

}
