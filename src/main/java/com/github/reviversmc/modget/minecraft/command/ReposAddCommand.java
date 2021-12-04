package com.github.reviversmc.modget.minecraft.command;

import java.net.MalformedURLException;
import java.net.URL;

import com.github.reviversmc.modget.library.exception.RepoAlreadyExistsException;
import com.github.reviversmc.modget.minecraft.Modget;
import com.github.reviversmc.modget.minecraft.manager.ModgetManager;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class ReposAddCommand extends CommandBase {
    private static final String PARENT_COMMAND = "repos";
    private static final String COMMAND = "add";
    private static final int PERMISSION_LEVEL = 4;

    void registerServer() {
        CommandRegistrationCallback.EVENT.register((dispatcher, isDedicated) ->
            dispatcher.register(CommandManager.literal(Modget.NAMESPACE_SERVER)
                .then(CommandManager.literal(PARENT_COMMAND)
                    .then(CommandManager.literal(COMMAND)
                        .then(CommandManager.argument("repoURL", StringArgumentType.greedyString())
                            .requires(source -> source.hasPermissionLevel(PERMISSION_LEVEL))
                            .executes(context -> {
                                PlayerEntity player = context.getSource().getPlayer();

                                new StartThread(player, StringArgumentType.getString(context, "repoURL")).start();
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
                    .then(ClientCommandManager.argument("repoURL", StringArgumentType.greedyString()).executes(context -> {
                        PlayerEntity player = ClientPlayerHack.getPlayer(context);

                        if (Modget.modPresentOnServer == true && player.hasPermissionLevel(PERMISSION_LEVEL)) {
                            player.sendMessage(new TranslatableText("info." + Modget.NAMESPACE + ".use_for_server_mods", Modget.NAMESPACE_SERVER)
                                .setStyle(Style.EMPTY.withColor(Formatting.BLUE)), false
                            );
                        }

                        new StartThread(player, StringArgumentType.getString(context, "repoURL")).start();
                        return 1;
                    }))
                )
            )
        );
    }



    public void executeCommand(PlayerEntity player, String repoUri) throws RepoAlreadyExistsException {
        try {
            new URL(repoUri);
        } catch (MalformedURLException e) {
            player.sendMessage(new TranslatableText(String.format("error.%s.not_an_url", Modget.NAMESPACE))
                .formatted(Formatting.RED), false
            );
            return;
        }

        try {
            ModgetManager.REPO_MANAGER.addRepo(repoUri);
        } catch (RepoAlreadyExistsException e) {
            player.sendMessage(new TranslatableText(String.format("error.%s.repo_already_exists", Modget.NAMESPACE), e.getIdOfAlreadyExistingRepo())
                .formatted(Formatting.RED), false
            );
            throw e;
        }

        int repoId = ModgetManager.REPO_MANAGER.getRepos().get(
            ModgetManager.REPO_MANAGER.getRepos().size() - 1
        ).getId();

        player.sendMessage(new TranslatableText(String.format("commands.%s.repo_added", Modget.NAMESPACE), repoId), false);
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
            if (isRunning == true) {
                return;
            }

            isRunning = true;
            try {
                executeCommand(player, repoUri);
                new RefreshCommand().executeCommand(player);
            } catch (RepoAlreadyExistsException e) {}
            isRunning = false;
        }
    }

}
