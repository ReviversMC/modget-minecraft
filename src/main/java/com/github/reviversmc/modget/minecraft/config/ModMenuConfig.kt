//package com.github.pozitp.config
//
//import com.terraformersmc.modmenu.api.ConfigScreenFactory
//import com.terraformersmc.modmenu.api.ModMenuApi
//import me.shedaniel.clothconfig2.api.ConfigBuilder
//import net.minecraft.client.gui.screen.Screen
//import net.minecraft.text.TranslatableText
//
//
//class ModMenuConfig : ModMenuApi {
//    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> {
//        return ConfigScreenFactory { parent: Screen? ->
//            val builder = ConfigBuilder.create()
//                .setParentScreen(parent)
//                .setTitle(TranslatableText("title.fabrilousupdater.config"))
//            val general = builder.getOrCreateCategory(TranslatableText("category.fabrilousupdater.general"))
//            builder.setFallbackCategory(general)
//
//            val entryBuilder = builder.entryBuilder()
//
//            general.addEntry(
//                    entryBuilder.startBooleanToggle(TranslatableText("option.fabrilousupdater.autoCheck"), ConfigManager.INSTANCE.getBooleanProperty("autoCheck"))
//                            .setSaveConsumer {b: Boolean -> ConfigManager.INSTANCE.setValue("autoCheck", b.toString())}
//                    .build())
//
//
//            builder.build()
//        }
//    }
//}
