package com.github.reviversmc.modget.minecraft.client.gui;

import com.github.reviversmc.modget.minecraft.Modget;
import com.github.reviversmc.modget.minecraft.client.gui.widgets.ModUpdateListWidget;
import com.github.reviversmc.modget.minecraft.client.gui.widgets.ModUpdateListWidget117;
import com.github.reviversmc.modget.minecraft.manager.ModgetManager;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class ModUpdateScreen117 extends ModUpdateScreenBase {

    public ModUpdateScreen117(Screen parent) {
        super(parent);
    }


    @Override
    void drawTitle(MatrixStack matrices, TextRenderer textRenderer, Text title, int x, int y, int colorCode) {
        // This looks identical to the 1.16 method, it even has the same intermediary name.
        // Nevertheless, it only works in 1.16 if we put this exact draw call into the common module?
        drawCenteredText(matrices, textRenderer, title, x, y, colorCode);
    }

    @Override
    ModUpdateListWidget<?> setUpdateListWidget() {
        return new ModUpdateListWidget117<ModUpdateScreen117>(client, this);
    }

    @Override
    void addUpdateListWidget() {
        addDrawableChild(updateListWidget);
    }


    @Override
    ButtonWidget addRefreshButton() {
        return addDrawableChild(new ButtonWidget(refreshX, actionRowY, buttonWidth, buttonHeight, new TranslatableText("gui." + Modget.NAMESPACE + ".refresh"), buttonWidget -> ModgetManager.UPDATE_MANAGER.searchForUpdates()));
    }

    @Override
    ButtonWidget addDownloadButton() {
        return addDrawableChild(new ButtonWidget(downloadX, actionRowY, buttonWidth, buttonHeight, new TranslatableText("gui." + Modget.NAMESPACE + ".download"), buttonWidget -> {
            if (updateListWidget.getSelected() != null) {
                Util.getOperatingSystem().open(ModgetManager.UPDATE_MANAGER
                        .getPreferredDownloadPage(updateListWidget.getSelected().getModVersionVariantMod()).getUrl());
            }
        }));
    }

    @Override
    ButtonWidget addDoneButton() {
        return addDrawableChild(new ButtonWidget(doneX, doneY, buttonWidth, buttonHeight, ScreenTexts.DONE, buttonWidget -> {
            assert client != null;
            client.setScreen(parent);
        }));
    }
}
