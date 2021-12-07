package com.github.reviversmc.modget.minecraft.client;

import com.github.reviversmc.modget.minecraft.config.ModgetConfig;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.LiteralText;

import java.io.IOException;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(new LiteralText("Auto Check Updates"));

            ConfigCategory general = builder.getOrCreateCategory(new LiteralText("General"));
            builder.setFallbackCategory(general);

            ConfigEntryBuilder entryBuilder = builder.entryBuilder();
            general.addEntry(
                entryBuilder.startBooleanToggle(new LiteralText("Auto Check Updates"), ModgetConfig.INSTANCE.getBooleanProperty("autoCheck"))
                .setSaveConsumer(b -> {
                    try {
                        ModgetConfig.INSTANCE.setValue("autoCheck", b.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                })
                .build());
            general.addEntry(
                entryBuilder.startBooleanToggle(new LiteralText("Auto Check Updates For Mods Who Request It"), ModgetConfig.INSTANCE.getBooleanProperty("autoCheckRequestingMods"))
                .setSaveConsumer(b -> {
                    try {
                        ModgetConfig.INSTANCE.setValue("autoCheckRequestingMods", b.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                })
                .build());

            return builder.build();
        };
    }
}
