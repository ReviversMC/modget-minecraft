package com.github.reviversmc.modget.minecraft.client.gui;

import com.github.reviversmc.modget.minecraft.client.gui.widgets.ModUpdateListWidget119;
import com.github.reviversmc.modget.minecraft.client.gui.widgets.ModUpdateListWidgetBase;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;

@Environment(EnvType.CLIENT)
public class ModUpdateScreen119 extends ModUpdateScreen117 {

    public ModUpdateScreen119(Screen parent) {
        super(parent);
    }

    @Override
    ModUpdateListWidgetBase<?> setUpdateListWidget() {
        return new ModUpdateListWidget119<ModUpdateScreen119>(client, this);
    }

}
