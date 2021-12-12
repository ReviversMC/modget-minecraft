package com.github.reviversmc.modget.minecraft.client.gui;

import com.github.reviversmc.modget.minecraft.Modget;
import com.github.reviversmc.modget.minecraft.client.gui.widgets.ModUpdateListWidget;
import com.github.reviversmc.modget.minecraft.manager.ModgetManager;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public abstract class ModUpdateScreenBase extends Screen {
    public ModUpdateListWidget<?> updateListWidget;
    protected ButtonWidget refreshButton;
    protected ButtonWidget downloadButton;
    protected final Screen parent;
    protected int bottomRowHeight = 60;
    protected int buttonHeight = 20;
    protected int buttonWidth = 150;
    protected int padding = 2;
    protected int actionRowY;
    protected int doneY;
    protected int refreshX;
    protected int downloadX;
    protected int doneX;


    public ModUpdateScreenBase(Screen parent) {
        super(new TranslatableText("gui." + Modget.NAMESPACE + ".title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        updateListWidget = setUpdateListWidget();
        actionRowY = height - bottomRowHeight / 2 - padding - buttonHeight;
        doneY = height - bottomRowHeight / 2 + padding;
        refreshX = width / 2 - buttonWidth - padding;
        downloadX = width / 2 + padding;
        doneX = width / 2 - buttonWidth / 2;
        addUpdateListWidget();
        refreshButton = addRefreshButton();
        downloadButton = addDownloadButton();
        addDoneButton();
        super.init();
    }

    abstract ModUpdateListWidget<?> setUpdateListWidget();
    abstract void addUpdateListWidget();
    abstract ButtonWidget addRefreshButton();
    abstract ButtonWidget addDownloadButton();
    abstract ButtonWidget addDoneButton();

    protected void refreshButtonAction() {
        try {
            ModgetManager.reload();
            ModgetManager.REPO_MANAGER.refresh();
            ModgetManager.UPDATE_MANAGER.reset();
        } catch (Exception e) {}
    }
    protected void downloadButtonAction() {
        if (updateListWidget.getSelected() != null) {
            Util.getOperatingSystem().open(ModgetManager.UPDATE_MANAGER
                    .getPreferredDownloadPage(updateListWidget.getSelected().getModVersionVariantMod()).getUrl());
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        refreshButton.active = ModgetManager.UPDATE_MANAGER.searchForUpdates() != null;
        downloadButton.active = updateListWidget.getSelected() != null;
        updateListWidget.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);
        drawTitle(matrices, textRenderer, title, width / 2, 16, 16777215);
    }

    abstract void drawTitle(MatrixStack matrices, TextRenderer textRenderer, Text title, int x, int y, int colorCode);


    public TextRenderer getTextRenderer() {
        return textRenderer;
    }

    public int getBottomRowHeight() {
        return this.bottomRowHeight;
    }

    public int getButtonHeight() {
        return this.buttonHeight;
    }

    public int getButtonWidth() {
        return this.buttonWidth;
    }

    public int getPadding() {
        return this.padding;
    }

    public int getActionRowY() {
        return this.actionRowY;
    }

    public int getDoneY() {
        return this.doneY;
    }

    public int getRefreshX() {
        return this.refreshX;
    }

    public int getDownloadX() {
        return this.downloadX;
    }

    public int getDoneX() {
        return this.doneX;
    }

}
