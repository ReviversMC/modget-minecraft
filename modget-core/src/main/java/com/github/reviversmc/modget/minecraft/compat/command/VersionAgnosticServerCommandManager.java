package com.github.reviversmc.modget.minecraft.compat.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.server.command.ServerCommandSource;

public class VersionAgnosticServerCommandManager {
    private static VersionAgnosticServerCommandManager versionAgnosticCommandManager;

    public static void set(VersionAgnosticServerCommandManager newVersionAgnosticCommandManager) {
        versionAgnosticCommandManager = newVersionAgnosticCommandManager;
    }

    public static VersionAgnosticServerCommandManager get() {
        return versionAgnosticCommandManager;
    }


    public void register(LiteralArgumentBuilder<ServerCommandSource> argumentBuilder) {
        throw new UnsupportedOperationException();
    }

}
