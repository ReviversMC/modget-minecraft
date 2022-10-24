package com.github.reviversmc.modget.minecraft.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.github.reviversmc.modget.library.data.ModUpdate;
import com.github.reviversmc.modget.library.util.ModUpdateChecker;
import com.github.reviversmc.modget.manifests.spec4.api.data.ManifestRepository;
import com.github.reviversmc.modget.manifests.spec4.api.data.manifest.common.NameUrlPair;
import com.github.reviversmc.modget.manifests.spec4.api.data.manifest.main.ModManifest;
import com.github.reviversmc.modget.manifests.spec4.api.data.manifest.version.ModVersion;
import com.github.reviversmc.modget.manifests.spec4.api.data.manifest.version.ModVersionVariant;
import com.github.reviversmc.modget.manifests.spec4.api.data.mod.ModPackage;
import com.github.reviversmc.modget.minecraft.Modget;
import com.github.reviversmc.modget.minecraft.api.InstalledModAdvanced;
import com.github.reviversmc.modget.minecraft.compat.VersionAgnosticMessage;
import com.github.reviversmc.modget.minecraft.compat.VersionAgnosticText;
import com.github.reviversmc.modget.minecraft.compat.command.ArgumentTypeType;
import com.github.reviversmc.modget.minecraft.compat.command.VersionAgnosticClientCommandManager;
import com.github.reviversmc.modget.minecraft.compat.command.VersionAgnosticServerCommandManager;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.fabricmc.api.EnvType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class MigrationCheckCommand extends CommandBase {
    private static final String[] COMMAND_PARTS = {"migrationcheck", "minecraftVersion"};
    private static final int PERMISSION_LEVEL = 3;

    void registerServer() {
        VersionAgnosticServerCommandManager.get().register(
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
        VersionAgnosticClientCommandManager.get().registerArgumentCommand(
            Arrays.asList(Modget.NAMESPACE, COMMAND_PARTS[0], COMMAND_PARTS[1]),
            ArgumentTypeType.STRING,
            (player, minecraftVersion) -> {
                if (Modget.INSTANCE.isModPresentOnServer() && player.hasPermissionLevel(PERMISSION_LEVEL)) {
                    VersionAgnosticMessage.get().sendInfo(player, VersionAgnosticText.get().translatable(
                            String.format("info.%s.use_for_server_mods", Modget.NAMESPACE),
                            String.format("/%s", Modget.NAMESPACE_SERVER)));
                }

                new StartThread(player, minecraftVersion).start();
            }
        );
    }


    public void executeCommand(PlayerEntity player, String minecraftVersion) {
        if (Modget.INSTANCE.hasInitializationError()) {
            VersionAgnosticMessage.get().sendWarning(player, VersionAgnosticText.get().translatable(
                    String.format("warning.%s.init_failed_try_running_refresh", Modget.NAMESPACE),
                    String.format("/%s %s",
                            ENVIRONMENT == EnvType.CLIENT ? Modget.NAMESPACE : Modget.NAMESPACE_SERVER,
                            RefreshCommand.COMMAND)));
            return;
        }

        StringBuilder errorMessageBuilder = new StringBuilder();
        List<SimpleEntry<String, SimpleEntry<ClickEvent, HoverEvent>>> messages = new ArrayList<>(15);

        List<Pair<ModUpdate, List<Exception>>> migratableMods = new ArrayList<>(15);

        for (InstalledModAdvanced mod : Modget.INSTANCE.getRecognizedMods()) {
            Pair<ModUpdate, List<Exception>> update;
            try {
                update = ModUpdateChecker.create().searchForModUpdate(mod, Modget.INSTANCE.REPO_MANAGER.getRepos(), minecraftVersion, "fabric");
            } catch (Exception e) {
                migratableMods.add(new MutablePair<>(null, Arrays.asList(e)));
                continue;
            }
            migratableMods.add(update);
        }

        for (Pair<ModUpdate, List<Exception>> migratableModExceptionPair : migratableMods) {
            for (Exception e : migratableModExceptionPair.getRight()) {
                errorMessageBuilder.append("\n" + e.getMessage());
            }
            if (migratableModExceptionPair.getLeft() == null) {
                continue;
            }

            ModUpdate modUpdate = migratableModExceptionPair.getLeft();
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
                    String.format("commands.%s.%s_title", Modget.NAMESPACE, COMMAND_PARTS[0])));
            for (SimpleEntry<String, SimpleEntry<ClickEvent, HoverEvent>> message : messages) {
                VersionAgnosticMessage.get().sendText(player, VersionAgnosticText.get().translatable(
                        message.getKey(),
                        message.getValue().getKey(),
                        message.getValue().getValue()));
            }
        }

        if (errorMessageBuilder.length() != 0) {
            VersionAgnosticMessage.get().sendError(player, VersionAgnosticText.get().translatable(
                    String.format("error.%s.while_searching_migratable_mods", Modget.NAMESPACE),
                    errorMessageBuilder.toString()));
        }
    }



    private class StartThread extends CommandBase.StartThread {
        String minecraftVersion;

        public StartThread(PlayerEntity player, String minecraftVersion) {
            super(player);
            this.minecraftVersion = minecraftVersion;
        }

        @Override
        public void run() {
            super.run();
            if (isRunning) {
                return;
            }

            isRunning = true;
            executeCommand(player, minecraftVersion);
            isRunning = false;
        }
    }
}
