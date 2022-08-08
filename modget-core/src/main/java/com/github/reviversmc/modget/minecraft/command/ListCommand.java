package com.github.reviversmc.modget.minecraft.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.text.WordUtils;

import com.github.reviversmc.modget.minecraft.Modget;
import com.github.reviversmc.modget.minecraft.api.InstalledModAdvanced;
import com.github.reviversmc.modget.minecraft.compat.VersionAgnosticCommandManager;
import com.github.reviversmc.modget.minecraft.compat.VersionAgnosticMessage;
import com.github.reviversmc.modget.minecraft.compat.VersionAgnosticText;
import com.github.reviversmc.modget.minecraft.manager.ModgetManager;

import net.fabricmc.api.EnvType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;

public class ListCommand extends CommandBase {
    private static final String COMMAND = "list";
    private static final int PERMISSION_LEVEL = 3;

    void registerServer() {
        VersionAgnosticCommandManager.get().registerServerCommand(
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
        VersionAgnosticCommandManager.get().registerClientLiteralCommand(
            Arrays.asList(Modget.NAMESPACE, COMMAND),
            player -> {
                if (Modget.modPresentOnServer == true && player.hasPermissionLevel(PERMISSION_LEVEL)) {
                    VersionAgnosticMessage.get().sendInfo(player, VersionAgnosticText.get().translatable(
                            String.format("info.%s.use_for_server_mods", Modget.NAMESPACE),
                            String.format("/%s", Modget.NAMESPACE_SERVER)));
                }

                new StartThread(player).start();
            }
        );
    }



    public void executeCommand(PlayerEntity player) {
        if (ModgetManager.getInitializationError() == true) {
            VersionAgnosticMessage.get().sendWarning(player, VersionAgnosticText.get().translatable(
                    String.format("warning.%s.init_failed_try_running_refresh", Modget.NAMESPACE),
                String.format("/%s %s",
                        ENVIRONMENT == EnvType.CLIENT ? Modget.NAMESPACE : Modget.NAMESPACE_SERVER,
                        RefreshCommand.COMMAND)));
            return;
        }

        List<String> messages = new ArrayList<>();

        // Send start message
        VersionAgnosticMessage.get().sendHeadline(player, VersionAgnosticText.get().translatable(
                String.format("commands.%s.%s_title", Modget.NAMESPACE, COMMAND)));
        // Get mod names
        for (int i = 0; i < ModgetManager.getRecognizedMods().size(); i++) {
            InstalledModAdvanced mod = ModgetManager.getRecognizedMods().get(i);
            messages.add(String.format("%s %s", WordUtils.capitalize(mod.getId()), mod.getInstalledVersion()));
        }
        java.util.Collections.sort(messages);

        // Print mod names
        for (String message : messages) {
            VersionAgnosticMessage.get().sendText(player, VersionAgnosticText.get().literal(message));
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
