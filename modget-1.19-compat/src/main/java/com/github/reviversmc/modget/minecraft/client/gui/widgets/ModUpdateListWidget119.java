package com.github.reviversmc.modget.minecraft.client.gui.widgets;

import com.github.reviversmc.modget.minecraft.client.gui.ModUpdateScreenBase;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public class ModUpdateListWidget119<T extends ModUpdateScreenBase> extends ModUpdateListWidget117<T> {

    public ModUpdateListWidget119(MinecraftClient client, T updateScreen) {
        super(client, updateScreen);
    }
}
