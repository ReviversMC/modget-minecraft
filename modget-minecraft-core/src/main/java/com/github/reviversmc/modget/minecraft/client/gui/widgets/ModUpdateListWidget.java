package com.github.reviversmc.modget.minecraft.client.gui.widgets;

import java.util.ArrayList;
import java.util.List;

import com.github.reviversmc.modget.library.data.ModUpdate;
import com.github.reviversmc.modget.manifests.spec4.api.data.manifest.version.ModVersionVariant;
import com.github.reviversmc.modget.minecraft.Modget;
import com.github.reviversmc.modget.minecraft.client.gui.ModUpdateScreenBase;
import com.github.reviversmc.modget.minecraft.client.gui.entries.ModUpdateListEntry;

import org.apache.commons.lang3.tuple.Pair;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class ModUpdateListWidget<T extends ModUpdateScreenBase> extends EntryListWidget<ModUpdateListEntry> {
    private final T updateScreen;
    private List<ModUpdate> updates = new ArrayList<>(15);

    public ModUpdateListWidget(MinecraftClient client, T updateScreen) {
        super(client, updateScreen.width, updateScreen.height, 32, updateScreen.height - updateScreen.getBottomRowHeight(), 18);
        this.updateScreen = updateScreen;
    }

    public void init() {
        reload();
    }

    public void reload() {
        List<ModUpdate> newUpdates = new ArrayList<>();
        for (Pair<ModUpdate, List<Exception>> pair : Modget.INSTANCE.UPDATE_MANAGER.searchForUpdates()) {
            if (pair.getLeft() != null) {
                newUpdates.add(pair.getLeft());
            }
        }

        if (!updates.equals(newUpdates)) {
            clearEntries();
            setSelected(null);
            if (newUpdates != null) {
                for (ModUpdate update : newUpdates) {
                    for (ModVersionVariant modVersionVariant : update.getLatestModVersionVariants()) {
                        addEntry(new ModUpdateListEntry(modVersionVariant, updateScreen, this));
                    }
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

    public int getWidth() {
        return width;
    }

    @Override
    public void setSelected(ModUpdateListEntry entry) {
        super.setSelected(entry);
    }

    @Override
    public ModUpdateListEntry getSelected() {
        return super.getSelected();
    }

    @Override
    protected void renderBackground(MatrixStack matrixStack) {
        updateScreen.renderBackground(matrixStack);
    }

    @Override
    protected boolean isFocused() {
        return updateScreen.getFocused() == this;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        if (updates == null) {
            drawCenteredText(matrices, updateScreen.getTextRenderer(),
                    new TranslatableText("gui" + Modget.NAMESPACE + ".loading"), width / 2,
                    (bottom - top) / 2 - updateScreen.getTextRenderer().fontHeight + top, 16777215);
        }
    }
}
