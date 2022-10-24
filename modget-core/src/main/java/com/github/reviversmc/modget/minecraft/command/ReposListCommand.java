package com.github.reviversmc.modget.minecraft.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.reviversmc.modget.manifests.spec4.api.data.ManifestRepository;
import com.github.reviversmc.modget.minecraft.Modget;
import com.github.reviversmc.modget.minecraft.compat.VersionAgnosticMessage;
import com.github.reviversmc.modget.minecraft.compat.VersionAgnosticText;
import com.github.reviversmc.modget.minecraft.compat.command.VersionAgnosticClientCommandManager;
import com.github.reviversmc.modget.minecraft.compat.command.VersionAgnosticServerCommandManager;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;

public class ReposListCommand extends CommandBase {
    private static final String[] COMMAND_PARTS = {"repos", "list"};
    private static final int PERMISSION_LEVEL = 3;

    void registerServer() {
        VersionAgnosticServerCommandManager.get().register(
            CommandManager.literal(Modget.NAMESPACE_SERVER)
                .then(CommandManager.literal(COMMAND_PARTS[0])
                    .then(CommandManager.literal(COMMAND_PARTS[1])
                        .requires(source -> source.hasPermissionLevel(PERMISSION_LEVEL))
                        .executes(context -> {
                            PlayerEntity player = context.getSource().getPlayer();

                            new StartThread(player).start();
                            return 1;
                        })
                    )
                )
        );
    }

    void registerClient() {
        VersionAgnosticClientCommandManager.get().registerLiteralCommand(
            Arrays.asList(Modget.NAMESPACE, COMMAND_PARTS[0], COMMAND_PARTS[1]),
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
        List<String> messages = new ArrayList<>();

        // Send start message
        VersionAgnosticMessage.get().sendHeadline(player, VersionAgnosticText.get().translatable(
                String.format("commands.%s.%s_title", Modget.NAMESPACE, String.join("_", COMMAND_PARTS))));

        // Get mod names
        for (int i = 0; i < Modget.INSTANCE.REPO_MANAGER.getRepos().size(); i++) {
            ManifestRepository repo = Modget.INSTANCE.REPO_MANAGER.getRepos().get(i);
            messages.add(String.format("%s: %s", Integer.toString(repo.getId()), repo.getUri()));
        }
        java.util.Collections.sort(messages);

        // Print mod names
        for (String message : messages) {
            VersionAgnosticMessage.get().sendText(player, VersionAgnosticText.get().translatable(message));
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
