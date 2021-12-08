package com.github.reviversmc.modget.minecraft.command;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.entity.player.PlayerEntity;

public class ClientPlayerHack {

    public static PlayerEntity getPlayer(CommandContext<FabricClientCommandSource> ctx) {
        PlayerEntity player = ctx.getSource().getPlayer();
        return player;
    }
}