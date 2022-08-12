package com.github.reviversmc.modget.minecraft.compat.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;

public class VersionAgnosticServerCommandManager116 extends VersionAgnosticServerCommandManager {

    public VersionAgnosticServerCommandManager116() {
    }

    @Override
    public void register(LiteralArgumentBuilder<ServerCommandSource> argumentBuilder) {
        CommandRegistrationCallback.EVENT.register((dispatcher, isDedicated) ->
                dispatcher.register(argumentBuilder));
    }

}
