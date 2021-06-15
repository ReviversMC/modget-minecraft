package com.nebelnidas.modget.command;

import java.util.ArrayList;

import com.nebelnidas.modget.Modget;
import com.nebelnidas.modget.data.ModUpdate;
import com.nebelnidas.modget.tools.DataFetcher;

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
                    for (ModContainer mod : Modget.dataFetcher.getRecognizedModContainers()) {
                        messages.add(mod.getMetadata().getName() + " " + mod.getMetadata().getVersion());
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
                    for (ModUpdate update : updates) {
                        context.getSource().sendFeedback(new LiteralText(update.text).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, update.downloadURL)).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableText("commands." + Modget.NAMESPACE + ".hover")))), false);
                    }
                    return updates.length;
                }))
                .then(CommandManager.literal("refresh").requires(source -> source.hasPermissionLevel(3)).executes(context -> {
                    checkLoaded();
                    Modget.dataFetcher.refreshLookupTable();
                    Modget.dataFetcher.scanMods();
                    context.getSource().sendFeedback(new TranslatableText("commands." + Modget.NAMESPACE + ".refresh_start"), true);
                    return 1;
                }))
        ));
    }
}
