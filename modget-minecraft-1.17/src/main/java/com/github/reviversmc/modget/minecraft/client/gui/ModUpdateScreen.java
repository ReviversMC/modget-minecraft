package com.github.reviversmc.modget.minecraft.client.gui;

import com.github.reviversmc.modget.minecraft.Modget;
import com.github.reviversmc.modget.minecraft.client.gui.widgets.ModUpdateListWidget;
import com.github.reviversmc.modget.minecraft.manager.ModgetManager;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class ModUpdateScreen extends ModUpdateScreenBase {

    public ModUpdateScreen(Screen parent) {
        super(parent);
    }

    @Override
    protected void init() {
        list = new ModUpdateListWidget<ModUpdateScreen>(client, this);
        addDrawableChild(list);
        super.init();
    }

    @Override
    protected void addButtons() {
        super.addButtons();
        refresh = addDrawableChild(new ButtonWidget(refreshX, actionRowY, buttonWidth, buttonHeight, new TranslatableText("gui." + Modget.NAMESPACE + ".refresh"), buttonWidget -> ModgetManager.UPDATE_MANAGER.searchForUpdates()));
        download = addDrawableChild(new ButtonWidget(downloadX, actionRowY, buttonWidth, buttonHeight, new TranslatableText("gui." + Modget.NAMESPACE + ".download"), buttonWidget -> {
            if (list.getSelected() != null) {
                Util.getOperatingSystem().open(list.getSelected().getUpdate().getDownloadPageUrls().getModrinth());
            }
        }));
        addDrawableChild(new ButtonWidget(doneX, doneY, buttonWidth, buttonHeight, ScreenTexts.DONE, buttonWidget -> {
            assert client != null;
            client.setScreen(parent);
        }));
    }
}
