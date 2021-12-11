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
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public abstract class ModUpdateScreenBase extends Screen {
    public ModUpdateListWidget<?> list;
    protected ButtonWidget download;
    protected ButtonWidget refresh;
    protected final Screen parent;
    protected int bottomRowHeight = 60;
    protected int buttonHeight = 20;
    protected int buttonWidth = 150;
    protected int padding = 2;
    protected int actionRowY = height - bottomRowHeight / 2 - padding - buttonHeight;
    protected int doneY = height - bottomRowHeight / 2 + padding;
    protected int refreshX = width / 2 - buttonWidth - padding;
    protected int downloadX = width / 2 + padding;
    protected int doneX = width / 2 - buttonWidth / 2;


    public ModUpdateScreenBase(Screen parent) {
        super(new TranslatableText("gui." + Modget.NAMESPACE + ".title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        addButtons();
        super.init();
    }

    abstract void addButtons();

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        refresh.active = ModgetManager.UPDATE_MANAGER.getUpdates() != null;
        download.active = list.getSelected() != null;
        list.render(matrices, mouseX, mouseY, delta);
        drawCenteredText(matrices, textRenderer, title, width / 2, 16, 16777215);
        super.render(matrices, mouseX, mouseY, delta);
    }



    public TextRenderer getTextRenderer() {
        return textRenderer;
    }

    // public ModUpdateListWidget getList() {
    //     return this.list;
    // }

    // public ButtonWidget getDownload() {
    //     return this.download;
    // }

    // public ButtonWidget getRefresh() {
    //     return this.refresh;
    // }

    // public Screen getParent() {
    //     return this.parent;
    // }

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
