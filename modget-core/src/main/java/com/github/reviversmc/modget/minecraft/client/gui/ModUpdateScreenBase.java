package com.github.reviversmc.modget.minecraft.client.gui;

import java.util.concurrent.atomic.AtomicBoolean;

import com.github.reviversmc.modget.minecraft.Modget;
import com.github.reviversmc.modget.minecraft.client.gui.widgets.ModUpdateListWidgetBase;
import com.github.reviversmc.modget.minecraft.compat.VersionAgnosticText;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public abstract class ModUpdateScreenBase extends Screen {
    protected ModUpdateListWidgetBase<?> updateListWidget;
    protected AtomicBoolean updatesReady;
    protected ButtonWidget refreshButton;
    protected ButtonWidget downloadButton;
    protected ButtonWidget doneButton;
    protected final Screen parent;
    protected Text searchingForUpdatesText = VersionAgnosticText.get().translatable("commands." + Modget.NAMESPACE + ".searching_for_updates");
    protected Text refreshButtonText = VersionAgnosticText.get().translatable("gui." + Modget.NAMESPACE + ".refresh");
    protected Text downloadButtonText = VersionAgnosticText.get().translatable("gui." + Modget.NAMESPACE + ".download");
    protected Text doneButtonText = VersionAgnosticText.get().DONE();
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
        super(VersionAgnosticText.get().translatable("gui." + Modget.NAMESPACE + ".title"));
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
        refreshButton = addRefreshButton(refreshButtonText);
        downloadButton = addDownloadButton(downloadButtonText);
        doneButton = addDoneButton(doneButtonText);
        updatesReady = new AtomicBoolean(false);
        refresh();
        super.init();
    }

    private void refresh() {
        refreshButton.active = false;
        new Thread(() -> {
            Modget.INSTANCE.UPDATE_MANAGER.searchForUpdates();
            updatesReady.set(true);
            refreshButton.active = Modget.INSTANCE.UPDATE_MANAGER.searchForUpdates() != null;
            updateListWidget.init();
        }).start();
    }

    abstract ModUpdateListWidgetBase<?> setUpdateListWidget();
    abstract void addUpdateListWidget();
    abstract ButtonWidget addRefreshButton(Text text);
    abstract ButtonWidget addDownloadButton(Text text);
    abstract ButtonWidget addDoneButton(Text text);

    protected void refreshButtonAction() {
        refreshButton.active = false;
        downloadButton.active = false;
        updatesReady.set(false);
        new Thread(() -> {
            try {
                Modget.INSTANCE.reload();
                Modget.INSTANCE.REPO_MANAGER.refresh();
                Modget.INSTANCE.UPDATE_MANAGER.reset();
                refresh();
            } catch (Exception e) {}
        }).start();
    }

    protected void downloadButtonAction() {
        if (updateListWidget.getSelected() != null) {
            Util.getOperatingSystem().open(Modget.INSTANCE.UPDATE_MANAGER
                    .getPreferredDownloadPage(updateListWidget.getSelected().getModVersionVariantMod()).getUrl());
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        if (updatesReady.get()) {
            updateListWidget.render(matrices, mouseX, mouseY, delta);
        } else {
            drawCenteredText(matrices, textRenderer, searchingForUpdatesText,
                    width / 2, height / 2 - bottomRowHeight / 2, 16777215);
        }
        drawCenteredText(matrices, textRenderer, title, width / 2, 16, 16777215);
        downloadButton.active = updateListWidget.getSelected() != null;
        refreshButton.render(matrices, mouseX, mouseY, delta);
        downloadButton.render(matrices, mouseX, mouseY, delta);
        doneButton.render(matrices, mouseX, mouseY, delta);
    }


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
