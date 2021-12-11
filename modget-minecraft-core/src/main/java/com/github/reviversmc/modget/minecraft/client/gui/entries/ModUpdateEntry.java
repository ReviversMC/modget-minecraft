package com.github.reviversmc.modget.minecraft.client.gui.entries;

import com.github.reviversmc.modget.manifests.spec4.api.data.manifest.version.ModVersionVariant;
import com.github.reviversmc.modget.minecraft.client.gui.ModUpdateScreenBase;
import com.github.reviversmc.modget.minecraft.client.gui.widgets.ModUpdateListWidget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class ModUpdateEntry extends EntryListWidget.Entry<ModUpdateEntry> {
    private final ModVersionVariant update;
    private final ModUpdateScreenBase updateScreen;
    private final ModUpdateListWidget<?> updateListWidget;

    public ModUpdateEntry(ModVersionVariant update, ModUpdateScreenBase updateScreen, ModUpdateListWidget<?> updateListWidget) {
        this.update = update;
        this.updateScreen = updateScreen;
        this.updateListWidget = updateListWidget;
    }

    @Override
    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX,
            int mouseY, boolean hovered, float tickDelta) {
        String text = updateScreen.getTextRenderer()
                .trimToWidth(update.getParentVersion().getParentManifest().getParentPackage().getPackageId() + " "
                        + update.getParentVersion().getVersion(), updateListWidget.getRowWidth() - 6);
            updateScreen.getTextRenderer().drawWithShadow(matrices, text,
                (float) (updateListWidget.getWidth() / 2 - updateScreen.getTextRenderer().getWidth(text) / 2), (float) (y + 3), 16777215,
                true);
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
        updateListWidget.setSelected(this);
    }

    public ModVersionVariant getUpdate() {
        return this.update;
    }

}
