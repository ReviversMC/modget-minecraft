package com.github.reviversmc.modget.minecraft.command;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.github.reviversmc.modget.library.util.ModSearcher;
import com.github.reviversmc.modget.manifests.spec4.api.data.manifest.common.NameUrlPair;
import com.github.reviversmc.modget.manifests.spec4.api.data.manifest.main.ModManifest;
import com.github.reviversmc.modget.manifests.spec4.api.data.manifest.version.ModVersion;
import com.github.reviversmc.modget.manifests.spec4.api.data.manifest.version.ModVersionVariant;
import com.github.reviversmc.modget.manifests.spec4.api.data.mod.ModPackage;
import com.github.reviversmc.modget.manifests.spec4.impl.data.manifest.common.NameUrlPairImpl;
import com.github.reviversmc.modget.minecraft.Modget;
import com.github.reviversmc.modget.minecraft.compat.VersionAgnosticCommandManager;
import com.github.reviversmc.modget.minecraft.compat.VersionAgnosticCommandManager.ArgumentTypeEnum;
import com.github.reviversmc.modget.minecraft.compat.VersionAgnosticMessage;
import com.github.reviversmc.modget.minecraft.compat.VersionAgnosticText;
import com.github.reviversmc.modget.minecraft.manager.ModgetManager;
import com.github.reviversmc.modget.minecraft.util.Utils;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;

public class SearchCommand extends CommandBase {
    private static final String[] COMMAND_PARTS = {"search", "term"};
    private static final int PERMISSION_LEVEL = 3;
    private static final int CHARS_NEEDED_FOR_EXTENSIVE_SEARCH = 4;

    void registerServer() {
        VersionAgnosticCommandManager.get().registerServerCommand(
            CommandManager.literal(Modget.NAMESPACE_SERVER)
                .then(CommandManager.literal(COMMAND_PARTS[0])
                    .then(CommandManager.argument(COMMAND_PARTS[1], StringArgumentType.greedyString())
                        .requires(source -> source.hasPermissionLevel(PERMISSION_LEVEL))
                        .executes(context -> {
                            PlayerEntity player = context.getSource().getPlayer();

                            new StartThread(player, StringArgumentType.getString(context, COMMAND_PARTS[1])).start();
                            return 1;
                        })
                    )
                )
        );
    }

    void registerClient() {
        VersionAgnosticCommandManager.get().registerClientArgumentCommand(
            Arrays.asList(Modget.NAMESPACE, COMMAND_PARTS[0], COMMAND_PARTS[1]),
            ArgumentTypeEnum.GREEDY_STRING,
            (player, term) -> {
                if (Modget.modPresentOnServer == true && player.hasPermissionLevel(PERMISSION_LEVEL)) {
                    VersionAgnosticMessage.get().sendInfo(player, VersionAgnosticText.get().translatable(
                            String.format("info.%s.use_for_server_mods", Modget.NAMESPACE),
                            String.format("/%s", Modget.NAMESPACE_SERVER)));
                }

                new StartThread(player, term).start();
            }
        );
    }



    public void executeCommand(PlayerEntity player, String term) {
        VersionAgnosticMessage.get().sendInfo(player, VersionAgnosticText.get().translatable(
                String.format("commands.%s.%s_start", Modget.NAMESPACE, COMMAND_PARTS[0])));

        Pair<List<ModVersionVariant>, List<Exception>> versionVariantsFoundWithExceptions
                = ModSearcher.create().searchForCompatibleMods(ModgetManager.REPO_MANAGER.getRepos(), term, CHARS_NEEDED_FOR_EXTENSIVE_SEARCH, Utils.create().getMinecraftVersion(), "fabric");

        List<ModVersionVariant> versionVariantsFound = versionVariantsFoundWithExceptions.getLeft();


        List<SimpleEntry<String, SimpleEntry<ClickEvent, HoverEvent>>> messages = new ArrayList<>(15);
        for (ModVersionVariant modVersionVariant : versionVariantsFound) {
            ModVersion modVersion = modVersionVariant.getParentVersion();
            ModManifest modManifest = modVersion.getParentManifest();
            ModPackage modPackage = modManifest.getParentPackage();

            String message = "";
            if (ModgetManager.REPO_MANAGER.getRepos().size() > 1) {
                message += String.format("[Repo %s] ", modManifest.getParentLookupTableEntry().getParentLookupTable().getParentRepository().getId());
            }
            message += String.format("%s %s", modPackage.getPackageId(), modVersion.getVersion());

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

            if (downloadNameUrlPair == null) {
                messages.add(new SimpleEntry<String, SimpleEntry<ClickEvent, HoverEvent>>(message, null));
            } else {
                messages.add(new SimpleEntry<String, SimpleEntry<ClickEvent, HoverEvent>>(message,
                        new SimpleEntry<ClickEvent, HoverEvent>(
                                new ClickEvent(ClickEvent.Action.OPEN_URL, downloadNameUrlPair.getUrl()),
                                new HoverEvent(HoverEvent.Action.SHOW_TEXT, VersionAgnosticText.get().translatable(
                                        String.format("commands.%s.hover", Modget.NAMESPACE),
                                        String.format("%s %s", modManifest.getName(), modVersion.getVersion()),
                                        downloadNameUrlPair.getName()
                                ))
                        )));
            }
        }
        if (versionVariantsFound.isEmpty()) {
            if (term.length() >= CHARS_NEEDED_FOR_EXTENSIVE_SEARCH) {
                VersionAgnosticMessage.get().sendInfo(player, VersionAgnosticText.get().translatable(
                        String.format("commands.%s.%s_no_mods_found", Modget.NAMESPACE, COMMAND_PARTS[0])));
            } else {
                VersionAgnosticMessage.get().sendInfo(player, VersionAgnosticText.get().translatable(
                        String.format("commands.%s.%s_no_mods_found_enter_at_least_%s_chars",
                                Modget.NAMESPACE,
                                COMMAND_PARTS[0],
                                CHARS_NEEDED_FOR_EXTENSIVE_SEARCH)));
            }
        } else {
            VersionAgnosticMessage.get().sendHeadline(player, VersionAgnosticText.get().translatable(
                    String.format("commands.%s.%s_mods_found", Modget.NAMESPACE, COMMAND_PARTS[0])));
        }

        StringBuilder errorMessageBuilder = new StringBuilder();
        for (Exception e : versionVariantsFoundWithExceptions.getRight()) {
            errorMessageBuilder.append("\n" + e.getMessage());
        }

        Text text;
        for (SimpleEntry<String, SimpleEntry<ClickEvent, HoverEvent>> message : messages) {
            if (message.getValue() == null) {
                text = VersionAgnosticText.get().translatable(message.getKey());
            } else {
                text = VersionAgnosticText.get().translatable(
                    message.getKey(),
                    message.getValue().getKey(),
                    message.getValue().getValue());
            }
            VersionAgnosticMessage.get().sendText(player, text);
        }

        if (errorMessageBuilder.length() != 0) {
            VersionAgnosticMessage.get().sendError(player, VersionAgnosticText.get().translatable(
                    String.format("error.%s.while_searching_mods", Modget.NAMESPACE),
                    errorMessageBuilder.toString()));
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
