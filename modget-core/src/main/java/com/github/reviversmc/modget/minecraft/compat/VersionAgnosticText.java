package com.github.reviversmc.modget.minecraft.compat;

import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class VersionAgnosticText {
    private static VersionAgnosticText versionAgnosticText;

    public static void set(VersionAgnosticText newVersionAgnosticText) {
        versionAgnosticText = newVersionAgnosticText;
    }

    public static VersionAgnosticText get() {
        return versionAgnosticText;
    }


    public Text literal(String string) {
        throw new UnsupportedOperationException();
    }

    public Text literal(String string, Formatting formatting) {
        throw new UnsupportedOperationException();
    }

    public Text translatable(String key, Object... args) {
        throw new UnsupportedOperationException();
    }

    public Text translatable(String key, Formatting formatting, Object... args) {
        throw new UnsupportedOperationException();
    }

    public Text translatable(String key, ClickEvent clickEvent, HoverEvent hoverEvent, Object... args) {
        throw new UnsupportedOperationException();
    }

    public Text DONE() {
        throw new UnsupportedOperationException();
    }

}
