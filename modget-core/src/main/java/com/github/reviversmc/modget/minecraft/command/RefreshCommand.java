package com.github.reviversmc.modget.minecraft.command;

import java.net.UnknownHostException;
import java.util.Arrays;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.github.reviversmc.modget.minecraft.Modget;
import com.github.reviversmc.modget.minecraft.compat.VersionAgnosticMessage;
import com.github.reviversmc.modget.minecraft.compat.VersionAgnosticText;
import com.github.reviversmc.modget.minecraft.compat.command.VersionAgnosticClientCommandManager;
import com.github.reviversmc.modget.minecraft.compat.command.VersionAgnosticServerCommandManager;
import com.github.reviversmc.modget.minecraft.manager.ModgetManager;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;

public class RefreshCommand extends CommandBase {
    public static final String COMMAND = "refresh";
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
        // Send start message
        VersionAgnosticMessage.get().sendInfo(player, VersionAgnosticText.get().translatable(
                String.format("commands.%s.%s_start", Modget.NAMESPACE, COMMAND)));

        // Refresh everything
        try {
            ModgetManager.reload();
            ModgetManager.REPO_MANAGER.refresh();
            ModgetManager.UPDATE_MANAGER.reset();
        } catch (Exception e) {
            if (e instanceof UnknownHostException) {
                VersionAgnosticMessage.get().sendError(player, VersionAgnosticText.get().translatable(
                        String.format("error.%s.repo_connection_error", Modget.NAMESPACE),
                        e.getMessage()));
            } else {
                VersionAgnosticMessage.get().sendError(player, VersionAgnosticText.get().translatable(
                        String.format("error.%s.refresh_error", Modget.NAMESPACE),
                        e.getMessage()));
            }
            Modget.logWarn("An error occurred while refreshing Modget", ExceptionUtils.getStackTrace(e));
            isRunning = false;
            return;
        }

        // Send finish message
        VersionAgnosticMessage.get().sendInfo(player, VersionAgnosticText.get().translatable(
                String.format("commands.%s.%s_finish", Modget.NAMESPACE, COMMAND)));
    }



    public class StartThread extends CommandBase.StartThread {

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
