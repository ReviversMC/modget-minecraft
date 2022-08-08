package com.github.reviversmc.modget.minecraft.compat;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.server.command.ServerCommandSource;

public class VersionAgnosticCommandManager {
    private static VersionAgnosticCommandManager versionAgnosticCommandManager;
    public static enum ArgumentTypeEnum {
        GREEDY_STRING,
        STRING,
        INTEGER
    }

    public static void set(VersionAgnosticCommandManager newVersionAgnosticCommandManager) {
        versionAgnosticCommandManager = newVersionAgnosticCommandManager;
    }

    public static VersionAgnosticCommandManager get() {
        return versionAgnosticCommandManager;
    }


    public void registerServerCommand(LiteralArgumentBuilder<ServerCommandSource> argumentBuilder) {
        throw new UnsupportedOperationException();
    }

    public void registerClientLiteralCommand(List<String> commandParts, Consumer<ClientPlayerEntity> consumer) {
        throw new UnsupportedOperationException();
    }

    public void registerClientArgumentCommand(List<String> commandParts, ArgumentTypeEnum argType, BiConsumer<ClientPlayerEntity, String> consumer) {
        throw new UnsupportedOperationException();
    }

}
