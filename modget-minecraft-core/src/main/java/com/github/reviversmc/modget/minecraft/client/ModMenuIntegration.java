package com.github.reviversmc.modget.minecraft.client;

import com.github.reviversmc.modget.minecraft.config.ModgetConfig;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.LiteralText;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(new LiteralText("Modget Options"));

            ConfigCategory general = builder.getOrCreateCategory(new LiteralText("General"));
            builder.setFallbackCategory(general);

            ConfigEntryBuilder entryBuilder = builder.entryBuilder();
            general.addEntry(
                entryBuilder.startBooleanToggle(new LiteralText("Auto Check Updates"), ModgetConfig.INSTANCE.getAutoCheck())
                .setSaveConsumer(value -> {
                    ModgetConfig.INSTANCE.setAutoCheck(value);
                })
                .build());

            return builder.build();
        };
    }
}
