package com.github.reviversmc.modget.minecraft.client.gui;

import com.github.reviversmc.modget.minecraft.client.gui.widgets.ModUpdateListWidget116;
import com.github.reviversmc.modget.minecraft.client.gui.widgets.ModUpdateListWidgetBase;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ModUpdateScreen116 extends ModUpdateScreenBase {

    public ModUpdateScreen116(Screen parent) {
        super(parent);
    }


    @Override
    ModUpdateListWidgetBase<?> setUpdateListWidget() {
        return new ModUpdateListWidget116<ModUpdateScreen116>(client, this);
    }

    @Override
    void addUpdateListWidget() {
        addChild(updateListWidget);
    }

    @Override
    ButtonWidget addRefreshButton(Text text) {
        return addButton(new ButtonWidget(refreshX, actionRowY, buttonWidth, buttonHeight,
                text, buttonWidget -> refreshButtonAction()));
    }

    @Override
    ButtonWidget addDownloadButton(Text text) {
        return addButton(new ButtonWidget(downloadX, actionRowY, buttonWidth, buttonHeight,
                text, buttonWidget -> downloadButtonAction()));
    }

    @Override
    ButtonWidget addDoneButton(Text text) {
        return addButton(new ButtonWidget(doneX, doneY, buttonWidth, buttonHeight, text, buttonWidget -> {
            assert client != null;
            client.openScreen(parent);
        }));
    }

}
