package com.github.reviversmc.modget.minecraft.command;

import java.util.ArrayList;
import java.util.List;

import com.github.reviversmc.modget.manifests.spec4.api.data.ManifestRepository;
import com.github.reviversmc.modget.minecraft.Modget;
import com.github.reviversmc.modget.minecraft.manager.ModgetManager;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class ReposListCommand extends CommandBase {
    private static final String PARENT_COMMAND = "repos";
    private static final String COMMAND = "list";
    private static final int PERMISSION_LEVEL = 3;

    void registerServer() {
        CommandRegistrationCallback.EVENT.register((dispatcher, isDedicated) ->
            dispatcher.register(CommandManager.literal(Modget.NAMESPACE_SERVER)
                .then(CommandManager.literal(PARENT_COMMAND)
                    .then(CommandManager.literal(COMMAND)
                        .requires(source -> source.hasPermissionLevel(PERMISSION_LEVEL))
                        .executes(context -> {
                            PlayerEntity player = context.getSource().getPlayer();

                            new StartThread(player).start();
                            return 1;
                        })
                    )
                )
        ));
    }

    void registerClient() {
        ClientCommandManager.DISPATCHER.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal(Modget.NAMESPACE)
            .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal(PARENT_COMMAND)
                .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal(COMMAND).executes(context -> {
                    PlayerEntity player = ClientPlayerHack.getPlayer(context);

                    if (Modget.modPresentOnServer == true && player.hasPermissionLevel(PERMISSION_LEVEL)) {
                        player.sendMessage(new TranslatableText("info." + Modget.NAMESPACE + ".use_for_server_mods", "/modgetserver")
                            .setStyle(Style.EMPTY.withColor(Formatting.BLUE)), false
                        );
                    }

                    new StartThread(player).start();
                    return 1;
                }))
            )
        );
    }



    public void executeCommand(PlayerEntity player) {
        List<String> messages = new ArrayList<>();

        // Send start message
        player.sendMessage(new TranslatableText(String.format("commands.%s.%s_title", Modget.NAMESPACE, "repos_list"))
                .formatted(Formatting.YELLOW), false);

        // Get mod names
        for (int i = 0; i < ModgetManager.REPO_MANAGER.getRepos().size(); i++) {
            ManifestRepository repo = ModgetManager.REPO_MANAGER.getRepos().get(i);
            messages.add(String.format("%s: %s", Integer.toString(repo.getId()), repo.getUri()));
        }
        java.util.Collections.sort(messages);

        // Print mod names
        for (String message : messages) {
            player.sendMessage(new LiteralText(message), false);
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
