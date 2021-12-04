package com.github.reviversmc.modget.minecraft.command;

import com.github.reviversmc.modget.library.exception.NoSuchRepoException;
import com.github.reviversmc.modget.minecraft.Modget;
import com.github.reviversmc.modget.minecraft.manager.ModgetManager;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class ReposRemoveCommand extends CommandBase {
    private static final String PARENT_COMMAND = "repos";
    private static final String COMMAND = "remove";
    private static final int PERMISSION_LEVEL = 4;

    void registerServer() {
        CommandRegistrationCallback.EVENT.register((dispatcher, isDedicated) ->
            dispatcher.register(CommandManager.literal(Modget.NAMESPACE_SERVER)
                .then(CommandManager.literal(PARENT_COMMAND)
                    .then(CommandManager.literal(COMMAND)
                        .then(CommandManager.argument("repoID", IntegerArgumentType.integer())
                            .requires(source -> source.hasPermissionLevel(PERMISSION_LEVEL))
                            .executes(context -> {
                                PlayerEntity player = context.getSource().getPlayer();

                                new StartThread(player, IntegerArgumentType.getInteger(context, "repoID")).start();
                                return 1;
                            })
                        )
                    )
                )
            )
        );
    }

    void registerClient() {
        ClientCommandManager.DISPATCHER.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal(Modget.NAMESPACE)
            .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal(PARENT_COMMAND)
                .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal(COMMAND)
                    .then(ClientCommandManager.argument("repoID", IntegerArgumentType.integer()).executes(context -> {
                        PlayerEntity player = ClientPlayerHack.getPlayer(context);

                        if (Modget.modPresentOnServer == true && player.hasPermissionLevel(PERMISSION_LEVEL)) {
                            player.sendMessage(new TranslatableText("info." + Modget.NAMESPACE + ".use_for_server_mods", Modget.NAMESPACE_SERVER)
                                .setStyle(Style.EMPTY.withColor(Formatting.BLUE)), false
                            );
                        }

                        new StartThread(player, IntegerArgumentType.getInteger(context, "repoID")).start();
                        return 1;
                    }))
                )
            )
        );
    }



    public void executeCommand(PlayerEntity player, int repoId) throws NoSuchRepoException {
        if (repoId == 0) {
            player.sendMessage(new TranslatableText(String.format("error.%s.repo_not_removable", Modget.NAMESPACE),
                ENVIRONMENT == "CLIENT" ? Modget.NAMESPACE : Modget.NAMESPACE_SERVER
            ).formatted(Formatting.RED), false);
            throw new NoSuchRepoException();
        }

        try {
            ModgetManager.REPO_MANAGER.removeRepo(repoId);
        } catch (NoSuchRepoException e) {
            player.sendMessage(new TranslatableText(String.format("error.%s.repo_not_found", Modget.NAMESPACE),
                repoId, ENVIRONMENT == "CLIENT" ? Modget.NAMESPACE : Modget.NAMESPACE_SERVER
            ).formatted(Formatting.RED), false);
            throw e;
        }

        player.sendMessage(new TranslatableText(String.format("commands.%s.repo_removed", Modget.NAMESPACE), repoId), false);
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
                new RefreshCommand().executeCommand(player);
            } catch (NoSuchRepoException e) {}
            isRunning = false;
        }
    }

}
