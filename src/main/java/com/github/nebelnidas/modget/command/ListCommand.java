package com.github.nebelnidas.modget.command;

import java.util.ArrayList;

import com.github.nebelnidas.modget.Modget;
import com.github.nebelnidas.modgetlib.data.RecognizedMod;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import org.apache.commons.text.WordUtils;

import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class ListCommand extends CommandBase {
    private static final String COMMAND = "list";
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



    private class StartThread extends CommandBase.StartThread {

        public StartThread(PlayerEntity player) {
            super(player);
        }

        @Override
        public void run() {
            super.run();

            isRunning = true;
            ArrayList<String> messages = new ArrayList<String>();

            // Send start message
            player.sendMessage(new TranslatableText(String.format("commands.%s.%s_title", Modget.NAMESPACE, COMMAND))
                .formatted(Formatting.YELLOW), false
            );

            // Get mod names
            for (int i = 0; i < MANAGER.getRecognizedMods().size(); i++) {
                RecognizedMod mod = MANAGER.getRecognizedMods().get(i);
                messages.add(String.format("%s %s", WordUtils.capitalize(mod.getId()), mod.getCurrentVersion()));
            }
            java.util.Collections.sort(messages);

            // Print mod names
            for (String message : messages) {
                player.sendMessage(new LiteralText(message), false);
            }

            isRunning = false;
        }
    }

}
