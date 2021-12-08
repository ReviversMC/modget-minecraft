package com.github.reviversmc.modget.minecraft.command;

import java.net.UnknownHostException;

import com.github.reviversmc.modget.minecraft.Modget;
import com.github.reviversmc.modget.minecraft.manager.ModgetManager;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import org.apache.commons.lang3.exception.ExceptionUtils;

import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class RefreshCommand extends CommandBase {
    private static final String COMMAND = "refresh";
    private static final int PERMISSION_LEVEL = 3;

    void registerServer() {
        CommandRegistrationCallback.EVENT.register((dispatcher, isDedicated) ->
            dispatcher.register(CommandManager.literal(Modget.NAMESPACE_SERVER)
                .then(CommandManager.literal(COMMAND)
                    .requires(source -> source.hasPermissionLevel(PERMISSION_LEVEL))
                    .executes(context -> {
                        PlayerEntity player = context.getSource().getPlayer();

                        new StartThread(player).start();
                        return 1;
                    })
                )
        ));
    }

    void registerClient() {
        ClientCommandManager.DISPATCHER.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal(Modget.NAMESPACE)
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
        );
    }



    public void executeCommand(PlayerEntity player) {
        // Send start message
        player.sendMessage(new TranslatableText(String.format("commands.%s.%s_start", Modget.NAMESPACE, COMMAND)), false);

        // Refresh everything
        try {
            ModgetManager.reload(true);
        } catch (Exception e) {
            if (e instanceof UnknownHostException) {
                player.sendMessage(new TranslatableText("error." + Modget.NAMESPACE + ".repo_connection_error", e.getMessage())
                    .formatted(Formatting.RED), false
                );
            } else {
                player.sendMessage(new TranslatableText("error." + Modget.NAMESPACE + ".refresh_error", e.getMessage())
                    .formatted(Formatting.RED), false
                );
            }
            Modget.logWarn("An error occurred while refreshing Modget", ExceptionUtils.getStackTrace(e));
            isRunning = false;
            return;
        }

        // Send finish message
        player.sendMessage(new TranslatableText(String.format("commands.%s.%s_finish", Modget.NAMESPACE, COMMAND)), false);
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
