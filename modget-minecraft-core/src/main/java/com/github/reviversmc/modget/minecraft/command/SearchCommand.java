package com.github.reviversmc.modget.minecraft.command;

import java.util.ArrayList;
import java.util.List;

import com.github.reviversmc.modget.library.util.ModSearcher;
import com.github.reviversmc.modget.manifests.spec4.api.data.manifest.common.NameUrlPair;
import com.github.reviversmc.modget.manifests.spec4.api.data.manifest.main.ModManifest;
import com.github.reviversmc.modget.manifests.spec4.api.data.manifest.version.ModVersion;
import com.github.reviversmc.modget.manifests.spec4.api.data.manifest.version.ModVersionVariant;
import com.github.reviversmc.modget.manifests.spec4.api.data.mod.ModPackage;
import com.github.reviversmc.modget.manifests.spec4.impl.data.manifest.common.NameUrlPairImpl;
import com.github.reviversmc.modget.minecraft.Modget;
import com.github.reviversmc.modget.minecraft.manager.ModgetManager;
import com.github.reviversmc.modget.minecraft.util.Utils;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import org.apache.commons.lang3.tuple.Pair;

import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class SearchCommand extends CommandBase {
    private static final String COMMAND = "search";
    private static final int PERMISSION_LEVEL = 3;
    private static final int CHARS_NEEDED_FOR_EXTENSIVE_SEARCH = 4;

    void registerServer() {
        CommandRegistrationCallback.EVENT.register((dispatcher, isDedicated) ->
            dispatcher.register(CommandManager.literal(Modget.NAMESPACE_SERVER)
                .then(CommandManager.literal(COMMAND)
                    .then(CommandManager.argument("term", StringArgumentType.greedyString())
                        .requires(source -> source.hasPermissionLevel(PERMISSION_LEVEL))
                        .executes(context -> {
                            PlayerEntity player = context.getSource().getPlayer();

                            new StartThread(player, StringArgumentType.getString(context, "term")).start();
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



    public void executeCommand(PlayerEntity player, String term) {
        player.sendMessage(new TranslatableText(String.format("commands.%s.%s_start", Modget.NAMESPACE, COMMAND)), false);

        Pair<List<ModVersionVariant>, List<Exception>> versionVariantsFoundWithExceptions
                = ModSearcher.create().searchForCompatibleMods(ModgetManager.REPO_MANAGER.getRepos(), term, CHARS_NEEDED_FOR_EXTENSIVE_SEARCH, Utils.create().getMinecraftVersion(), "fabric");

        List<ModVersionVariant> versionVariantsFound = versionVariantsFoundWithExceptions.getLeft();


        List<Text> messages = new ArrayList<>(15);
        for (ModVersionVariant modVersionVariant : versionVariantsFound) {
            ModVersion modVersion = modVersionVariant.getParentVersion();
            ModManifest modManifest = modVersion.getParentManifest();
            ModPackage modPackage = modManifest.getParentPackage();

            String tempMessageString = "";
            if (ModgetManager.REPO_MANAGER.getRepos().size() > 1) {
                tempMessageString += String.format("[Repo %s] ", modManifest.getParentLookupTableEntry().getParentLookupTable().getParentRepository().getId());
            }
            tempMessageString += String.format("%s %s", modPackage.getPackageId(), modVersion.getVersion());

            NameUrlPair downloadNameUrlPair = null;
            if (modVersionVariant.getDownloadPageUrls().getModrinth() != null) {
                downloadNameUrlPair = new NameUrlPairImpl("Modrinth", modVersionVariant.getDownloadPageUrls().getModrinth());
            } else if (modVersionVariant.getDownloadPageUrls().getCurseforge() != null) {
                downloadNameUrlPair = new NameUrlPairImpl("CurseForge", modVersionVariant.getDownloadPageUrls().getCurseforge());
            } else if (modVersionVariant.getDownloadPageUrls().getSourceControl() != null) {
                downloadNameUrlPair = new NameUrlPairImpl("Source Control", modVersionVariant.getDownloadPageUrls().getSourceControl());
            } else if (modVersionVariant.getDownloadPageUrls().getOther() != null) {
                for (NameUrlPair nameUrlPair : modVersionVariant.getDownloadPageUrls().getOther()) {
                    if (nameUrlPair.getUrl() != null) {
                        downloadNameUrlPair = new NameUrlPairImpl(nameUrlPair.getName(), nameUrlPair.getUrl());
                    }
                }
            }

            Text textMessage;
            if (downloadNameUrlPair == null) {
                textMessage = new LiteralText(tempMessageString);
            } else {
                final NameUrlPair finalNameUrlPair = downloadNameUrlPair;
                textMessage = new LiteralText(tempMessageString).styled(style ->
                    style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, finalNameUrlPair.getUrl()))
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableText(
                        "commands." + Modget.NAMESPACE + ".hover", String.format("%s %s", modManifest.getName(),
                        modVersion.getVersion()), finalNameUrlPair.getName()
                    ))));
            }
            messages.add(textMessage);
        }
        String messageTitle;
        if (versionVariantsFound.isEmpty()) {
            if (term.length() >= CHARS_NEEDED_FOR_EXTENSIVE_SEARCH) {
                messageTitle = String.format("commands.%s.%s_no_mods_found", Modget.NAMESPACE, COMMAND);
            } else {
                messageTitle = String.format("commands.%s.%s_no_mods_found_enter_at_least_%s_chars", Modget.NAMESPACE, COMMAND, CHARS_NEEDED_FOR_EXTENSIVE_SEARCH);
            }
        } else {
            messageTitle = String.format("commands.%s.%s_mods_found", Modget.NAMESPACE, COMMAND);
        }

        StringBuilder errorMessageBuilder = new StringBuilder();
        for (Exception e : versionVariantsFoundWithExceptions.getRight()) {
            errorMessageBuilder.append("\n" + e.getMessage());
        }

        player.sendMessage(new TranslatableText(messageTitle)
                .formatted(Formatting.YELLOW), false);

        for (Text text : messages) {
            player.sendMessage(text, false);
        }

        if (errorMessageBuilder.length() != 0) {
            player.sendMessage(new TranslatableText("Errors occurred while searching for mods:" + errorMessageBuilder.toString())
                    .formatted(Formatting.RED), false);
        }
    }



    private class StartThread extends CommandBase.StartThread {
        private PlayerEntity player;
        private String term = "";

        public StartThread(PlayerEntity player, String term) {
            super(player);
            this.term = term;
            this.player = player;
        }

        @Override
        public void run() {
            super.run();
            if (isRunning == true) {
                return;
            }

            isRunning = true;
            executeCommand(player, term);
            isRunning = false;
        }
    }
}
