package com.github.reviversmc.modget.minecraft.client.gui.entries;

import com.github.reviversmc.modget.manifests.spec4.api.data.manifest.version.ModVersionVariant;
import com.github.reviversmc.modget.minecraft.client.gui.ModUpdateScreenBase;
import com.github.reviversmc.modget.minecraft.client.gui.widgets.ModUpdateListWidgetBase;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class ModUpdateListEntry extends EntryListWidget.Entry<ModUpdateListEntry> {
    private final ModVersionVariant modVersionVariant;
    private final ModUpdateScreenBase updateScreen;
    private final ModUpdateListWidgetBase<?> updateListWidget;

    public ModUpdateListEntry(ModVersionVariant modVersionVariant, ModUpdateScreenBase updateScreen, ModUpdateListWidgetBase<?> updateListWidget) {
        this.modVersionVariant = modVersionVariant;
        this.updateScreen = updateScreen;
        this.updateListWidget = updateListWidget;
    }

    @Override
    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX,
            int mouseY, boolean hovered, float tickDelta) {
        String text = updateScreen.getTextRenderer()
                .trimToWidth(modVersionVariant.getParentVersion().getParentManifest().getParentPackage().getPackageId() + " "
                        + modVersionVariant.getParentVersion().getVersion(), updateListWidget.getRowWidth() - 6);
        updateScreen.getTextRenderer().drawWithShadow(matrices, text,
                (float) (updateListWidget.getWidth() / 2 - updateScreen.getTextRenderer().getWidth(text) / 2),
                (float) (y + 3), 16777215,
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

    public ModVersionVariant getModVersionVariantMod() {
        return this.modVersionVariant;
    }

}
