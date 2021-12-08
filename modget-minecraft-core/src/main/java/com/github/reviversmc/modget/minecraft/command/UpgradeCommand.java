package com.github.reviversmc.modget.minecraft.command;

import java.util.ArrayList;
import java.util.List;

import com.github.reviversmc.modget.library.util.ModUpdateChecker;
import com.github.reviversmc.modget.manifests.spec4.api.data.manifest.common.NameUrlPair;
import com.github.reviversmc.modget.manifests.spec4.api.data.manifest.main.ModManifest;
import com.github.reviversmc.modget.manifests.spec4.api.data.manifest.version.ModVersion;
import com.github.reviversmc.modget.manifests.spec4.api.data.manifest.version.ModVersionVariant;
import com.github.reviversmc.modget.manifests.spec4.api.data.mod.InstalledMod;
import com.github.reviversmc.modget.manifests.spec4.api.data.mod.ModPackage;
import com.github.reviversmc.modget.manifests.spec4.impl.data.manifest.common.NameUrlPairImpl;
import com.github.reviversmc.modget.minecraft.Modget;
import com.github.reviversmc.modget.minecraft.manager.ModgetManager;
import com.github.reviversmc.modget.minecraft.util.Utils;
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

public class UpgradeCommand extends CommandBase {
    private static final String COMMAND = "upgrade";
    private static final int PERMISSION_LEVEL = 3;

    void registerServer() {
        CommandRegistrationCallback.EVENT.register((dispatcher, isDedicated) ->
            dispatcher.register(CommandManager.literal(Modget.NAMESPACE_SERVER)
                .then(CommandManager.literal(COMMAND)
                    .requires(source -> source.hasPermissionLevel(PERMISSION_LEVEL))
                    .executes(context -> {
                        PlayerEntity player = context.getSource().getPlayer();

                        new StartThread(player).start();
                        return 1;
                    })
                )
        ));
    }

    void registerClient() {
        ClientCommandManager.DISPATCHER.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal(Modget.NAMESPACE)
            .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal(COMMAND).executes(context -> {
                PlayerEntity player = ClientPlayerHack.getPlayer(context);

                if (Modget.modPresentOnServer == true && player.hasPermissionLevel(PERMISSION_LEVEL)) {
                    player.sendMessage(new TranslatableText("info." + Modget.NAMESPACE + ".use_for_server_mods", Modget.NAMESPACE_SERVER)
                        .setStyle(Style.EMPTY.withColor(Formatting.BLUE)), false
                    );
                }

                new StartThread(player).start();
                return 1;
            }))
        );
    }



    public void executeCommand(PlayerEntity player) {
        if (ModgetManager.getInitializationError() == true) {
            player.sendMessage(new TranslatableText(String.format("info.%s.init_failed_try_running_refresh", Modget.NAMESPACE), ENVIRONMENT == "CLIENT" ? Modget.NAMESPACE : Modget.NAMESPACE_SERVER)
                    .formatted(Formatting.YELLOW), false);
            return;
        }

        player.sendMessage(new TranslatableText(String.format("commands.%s.searching_for_updates", Modget.NAMESPACE)), false);


        StringBuilder errorMessageBuilder = new StringBuilder();
        List<Text> messages = new ArrayList<>(15);

        for (InstalledMod mod : ModgetManager.getRecognizedMods()) {
            Pair<ModVersionVariant, List<Exception>> update;
            try {
                update = ModUpdateChecker.create().searchForModUpdate(mod, ModgetManager.REPO_MANAGER.getRepos(), Utils.create().getMinecraftVersion(), "fabric");
            } catch (Exception e) {
                errorMessageBuilder.append("\n" + e.getMessage());
                continue;
            }

            for (Exception e : update.getRight()) {
                errorMessageBuilder.append("\n" + e.getMessage());
            }
            if (update.getLeft() == null) {
                continue;
            }

            ModVersionVariant modVersionVariant = update.getLeft();
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

        if (messages.isEmpty()) {
            player.sendMessage(new TranslatableText(String.format("commands.%s.no_updates_found", Modget.NAMESPACE)), false);
        } else {
            player.sendMessage(new TranslatableText(String.format("commands.%s.%s_title", Modget.NAMESPACE, COMMAND))
                    .formatted(Formatting.YELLOW), false);
            for (Text text : messages) {
                player.sendMessage(text, false);
            }
        }

        if (errorMessageBuilder.length() != 0) {
            player.sendMessage(new TranslatableText("Errors occurred while searching for updates:" + errorMessageBuilder.toString())
                    .formatted(Formatting.RED), false);
        }

    }



    private class StartThread extends CommandBase.StartThread {

        public StartThread(PlayerEntity player) {
            super(player);
        }

        @Override
        public void run() {
            super.run();
            if (isRunning == true) {
                return;
            }

            isRunning = true;
            executeCommand(player);
            isRunning = false;
        }
    }
}
