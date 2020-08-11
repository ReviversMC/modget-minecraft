package com.thebrokenrail.modupdater.command;

import com.thebrokenrail.modupdater.ModUpdater;
import com.thebrokenrail.modupdater.data.ModUpdate;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.CommandException;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class ModUpdaterCommand {
    private static void checkLoaded() throws CommandException {
        if (ModUpdater.getUpdates() == null) {
            throw new CommandException(new TranslatableText("commands." + ModUpdater.NAMESPACE + ".not_loaded"));
        }
    }

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, isDedicated) -> dispatcher.register(CommandManager.literal(ModUpdater.NAMESPACE)
                .then(CommandManager.literal("list").executes(context -> {
                    checkLoaded();
                    context.getSource().sendFeedback(new TranslatableText("commands." + ModUpdater.NAMESPACE + ".list_title").formatted(Formatting.YELLOW), false);
                    ModUpdate[] updates = ModUpdater.getUpdates();
                    assert updates != null;
                    for (ModUpdate update : updates) {
                        context.getSource().sendFeedback(new LiteralText(update.text).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, update.downloadURL)).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableText("commands." + ModUpdater.NAMESPACE + ".hover")))), false);
                    }
                    return updates.length;
                }))
                .then(CommandManager.literal("refresh").requires(source -> source.hasPermissionLevel(3)).executes(context -> {
                    checkLoaded();
                    ModUpdater.findUpdates();
                    context.getSource().sendFeedback(new TranslatableText("commands." + ModUpdater.NAMESPACE + ".refresh_start"), true);
                    return 1;
                }))
        ));
    }
}
