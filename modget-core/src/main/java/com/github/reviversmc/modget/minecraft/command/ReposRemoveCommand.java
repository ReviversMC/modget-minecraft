package com.github.reviversmc.modget.minecraft.command;

import java.util.Arrays;

import com.github.reviversmc.modget.library.exception.NoSuchRepoException;
import com.github.reviversmc.modget.minecraft.Modget;
import com.github.reviversmc.modget.minecraft.compat.VersionAgnosticMessage;
import com.github.reviversmc.modget.minecraft.compat.VersionAgnosticText;
import com.github.reviversmc.modget.minecraft.compat.command.ArgumentTypeType;
import com.github.reviversmc.modget.minecraft.compat.command.VersionAgnosticClientCommandManager;
import com.github.reviversmc.modget.minecraft.compat.command.VersionAgnosticServerCommandManager;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import net.fabricmc.api.EnvType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;

public class ReposRemoveCommand extends CommandBase {
    private static final String[] COMMAND_PARTS = {"repos", "remove", "repoId"};
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
                if (Modget.INSTANCE.isModPresentOnServer() && player.hasPermissionLevel(PERMISSION_LEVEL)) {
                    VersionAgnosticMessage.get().sendInfo(player, VersionAgnosticText.get().translatable(
                            String.format("info.%s.use_for_server_mods", Modget.NAMESPACE),
                            String.format("/%s", Modget.NAMESPACE_SERVER)));
                }

                new StartThread(player, Integer.valueOf(repoId)).start();
            }
        );
    }



    public void executeCommand(PlayerEntity player, int repoId) throws NoSuchRepoException {
        if (repoId == 0) {
            VersionAgnosticMessage.get().sendError(player, VersionAgnosticText.get().translatable(
                    String.format("error.%s.repo_not_removable", Modget.NAMESPACE),
                    ENVIRONMENT == EnvType.CLIENT ? Modget.NAMESPACE : Modget.NAMESPACE_SERVER));
            throw new NoSuchRepoException();
        }

        try {
            Modget.INSTANCE.REPO_MANAGER.removeRepo(repoId);
        } catch (NoSuchRepoException e) {
            VersionAgnosticMessage.get().sendError(player, VersionAgnosticText.get().translatable(
                    String.format("error.%s.repo_not_found", Modget.NAMESPACE),
                    repoId,
                    ENVIRONMENT == EnvType.CLIENT ? Modget.NAMESPACE : Modget.NAMESPACE_SERVER));
            throw e;
        }

        VersionAgnosticMessage.get().sendInfo(player, VersionAgnosticText.get().translatable(
                String.format("commands.%s.repo_removed", Modget.NAMESPACE),
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
            if (isRunning) {
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
