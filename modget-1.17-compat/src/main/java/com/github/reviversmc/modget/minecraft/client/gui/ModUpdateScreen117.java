package com.github.reviversmc.modget.minecraft.client.gui;

import com.github.reviversmc.modget.minecraft.client.gui.widgets.ModUpdateListWidgetBase;
import com.github.reviversmc.modget.minecraft.client.gui.widgets.ModUpdateListWidget117;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ModUpdateScreen117 extends ModUpdateScreen116 {

    public ModUpdateScreen117(Screen parent) {
        super(parent);
    }


    @Override
    ModUpdateListWidgetBase<?> setUpdateListWidget() {
        return new ModUpdateListWidget117<ModUpdateScreen117>(client, this);
    }

    @Override
    void addUpdateListWidget() {
        addDrawableChild(updateListWidget);
    }

    @Override
    ButtonWidget addRefreshButton(Text text) {
        return addDrawableChild(new ButtonWidget(refreshX, actionRowY, buttonWidth, buttonHeight,
                text, buttonWidget -> refreshButtonAction()));
    }

    @Override
    ButtonWidget addDownloadButton(Text text) {
        return addDrawableChild(new ButtonWidget(downloadX, actionRowY, buttonWidth, buttonHeight,
                text, buttonWidget -> downloadButtonAction()));
    }

    @Override
    ButtonWidget addDoneButton(Text text) {
        return addDrawableChild(new ButtonWidget(doneX, doneY, buttonWidth, buttonHeight, text, buttonWidget -> {
            assert client != null;
            client.setScreen(parent);
        }));
    }

}
