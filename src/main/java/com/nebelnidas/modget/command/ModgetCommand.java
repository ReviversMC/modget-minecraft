package com.nebelnidas.modget.command;

import java.net.UnknownHostException;
import java.util.ArrayList;

import com.nebelnidas.modget.Modget;
import com.nebelnidas.modget.data.ManifestMod;
import com.nebelnidas.modget.data.ManifestModVersion;
import com.nebelnidas.modget.legacy.data.ModUpdate;

import org.apache.commons.text.WordUtils;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.command.CommandException;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class ModgetCommand {
    private static void checkLoaded() throws CommandException {
        if (Modget.getUpdates() == null) {
            throw new CommandException(new TranslatableText("commands." + Modget.NAMESPACE + ".not_loaded"));
        }
    }

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, isDedicated) -> dispatcher.register(CommandManager.literal(Modget.NAMESPACE)
                .then(CommandManager.literal("list").executes(context -> {
                    checkLoaded();
                    context.getSource().sendFeedback(new TranslatableText("commands." + Modget.NAMESPACE + ".list_title").formatted(Formatting.YELLOW), false);
                    ArrayList<String> messages = new ArrayList<String>();
                    for (int i = 0; i < Modget.MAIN_MANAGER.MANIFEST_MANAGER.getRecognizedManifestMods().size(); i++) {
                        ManifestMod modManifest = Modget.MAIN_MANAGER.MANIFEST_MANAGER.getRecognizedManifestMods().get(i);
                        ModContainer modContainer = Modget.MAIN_MANAGER.getRecognizedModContainers().get(i);
                        messages.add(String.format("%s.%s %s", modManifest.getPublisher(), WordUtils.capitalize(modManifest.getId()), modContainer.getMetadata().getVersion()));
                    }
                    java.util.Collections.sort(messages);
                    for (String message : messages) {
                        context.getSource().sendFeedback(new LiteralText(message), false);
                    }
                    return messages.size();
                }))
                .then(CommandManager.literal("upgrade").executes(context -> {
                    checkLoaded();
                    context.getSource().sendFeedback(new TranslatableText("commands." + Modget.NAMESPACE + ".upgrade_title").formatted(Formatting.YELLOW), false);
                    ModUpdate[] updates = Modget.getUpdates();
                    assert updates != null;
                    for (ManifestMod mod : Modget.MAIN_MANAGER.getModManifestsWithUpdates()) {
                        ManifestModVersion newModVersion = Modget.MAIN_MANAGER.findManifestModVersionMatchingCurrentMinecraftVersion(mod);
                        context.getSource().sendFeedback(new LiteralText(String.format("%s.%s %s", mod.getPublisher(), WordUtils.capitalize(mod.getId()), newModVersion.getVersion())).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, newModVersion.getUrls()[0])).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableText("commands." + Modget.NAMESPACE + ".hover")))), false);
                    }
                    return updates.length;
                }))
                .then(CommandManager.literal("refresh").requires(source -> source.hasPermissionLevel(3)).executes(context -> {
                    checkLoaded();
                    context.getSource().sendFeedback(new TranslatableText("commands." + Modget.NAMESPACE + ".refresh_start").formatted(Formatting.YELLOW), true);
                    try {
                        Modget.MAIN_MANAGER.LOOKUP_TABLE_MANAGER.refreshLookupTable();
                    } catch (UnknownHostException e) {
                        context.getSource().sendFeedback(new TranslatableText("error." + Modget.NAMESPACE + ".github_connection_error"), true);
                    } catch (Exception e) {
                        context.getSource().sendFeedback(new TranslatableText("error." + Modget.NAMESPACE + ".lookup_table_access_error"), true);
                    }
                    Modget.MAIN_MANAGER.scanMods();
                    return 1;
                }))
        ));
    }
}
