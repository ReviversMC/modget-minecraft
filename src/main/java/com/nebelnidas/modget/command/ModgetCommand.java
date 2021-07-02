package com.nebelnidas.modget.command;

import java.net.UnknownHostException;
import java.util.ArrayList;

import com.nebelnidas.modget.Modget;
import com.nebelnidas.modget.data.ManifestModVersion;
import com.nebelnidas.modget.data.Package;
import com.nebelnidas.modget.data.RecognizedMod;

import org.apache.commons.text.WordUtils;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class ModgetCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, isDedicated) -> dispatcher.register(CommandManager.literal(Modget.NAMESPACE)
                .then(CommandManager.literal("list").executes(context -> {
                    context.getSource().sendFeedback(new TranslatableText("commands." + Modget.NAMESPACE + ".list_title").formatted(Formatting.YELLOW), false);
                    ArrayList<String> messages = new ArrayList<String>();
                    for (int i = 0; i < Modget.MAIN_MANAGER.getRecognizedMods().size(); i++) {
                        RecognizedMod mod = Modget.MAIN_MANAGER.getRecognizedMods().get(i);
                        messages.add(String.format("%s %s", WordUtils.capitalize(mod.getId()), mod.getCurrentVersion()));
                    }
                    java.util.Collections.sort(messages);
                    for (String message : messages) {
                        context.getSource().sendFeedback(new LiteralText(message), false);
                    }
                    return messages.size();
                }))
                .then(CommandManager.literal("upgrade").executes(context -> {
                    context.getSource().sendFeedback(new TranslatableText("commands." + Modget.NAMESPACE + ".upgrade_title").formatted(Formatting.YELLOW), false);
                    for (RecognizedMod mod : Modget.MAIN_MANAGER.getModsWithUpdates()) {
                        if (mod.getAvailablePackages().size() > 1) {
                            context.getSource().sendFeedback(new TranslatableText("info." + Modget.NAMESPACE + ".multiple_packages_available", mod.getId()), true);
                        }
                        for (Package p : mod.getAvailablePackages()) {
                            ManifestModVersion newModVersion = p.getLatestCompatibleModVersion();
                            context.getSource().sendFeedback(new LiteralText(String.format("%s.%s %s", p.getPublisher(), WordUtils.capitalize(mod.getId()), newModVersion.getVersion())).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, newModVersion.getDownloadPageUrls()[0].getUrl())).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableText("commands." + Modget.NAMESPACE + ".hover")))), false);
                        }
                    }
                    return Modget.MAIN_MANAGER.getModsWithUpdates().size();
                }))
                .then(CommandManager.literal("refresh").requires(source -> source.hasPermissionLevel(3)).executes(context -> {
                    context.getSource().sendFeedback(new TranslatableText("commands." + Modget.NAMESPACE + ".refresh_start").formatted(Formatting.YELLOW), true);
                    try {
                        Modget.MAIN_MANAGER.LOOKUP_TABLE_MANAGER.refreshLookupTable();
                    } catch (UnknownHostException e) {
                        context.getSource().sendFeedback(new TranslatableText("error." + Modget.NAMESPACE + ".github_connection_error"), true);
                    } catch (Exception e) {
                        context.getSource().sendFeedback(new TranslatableText("error." + Modget.NAMESPACE + ".lookup_table_access_error"), true);
                    }
                    Modget.MAIN_MANAGER.reload();
                    return 1;
                }))
        ));
    }
}
