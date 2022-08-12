package com.github.reviversmc.modget.minecraft.compat.command;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.minecraft.entity.player.PlayerEntity;

public class VersionAgnosticClientCommandManager {
    private static VersionAgnosticClientCommandManager versionAgnosticCommandManager;

    public static void set(VersionAgnosticClientCommandManager newVersionAgnosticCommandManager) {
        versionAgnosticCommandManager = newVersionAgnosticCommandManager;
    }

    public static VersionAgnosticClientCommandManager get() {
        return versionAgnosticCommandManager;
    }

    public void registerLiteralCommand(List<String> commandParts, Consumer<PlayerEntity> consumer) {
        throw new UnsupportedOperationException();
    }

    public void registerArgumentCommand(List<String> commandParts, ArgumentTypeType argType, BiConsumer<PlayerEntity, String> consumer) {
        throw new UnsupportedOperationException();
    }

}
