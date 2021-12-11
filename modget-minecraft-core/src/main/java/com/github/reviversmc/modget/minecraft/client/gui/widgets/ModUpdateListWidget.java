package com.github.reviversmc.modget.minecraft.client.gui.widgets;

import java.util.ArrayList;
import java.util.List;

import com.github.reviversmc.modget.manifests.spec4.api.data.manifest.version.ModVersionVariant;
import com.github.reviversmc.modget.minecraft.Modget;
import com.github.reviversmc.modget.minecraft.client.gui.ModUpdateScreenBase;
import com.github.reviversmc.modget.minecraft.client.gui.entries.ModUpdateEntry;
import com.github.reviversmc.modget.minecraft.manager.ModgetManager;

import org.apache.commons.lang3.tuple.Pair;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class ModUpdateListWidget<T extends ModUpdateScreenBase> extends EntryListWidget<ModUpdateEntry> {
    private final T updateScreen;
    private List<ModVersionVariant> updates = new ArrayList<>(15);

    public ModUpdateListWidget(MinecraftClient client, T updateScreen) {
        super(client, updateScreen.width, updateScreen.height, 32, updateScreen.height - updateScreen.getBottomRowHeight(), 18);
        this.updateScreen = updateScreen;

        reload();
    }

    public void reload() {
        List<ModVersionVariant> newUpdates = new ArrayList<>();
        for (Pair<ModVersionVariant, List<Exception>> pair : ModgetManager.UPDATE_MANAGER.getUpdates()) {
            if (pair.getLeft() != null) {
                newUpdates.add(pair.getLeft());
            }
        }

        if (!updates.equals(newUpdates)) {
            clearEntries();
            setSelected(null);
            if (newUpdates != null) {
                for (ModVersionVariant update : newUpdates) {
                    addEntry(new ModUpdateEntry(update, updateScreen, this));
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
    public void setSelected(ModUpdateEntry entry) {
        super.setSelected(entry);
    }

    @Override
    public ModUpdateEntry getSelected() {
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
        reload();
        super.render(matrices, mouseX, mouseY, delta);
        if (updates == null) {
            drawCenteredText(matrices, updateScreen.getTextRenderer(),
                    new TranslatableText("gui" + Modget.NAMESPACE + ".loading"), width / 2,
                    (bottom - top) / 2 - updateScreen.getTextRenderer().fontHeight + top, 16777215);
        }
    }
}
