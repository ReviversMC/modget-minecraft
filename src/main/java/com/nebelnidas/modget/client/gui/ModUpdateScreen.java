package com.nebelnidas.modget.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;

import java.util.Arrays;

import com.nebelnidas.modget.Modget;
import com.nebelnidas.modget.data.ModUpdate;

@Environment(EnvType.CLIENT)
public class ModUpdateScreen extends Screen {
    public ModUpdateListWidget list;
    private ButtonWidget download;
    private ButtonWidget refresh;
    private final Screen parent;

    private static final int BOTTOM_ROW = 60;

    public ModUpdateScreen(Screen parent) {
        super(new TranslatableText("gui." + Modget.NAMESPACE + ".title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        list = new ModUpdateListWidget(client, this);
        children.add(list);
        int buttonHeight = 20;
        int padding = 2;
        int actionRowY = height - BOTTOM_ROW / 2 - padding - buttonHeight;
        int doneY = height - BOTTOM_ROW / 2 + padding;
        int buttonWidth = 150;
        int refreshX = width / 2 - buttonWidth - padding;
        int downloadX = width / 2 + padding;
        int doneX = width / 2 - buttonWidth / 2;
        refresh = addButton(new ButtonWidget(refreshX, actionRowY, buttonWidth, buttonHeight, new TranslatableText("gui." + Modget.NAMESPACE + ".refresh"), buttonWidget -> Modget.findUpdates()));
        download = addButton(new ButtonWidget(downloadX, actionRowY, buttonWidth, buttonHeight, new TranslatableText("gui." + Modget.NAMESPACE + ".download"), buttonWidget -> {
            if (list.getSelected() != null) {
                Util.getOperatingSystem().open(list.getSelected().update.downloadURL);
            }
        }));
        addButton(new ButtonWidget(doneX, doneY, buttonWidth, buttonHeight, ScreenTexts.DONE, buttonWidget -> {
            assert client != null;
            client.openScreen(parent);
        }));
        super.init();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        refresh.active = Modget.getUpdates() != null;
        download.active = list.getSelected() != null;
        list.render(matrices, mouseX, mouseY, delta);
        drawCenteredText(matrices, textRenderer, title, width / 2, 16, 16777215);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Environment(EnvType.CLIENT)
    private static class ModUpdateListWidget extends EntryListWidget<ModUpdateEntry> {
        private final ModUpdateScreen screen;
        private ModUpdate[] updates = null;

        private ModUpdateListWidget(MinecraftClient client, ModUpdateScreen screen) {
            super(client, screen.width, screen.height, 32, screen.height - BOTTOM_ROW, 18);
            this.screen = screen;

            reload();
        }

        public void reload() {
            ModUpdate[] newUpdates = Modget.getUpdates();
            if (!Arrays.equals(updates, newUpdates)) {
                clearEntries();
                setSelected(null);
                if (newUpdates != null) {
                    for (ModUpdate update : newUpdates) {
                        addEntry(new ModUpdateEntry(update, screen, this));
                    }
                }
                updates = newUpdates;
            }
        }

        @Override
        public int getRowWidth() {
            return width - 40;
        }

        @Override
        protected int getScrollbarPositionX() {
            return width - 14;
        }

        private int getWidth() {
            return width;
        }

        @Override
        public void setSelected(ModUpdateEntry entry) {
            super.setSelected(entry);
            if (entry != null) {
                NarratorManager.INSTANCE.narrate(new TranslatableText("narrator.select", entry.update.text).asString());
            }
        }

        @Override
        protected void renderBackground(MatrixStack matrixStack) {
            screen.renderBackground(matrixStack);
        }

        @Override
        protected boolean isFocused() {
            return screen.getFocused() == this;
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            reload();
            super.render(matrices, mouseX, mouseY, delta);
            if (updates == null) {
                drawCenteredText(matrices, screen.textRenderer, new TranslatableText("gui.modget.loading"), width / 2, (bottom - top) / 2 - screen.textRenderer.fontHeight + top, 16777215);
            }
        }
    }

    @Environment(EnvType.CLIENT)
    private static class ModUpdateEntry extends EntryListWidget.Entry<ModUpdateScreen.ModUpdateEntry> {
        private final ModUpdate update;
        private final ModUpdateScreen screen;
        private final ModUpdateListWidget parent;

        private ModUpdateEntry(ModUpdate update, ModUpdateScreen screen, ModUpdateListWidget parent) {
            this.update = update;
            this.screen = screen;
            this.parent = parent;
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            String text = screen.textRenderer.trimToWidth(update.text, parent.getRowWidth() - 6);
            screen.textRenderer.drawWithShadow(matrices, text, (float) (parent.getWidth() / 2 - screen.textRenderer.getWidth(text) / 2), (float) (y + 3), 16777215, true);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == 0) {
                onPressed();
                return true;
            } else {
                return false;
            }
        }

        private void onPressed() {
            parent.setSelected(this);
        }
    }
}
