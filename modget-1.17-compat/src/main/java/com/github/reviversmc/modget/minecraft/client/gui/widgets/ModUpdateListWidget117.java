package com.github.reviversmc.modget.minecraft.client.gui.widgets;

import com.github.reviversmc.modget.minecraft.client.gui.ModUpdateScreenBase;
import com.github.reviversmc.modget.minecraft.client.gui.entries.ModUpdateListEntry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;

@Environment(EnvType.CLIENT)
public class ModUpdateListWidget117<T extends ModUpdateScreenBase> extends ModUpdateListWidget116<T> {

    public ModUpdateListWidget117(MinecraftClient client, T updateScreen) {
        super(client, updateScreen);
    }

    @Override
    public ModUpdateListEntry getSelected() {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            return super.getSelectedOrNull();
        }
        return super.getSelected();
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder narrationMessageBuilder) {
        // TODO
    }
}
