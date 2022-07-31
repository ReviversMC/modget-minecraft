package com.github.reviversmc.modget.minecraft.client.gui.widgets;

import com.github.reviversmc.modget.minecraft.client.gui.ModUpdateScreenBase;
import com.github.reviversmc.modget.minecraft.client.gui.entries.ModUpdateListEntry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public class ModUpdateListWidget116<T extends ModUpdateScreenBase> extends ModUpdateListWidgetBase<T> {

    public ModUpdateListWidget116(MinecraftClient client, T updateScreen) {
        super(client, updateScreen);
    }

    @Override
    public ModUpdateListEntry getSelectedElement() {
        return super.getSelected();
    }
}
