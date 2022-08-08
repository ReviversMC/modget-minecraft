package com.github.reviversmc.modget.minecraft.client.gui.widgets;

import com.github.reviversmc.modget.minecraft.client.gui.ModUpdateScreenBase;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public class ModUpdateListWidget116<T extends ModUpdateScreenBase> extends ModUpdateListWidgetBase<T> {

    public ModUpdateListWidget116(MinecraftClient client, T updateScreen) {
        super(client, updateScreen);
    }

}
