package com.github.reviversmc.modget.minecraft.command;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import com.github.reviversmc.modget.library.exception.RepoAlreadyExistsException;
import com.github.reviversmc.modget.minecraft.Modget;
import com.github.reviversmc.modget.minecraft.compat.VersionAgnosticMessage;
import com.github.reviversmc.modget.minecraft.compat.VersionAgnosticText;
import com.github.reviversmc.modget.minecraft.compat.command.ArgumentTypeType;
import com.github.reviversmc.modget.minecraft.compat.command.VersionAgnosticClientCommandManager;
import com.github.reviversmc.modget.minecraft.compat.command.VersionAgnosticServerCommandManager;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.fabricmc.api.EnvType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;

public class ReposAddCommand extends CommandBase {
    private static final String[] COMMAND_PARTS = {"repos", "add", "repoUrl"};
    private static final int PERMISSION_LEVEL = 4;

    void registerServer() {
        VersionAgnosticServerCommandManager.get().register(
            CommandManager.literal(Modget.NAMESPACE_SERVER)
                .then(CommandManager.literal(COMMAND_PARTS[0])
                    .then(CommandManager.literal(COMMAND_PARTS[1])
                        .then(CommandManager.argument(COMMAND_PARTS[2], StringArgumentType.greedyString())
                            .requires(source -> source.hasPermissionLevel(PERMISSION_LEVEL))
                            .executes(context -> {
                                PlayerEntity player = context.getSource().getPlayer();

                                new StartThread(player, StringArgumentType.getString(context, COMMAND_PARTS[2])).start();
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
            ArgumentTypeType.STRING,
            (player, repoUrl) -> {
                if (Modget.INSTANCE.isModPresentOnServer() && player.hasPermissionLevel(PERMISSION_LEVEL)) {
                    VersionAgnosticMessage.get().sendInfo(player, VersionAgnosticText.get().translatable(
                            String.format("info.%s.use_for_server_mods", Modget.NAMESPACE),
                            String.format("/%s", Modget.NAMESPACE_SERVER)));
                }

                new StartThread(player, repoUrl).start();
            }
        );
    }


    public void executeCommand(PlayerEntity player, String repoUri) throws RepoAlreadyExistsException {
        try {
            new URL(repoUri);
        } catch (MalformedURLException e) {
            VersionAgnosticMessage.get().sendError(player, VersionAgnosticText.get().translatable(
                    String.format("error.%s.not_an_url", Modget.NAMESPACE)));
            return;
        }

        try {
            Modget.INSTANCE.REPO_MANAGER.addRepo(repoUri);
        } catch (RepoAlreadyExistsException e) {
            VersionAgnosticMessage.get().sendError(player, VersionAgnosticText.get().translatable(
                    String.format("error.%s.repo_already_exists", Modget.NAMESPACE),
                    e.getIdOfAlreadyExistingRepo()));
            throw e;
        }

        int repoId = Modget.INSTANCE.REPO_MANAGER.getRepos()
                .get(Modget.INSTANCE.REPO_MANAGER.getRepos().size() - 1)
                .getId();

        VersionAgnosticMessage.get().sendInfo(player, VersionAgnosticText.get().translatable(
                String.format("commands.%s.repo_added", Modget.NAMESPACE),
                repoId,
                String.format("/%s %s",
                        ENVIRONMENT == EnvType.CLIENT ? Modget.NAMESPACE : Modget.NAMESPACE_SERVER,
                        RefreshCommand.COMMAND)));
    }



    private class StartThread extends CommandBase.StartThread {
        String repoUri;

        public StartThread(PlayerEntity player, String repoUri) {
            super(player);
            this.repoUri = repoUri;
        }

        @Override
        public void run() {
            super.run();
            if (isRunning) {
                return;
            }

            isRunning = true;
            try {
                executeCommand(player, repoUri);
            } catch (RepoAlreadyExistsException e) {}
            isRunning = false;
        }
    }

}
