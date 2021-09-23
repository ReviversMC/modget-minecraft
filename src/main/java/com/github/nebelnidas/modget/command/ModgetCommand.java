package com.github.nebelnidas.modget.command;

import java.net.UnknownHostException;
import java.util.ArrayList;

import com.github.nebelnidas.modget.Modget;
import com.github.nebelnidas.modgetlib.data.ManifestModVersion;
import com.github.nebelnidas.modgetlib.data.Package;
import com.github.nebelnidas.modgetlib.data.RecognizedMod;
import com.github.nebelnidas.modgetlib.data.Repository;
import com.github.nebelnidas.modgetlib.manager.ModgetLibManager;

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
        ModgetLibManager manager = Modget.MODGET_MANAGER.MODGET_LIB_MANAGER;

        CommandRegistrationCallback.EVENT.register((dispatcher, isDedicated) -> dispatcher.register(CommandManager.literal(Modget.NAMESPACE)
                .then(CommandManager.literal("list").requires(source -> source.hasPermissionLevel(3)).executes(context -> {
                    context.getSource().sendFeedback(new TranslatableText("commands." + Modget.NAMESPACE + ".list_title").formatted(Formatting.YELLOW), false);
                    ArrayList<String> messages = new ArrayList<String>();
                    for (int i = 0; i < manager.getRecognizedMods().size(); i++) {
                        RecognizedMod mod = manager.getRecognizedMods().get(i);
                        messages.add(String.format("%s %s", WordUtils.capitalize(mod.getId()), mod.getCurrentVersion()));
                    }
                    java.util.Collections.sort(messages);
                    for (String message : messages) {
                        context.getSource().sendFeedback(new LiteralText(message), false);
                    }
                    return messages.size();
                }))
                .then(CommandManager.literal("upgrade").requires(source -> source.hasPermissionLevel(4)).executes(context -> {
                    context.getSource().sendFeedback(new TranslatableText("commands." + Modget.NAMESPACE + ".upgrade_title").formatted(Formatting.YELLOW), false);
                    for (RecognizedMod mod : manager.getModsWithUpdates()) {
                        if (mod.getAvailablePackages().size() > 1) {
                            context.getSource().sendFeedback(new TranslatableText("info." + Modget.NAMESPACE + ".multiple_packages_available", mod.getId()), true);
                        }
                        for (Package p : mod.getAvailablePackages()) {
                            ManifestModVersion newModVersion = p.getLatestCompatibleModVersion();
                            context.getSource().sendFeedback(new LiteralText(
                                String.format("Repo%s.%s.%s %s", p.getParentLookupTableEntry().getParentLookupTable().getParentRepository().getId(),
                                    p.getPublisher(), mod.getId(), newModVersion.getVersion())
                            ).styled(style ->
                                style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, newModVersion.getDownloadPageUrls()[0].getUrl()))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableText(
                                    "commands." + Modget.NAMESPACE + ".hover", String.format("%s %s", p.getName(), newModVersion.getVersion())
                                )))
                            ), false);
                        }
                    }
                    return manager.getModsWithUpdates().size();
                }))
                .then(CommandManager.literal("refresh").requires(source -> source.hasPermissionLevel(3)).executes(context -> {
                    context.getSource().sendFeedback(new TranslatableText("commands." + Modget.NAMESPACE + ".refresh_start").formatted(Formatting.YELLOW), true);
                    for (Repository repo : manager.REPO_MANAGER.getRepos()) {
                        try {
                            repo.refreshLookupTable();
                        } catch (Exception e) {
                            if (e instanceof UnknownHostException) {
                                context.getSource().sendFeedback(new TranslatableText("error." + Modget.NAMESPACE + ".github_connection_error"), true);
                            } else {
                                context.getSource().sendFeedback(new TranslatableText("error." + Modget.NAMESPACE + ".lookup_table_access_error"), true);
                            }
                        }
                    }
                    Modget.MODGET_MANAGER.reload();
                    return 1;
                }))
                .then(CommandManager.literal("repos")
                    .then(CommandManager.literal("list").requires(source -> source.hasPermissionLevel(3)).executes(context -> {
                        context.getSource().sendFeedback(new TranslatableText("commands." + Modget.NAMESPACE + ".repos_list_title").formatted(Formatting.YELLOW), true);
                        ArrayList<String> messages = new ArrayList<String>();
                        for (int i = 0; i < manager.REPO_MANAGER.getRepos().size(); i++) {
                            Repository repo = manager.REPO_MANAGER.getRepos().get(i);
                            messages.add(String.format("%s: %s", Integer.toString(repo.getId()), repo.getUri()));
                        }
                        // java.util.Collections.sort(messages);
                        for (String message : messages) {
                            context.getSource().sendFeedback(new LiteralText(message), false);
                        }
                        return messages.size();
                    }))
                )
        ));
    }
}
