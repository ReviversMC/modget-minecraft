package com.github.nebelnidas.modget.command;

import java.util.ArrayList;

import com.github.nebelnidas.modget.Modget;
import com.github.nebelnidas.modgetlib.data.ManifestModVersion;
import com.github.nebelnidas.modgetlib.data.Package;
import com.github.nebelnidas.modgetlib.data.RecognizedMod;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class SearchCommand extends CommandBase {
    private static final String COMMAND = "search";
    private static final int PERMISSION_LEVEL = 3;

    void registerServer() {
        CommandRegistrationCallback.EVENT.register((dispatcher, isDedicated) ->
            dispatcher.register(CommandManager.literal(Modget.NAMESPACE_SERVER)
                .then(CommandManager.literal(COMMAND)
                    .then(CommandManager.argument("message", StringArgumentType.greedyString())
                        .requires(source -> source.hasPermissionLevel(PERMISSION_LEVEL))
                        .executes(context -> {
                            PlayerEntity player = context.getSource().getPlayer();

                            new StartThread(player, StringArgumentType.getString(context, "message")).start();
                            return 1;
                        })
                    )
                )
            ));
    }

    void registerClient() {
        ClientCommandManager.DISPATCHER.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal(Modget.NAMESPACE)
            .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal(COMMAND)
                .then(ClientCommandManager.argument("term", StringArgumentType.greedyString()).executes(context -> {
                    PlayerEntity player = ClientPlayerHack.getPlayer(context);

                    if (Modget.modPresentOnServer == true && player.hasPermissionLevel(PERMISSION_LEVEL)) {
                        player.sendMessage(new TranslatableText("info." + Modget.NAMESPACE + ".use_for_server_mods", Modget.NAMESPACE_SERVER)
                            .setStyle(Style.EMPTY.withColor(Formatting.BLUE)), false
                        );
                    }

                    new StartThread(player, StringArgumentType.getString(context, "term")).start();
                    return 1;
                }))
            )
        );
    }



    private class StartThread extends CommandBase.StartThread {
        private final int CHARS_NEEDED_FOR_EXTENSIVE_SEARCH = 4;
        private String term = "";

        public StartThread(PlayerEntity player, String term) {
            super(player);
            this.term = term;
        }

        @Override
        public void run() {
            super.run();

            isRunning = true;

            player.sendMessage(new TranslatableText(String.format("commands.%s.%s_start", Modget.NAMESPACE, COMMAND))
                .formatted(Formatting.YELLOW), false
            );

            ArrayList<RecognizedMod> modsFound = MANAGER.searchForMods(term, CHARS_NEEDED_FOR_EXTENSIVE_SEARCH);

            String notification = "";
            if (modsFound.size() > 0) {
                notification = String.format("commands.%s.%s_mods_found", Modget.NAMESPACE, COMMAND);
            } else if (term.length() >= CHARS_NEEDED_FOR_EXTENSIVE_SEARCH) {
                notification = String.format("commands.%s.%s_no_mods_found", Modget.NAMESPACE, COMMAND);
            } else {
                notification = String.format("commands.%s.%s_no_mods_found_enter_at_least_%s_chars", Modget.NAMESPACE, COMMAND, CHARS_NEEDED_FOR_EXTENSIVE_SEARCH);
            }
            player.sendMessage(new TranslatableText(notification)
                .formatted(Formatting.YELLOW), false
            );

            for (RecognizedMod mod : modsFound) {
                if (mod.getAvailablePackages().size() > 1) {
                    player.sendMessage(new TranslatableText("info." + Modget.NAMESPACE + ".multiple_packages_available", mod.getId()), true);
                }
                for (Package p : mod.getAvailablePackages()) {
                    ManifestModVersion modVersion = p.getLatestCompatibleModVersion();
                    if (modVersion == null) {
                        // Mod is incompatible
                        continue;
                    }

                    String message = "";
                    if (MANAGER.REPO_MANAGER.getRepos().size() > 1) {
                        message += String.format("[Repo %s] ", p.getParentLookupTableEntry().getParentLookupTable().getParentRepository().getId());
                    }
                    message += String.format("%s.%s %s", p.getPublisher(), mod.getId(), modVersion.getVersion());

                    player.sendMessage(new LiteralText(
                        message
                    ).styled(style ->
                        style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, modVersion.getDownloadPageUrls()[0].getUrl()))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableText(
                            "commands." + Modget.NAMESPACE + ".hover", String.format("%s %s", p.getName(), modVersion.getVersion())
                        )))
                    ), false);
                }
            }

            isRunning = false;
        }
    }
}
