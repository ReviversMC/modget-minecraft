package com.github.reviversmc.modget.minecraft.compat.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;

public class VersionAgnosticServerCommandManager119 extends VersionAgnosticServerCommandManager {

    public VersionAgnosticServerCommandManager119() {
    }

    @Override
    public void register(LiteralArgumentBuilder<ServerCommandSource> argumentBuilder) {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(argumentBuilder));
    }

}
