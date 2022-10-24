package com.github.reviversmc.modget.minecraft.compat;

import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class VersionAgnosticText119 extends VersionAgnosticText {

    public VersionAgnosticText119() {
    }

    @Override
    public MutableText literal(String string) {
        return Text.literal(string);
    }

    @Override
    public MutableText literal(String string, Formatting formatting) {
        return literal(string).formatted(formatting);
    }

	@Override
    public MutableText translatable(String key, Object... args) {
        return Text.translatable(key, args);
    }

	@Override
    public MutableText translatable(String key, Formatting formatting, Object... args) {
        return translatable(key, args).formatted(formatting);
    }

    @Override
    public MutableText translatable(String key, ClickEvent clickEvent, HoverEvent hoverEvent, Object... args) {
        return translatable(key, args).styled(style -> {
            return style.withClickEvent(clickEvent).withHoverEvent(hoverEvent);
        });
    }

    @Override
    public Text DONE() {
        return ScreenTexts.DONE;
    }

}
