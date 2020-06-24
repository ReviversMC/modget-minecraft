package com.thebrokenrail.modupdater.client.gui;

import com.thebrokenrail.modupdater.ModUpdater;
import com.thebrokenrail.modupdater.util.ModUpdate;
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

@Environment(EnvType.CLIENT)
public class ModUpdateScreen extends Screen {
    private ModUpdateListWidget list;
    private ButtonWidget download;
    private final Screen parent;

    public ModUpdateScreen(Screen parent) {
        super(new TranslatableText("gui." + ModUpdater.NAMESPACE + ".title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        list = new ModUpdateListWidget(client, this);
        children.add(list);
        int buttonWidth = 150;
        int paddingX = 5;
        int doneX = width / 2 - buttonWidth - paddingX;
        int downloadX = width / 2 + paddingX;
        download = addButton(new ButtonWidget(downloadX, height - 30, buttonWidth, 20, new TranslatableText("gui." + ModUpdater.NAMESPACE + ".download"), buttonWidget -> {
            if (list.getSelected() != null) {
                Util.getOperatingSystem().open(list.getSelected().update.downloadURL);
            }
        }));
        download.active = false;
        addButton(new ButtonWidget(doneX, height - 30, buttonWidth, 20, ScreenTexts.DONE, buttonWidget -> {
            assert client != null;
            client.openScreen(parent);
        }));
        super.init();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        list.render(matrices, mouseX, mouseY, delta);
        drawCenteredText(matrices, textRenderer, title, width / 2, 16, 16777215);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Environment(EnvType.CLIENT)
    private static class ModUpdateListWidget extends EntryListWidget<ModUpdateEntry> {
        private final ModUpdateScreen screen;

        private ModUpdateListWidget(MinecraftClient client, ModUpdateScreen screen) {
            super(client, screen.width, screen.height, 32, screen.height - 40, 18);
            this.screen = screen;

            for (ModUpdate update : ModUpdater.getUpdates()) {
                addEntry(new ModUpdateEntry(update, screen, this));
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
                screen.download.active = true;
            } else {
                screen.download.active = false;
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
