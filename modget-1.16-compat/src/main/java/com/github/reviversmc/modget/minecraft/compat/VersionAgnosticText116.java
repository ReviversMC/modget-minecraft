package com.github.reviversmc.modget.minecraft.compat;

import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class VersionAgnosticText116 extends VersionAgnosticText {

    public VersionAgnosticText116() {
    }

    @Override
    public MutableText literal(String string) {
        return new LiteralText(string);
    }

    @Override
    public MutableText literal(String string, Formatting formatting) {
        return literal(string).formatted(formatting);
    }

	@Override
    public MutableText translatable(String key, Object... args) {
        return new TranslatableText(key, args);
    }

	@Override
    public MutableText translatable(String key, Formatting formatting, Object... args) {
        return translatable(key, args).formatted(formatting);
    }

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
