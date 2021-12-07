package com.github.reviversmc.modget.minecraft.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.LiteralText;

import java.io.IOException;

public class ModMenuConfig implements ModMenuApi {
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
                entryBuilder.startBooleanToggle(new LiteralText("Auto Check Updates"), ConfigManager.INSTANCE.getBooleanProperty("autoCheck"))
                .setSaveConsumer(b -> {
                    try {
                        ConfigManager.INSTANCE.setValue("autoCheck", b.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                })
                .build());
            return builder.build();
        };
    }
}
