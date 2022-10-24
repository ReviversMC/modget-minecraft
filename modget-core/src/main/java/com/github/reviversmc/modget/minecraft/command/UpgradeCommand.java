package com.github.reviversmc.modget.minecraft.command;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.github.reviversmc.modget.library.data.ModUpdate;
import com.github.reviversmc.modget.manifests.spec4.api.data.ManifestRepository;
import com.github.reviversmc.modget.manifests.spec4.api.data.manifest.common.NameUrlPair;
import com.github.reviversmc.modget.manifests.spec4.api.data.manifest.main.ModManifest;
import com.github.reviversmc.modget.manifests.spec4.api.data.manifest.version.ModVersion;
import com.github.reviversmc.modget.manifests.spec4.api.data.manifest.version.ModVersionVariant;
import com.github.reviversmc.modget.manifests.spec4.api.data.mod.ModPackage;
import com.github.reviversmc.modget.minecraft.Modget;
import com.github.reviversmc.modget.minecraft.compat.VersionAgnosticMessage;
import com.github.reviversmc.modget.minecraft.compat.VersionAgnosticText;
import com.github.reviversmc.modget.minecraft.compat.command.VersionAgnosticClientCommandManager;
import com.github.reviversmc.modget.minecraft.compat.command.VersionAgnosticServerCommandManager;

import net.fabricmc.api.EnvType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;

public class UpgradeCommand extends CommandBase {
    private static final String COMMAND = "upgrade";
    private static final int PERMISSION_LEVEL = 3;

    void registerServer() {
        VersionAgnosticServerCommandManager.get().register(
            CommandManager.literal(Modget.NAMESPACE_SERVER)
                .then(CommandManager.literal(COMMAND)
                    .requires(source -> source.hasPermissionLevel(PERMISSION_LEVEL))
                    .executes(context -> {
                        PlayerEntity player = context.getSource().getPlayer();

                        new StartThread(player).start();
                        return 1;
                    })
                )
        );
    }

    void registerClient() {
        VersionAgnosticClientCommandManager.get().registerLiteralCommand(
            Arrays.asList(Modget.NAMESPACE, COMMAND),
            player -> {
                if (Modget.INSTANCE.isModPresentOnServer() && player.hasPermissionLevel(PERMISSION_LEVEL)) {
                    VersionAgnosticMessage.get().sendInfo(player, VersionAgnosticText.get().translatable(
                            String.format("info.%s.use_for_server_mods", Modget.NAMESPACE),
                            String.format("/%s", Modget.NAMESPACE_SERVER)));
                }

                new StartThread(player).start();
            }
        );
    }



    public void executeCommand(PlayerEntity player) {
        if (Modget.INSTANCE.hasInitializationError()) {
            VersionAgnosticMessage.get().sendWarning(player, VersionAgnosticText.get().translatable(
                    String.format("warning.%s.init_failed_try_running_refresh", Modget.NAMESPACE),
                    String.format("/%s %s",
                            ENVIRONMENT == EnvType.CLIENT ? Modget.NAMESPACE : Modget.NAMESPACE_SERVER,
                            RefreshCommand.COMMAND)));
            return;
        }
        VersionAgnosticMessage.get().sendInfo(player, VersionAgnosticText.get().translatable(
                String.format("commands.%s.searching_for_updates", Modget.NAMESPACE)));


        StringBuilder errorMessageBuilder = new StringBuilder();
        List<SimpleEntry<String, SimpleEntry<ClickEvent, HoverEvent>>> messages = new ArrayList<>(15);

        for (Pair<ModUpdate, List<Exception>> updateExceptionPair : Modget.INSTANCE.UPDATE_MANAGER.searchForUpdates()) {
            for (Exception e : updateExceptionPair.getRight()) {
                errorMessageBuilder.append("\n" + e.getMessage());
            }
            if (updateExceptionPair.getLeft() == null) {
                continue;
            }

            ModUpdate modUpdate = updateExceptionPair.getLeft();
            for (ModVersionVariant modVersionVariant : modUpdate.getLatestModVersionVariants()) {
                ModVersion modVersion = modVersionVariant.getParentVersion();
                ModManifest modManifest = modVersion.getParentManifest();
                ModPackage modPackage = modManifest.getParentPackage();
                ManifestRepository repo = modManifest.getParentLookupTableEntry().getParentLookupTable().getParentRepository();

                String message = "";
                if (Modget.INSTANCE.REPO_MANAGER.getRepos().size() > 1) {
                    message += String.format("[Repo %s] ", repo.getId());
                }
                message += String.format("%s %s", modPackage.getPackageId(), modVersion.getVersion());


                NameUrlPair downloadNameUrlPair = Modget.INSTANCE.UPDATE_MANAGER.getPreferredDownloadPage(modVersionVariant);
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
        }

        if (messages.isEmpty()) {
            VersionAgnosticMessage.get().sendInfo(player, VersionAgnosticText.get().translatable(
                    String.format("commands.%s.no_updates_found", Modget.NAMESPACE)));
        } else {
            VersionAgnosticMessage.get().sendHeadline(player, VersionAgnosticText.get().translatable(
                    String.format("commands.%s.%s_title", Modget.NAMESPACE, COMMAND)));
            for (SimpleEntry<String, SimpleEntry<ClickEvent, HoverEvent>> message : messages) {
                VersionAgnosticMessage.get().sendText(player, VersionAgnosticText.get().translatable(
                        message.getKey(),
                        message.getValue().getKey(),
                        message.getValue().getValue()));
            }
        }

        if (errorMessageBuilder.length() != 0) {
            VersionAgnosticMessage.get().sendError(player, VersionAgnosticText.get().translatable(
                    String.format("error.%s.while_searching_updates", Modget.NAMESPACE),
                    errorMessageBuilder.toString()));
        }
    }



    private class StartThread extends CommandBase.StartThread {

        public StartThread(PlayerEntity player) {
            super(player);
        }

        @Override
        public void run() {
            super.run();
            if (isRunning) {
                return;
            }

            isRunning = true;
            executeCommand(player);
            isRunning = false;
        }
    }
}
