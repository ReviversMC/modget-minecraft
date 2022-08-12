package com.github.reviversmc.modget.minecraft.command;

import java.util.Arrays;

import com.github.reviversmc.modget.library.exception.NoSuchRepoException;
import com.github.reviversmc.modget.manifests.spec4.api.data.ManifestRepository;
import com.github.reviversmc.modget.minecraft.Modget;
import com.github.reviversmc.modget.minecraft.compat.VersionAgnosticMessage;
import com.github.reviversmc.modget.minecraft.compat.VersionAgnosticText;
import com.github.reviversmc.modget.minecraft.compat.command.ArgumentTypeType;
import com.github.reviversmc.modget.minecraft.compat.command.VersionAgnosticClientCommandManager;
import com.github.reviversmc.modget.minecraft.compat.command.VersionAgnosticServerCommandManager;
import com.github.reviversmc.modget.minecraft.manager.ModgetManager;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import net.fabricmc.api.EnvType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;

public class ReposDisableCommand extends CommandBase {
    private static final String[] COMMAND_PARTS = {"repos", "disable", "repoId"};
    private static final int PERMISSION_LEVEL = 4;

    void registerServer() {
        VersionAgnosticServerCommandManager.get().register(
            CommandManager.literal(Modget.NAMESPACE_SERVER)
                .then(CommandManager.literal(COMMAND_PARTS[0])
                    .then(CommandManager.literal(COMMAND_PARTS[1])
                        .then(CommandManager.argument(COMMAND_PARTS[2], IntegerArgumentType.integer())
                            .requires(source -> source.hasPermissionLevel(PERMISSION_LEVEL))
                            .executes(context -> {
                                PlayerEntity player = context.getSource().getPlayer();

                                new StartThread(player, IntegerArgumentType.getInteger(context, COMMAND_PARTS[2])).start();
                                return 1;
                            })
                        )
                    )
                )
        );
    }

    void registerClient() {
        VersionAgnosticClientCommandManager.get().registerArgumentCommand(
            Arrays.asList(Modget.NAMESPACE, COMMAND_PARTS[0], COMMAND_PARTS[1], COMMAND_PARTS[2]),
            ArgumentTypeType.INTEGER,
            (player, repoId) -> {
                if (Modget.modPresentOnServer == true && player.hasPermissionLevel(PERMISSION_LEVEL)) {
                    VersionAgnosticMessage.get().sendInfo(player, VersionAgnosticText.get().translatable(
                            String.format("info.%s.use_for_server_mods", Modget.NAMESPACE),
                            String.format("/%s", Modget.NAMESPACE_SERVER)));
                }

                new StartThread(player, Integer.valueOf(repoId)).start();
            }
        );
    }



    public void executeCommand(PlayerEntity player, int repoId) throws NoSuchRepoException {
        ManifestRepository repo;

        try {
            repo = ModgetManager.REPO_MANAGER.getRepo(repoId);
        } catch (NoSuchRepoException e) {
            VersionAgnosticMessage.get().sendError(player, VersionAgnosticText.get().translatable(
                    String.format("error.%s.repo_not_found", Modget.NAMESPACE),
                    repoId,
                    ENVIRONMENT == EnvType.CLIENT ? Modget.NAMESPACE : Modget.NAMESPACE_SERVER));
            throw e;
        }

        if (ModgetManager.REPO_MANAGER.getRepo(repoId).isEnabled() == false) {
            VersionAgnosticMessage.get().sendWarning(player, VersionAgnosticText.get().translatable(
                    String.format("warning.%s.repo_already_disabled", Modget.NAMESPACE)));
            return;
        }

        repo.setEnabled(false);
        VersionAgnosticMessage.get().sendInfo(player, VersionAgnosticText.get().translatable(
                String.format("commands.%s.repo_disabled", Modget.NAMESPACE),
                repoId,
                String.format("/%s %s",
                        ENVIRONMENT == EnvType.CLIENT ? Modget.NAMESPACE : Modget.NAMESPACE_SERVER,
                        RefreshCommand.COMMAND)));
    }



    private class StartThread extends CommandBase.StartThread {
        int repoId;

        public StartThread(PlayerEntity player, int repoId) {
            super(player);
            this.repoId = repoId;
        }

        @Override
        public void run() {
            super.run();
            if (isRunning == true) {
                return;
            }

            isRunning = true;
            try {
                executeCommand(player, repoId);
            } catch (NoSuchRepoException e) {}
            isRunning = false;
        }
    }

}
