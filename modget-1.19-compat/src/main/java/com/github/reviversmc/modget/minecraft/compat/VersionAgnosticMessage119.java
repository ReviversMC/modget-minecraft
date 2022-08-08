package com.github.reviversmc.modget.minecraft.compat;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class VersionAgnosticMessage119 extends VersionAgnosticMessage {

    public VersionAgnosticMessage119() {
    }

    @Override
    public void sendInfo(PlayerEntity player, Text message) {
        player.sendMessage(message.copy().setStyle(message.getStyle()).formatted(infoFormatting), false);
    }

    @Override
    public void sendWarning(PlayerEntity player, Text message) {
        player.sendMessage(message.copy().setStyle(message.getStyle()).formatted(warningFormatting), false);
    }

    @Override
    public void sendError(PlayerEntity player, Text message) {
        player.sendMessage(message.copy().setStyle(message.getStyle()).formatted(errorFormatting), false);
    }

    @Override
    public void sendText(PlayerEntity player, Text message) {
        player.sendMessage(message.copy().setStyle(message.getStyle()).formatted(textFormatting), false);
    }

    @Override
    public void sendHeadline(PlayerEntity player, Text message) {
        player.sendMessage(message.copy().setStyle(message.getStyle()).formatted(headlineFormatting), false);
    }

}
