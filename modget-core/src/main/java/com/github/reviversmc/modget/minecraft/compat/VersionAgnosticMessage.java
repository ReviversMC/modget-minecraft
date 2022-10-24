package com.github.reviversmc.modget.minecraft.compat;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class VersionAgnosticMessage {
    protected static final Formatting infoFormatting = Formatting.AQUA;
    protected static final Formatting warningFormatting = Formatting.GOLD;
    protected static final Formatting errorFormatting = Formatting.RED;
    protected static final Formatting textFormatting = Formatting.WHITE;
    protected static final Formatting headlineFormatting = Formatting.YELLOW;
    private static VersionAgnosticMessage versionAgnosticMessage;

    public static void set(VersionAgnosticMessage newVersionAgnosticText) {
        versionAgnosticMessage = newVersionAgnosticText;
    }

    public static VersionAgnosticMessage get() {
        return versionAgnosticMessage;
    }


    public void sendInfo(PlayerEntity player, Text message) {
        throw new UnsupportedOperationException();
    }

    public void sendWarning(PlayerEntity player, Text message) {
        throw new UnsupportedOperationException();
    }

    public void sendError(PlayerEntity player, Text message) {
        throw new UnsupportedOperationException();
    }

	public void sendText(PlayerEntity player, Text translatable) {
        throw new UnsupportedOperationException();
	}

	public void sendHeadline(PlayerEntity player, Text translatable) {
        throw new UnsupportedOperationException();
	}

}
