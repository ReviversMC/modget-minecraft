package com.github.reviversmc.modget.minecraft;


import com.github.reviversmc.modget.minecraft.command.ListCommand;
import com.github.reviversmc.modget.minecraft.command.RefreshCommand;
import com.github.reviversmc.modget.minecraft.command.ReposAddCommand;
import com.github.reviversmc.modget.minecraft.command.ReposDisableCommand;
import com.github.reviversmc.modget.minecraft.command.ReposEnableCommand;
import com.github.reviversmc.modget.minecraft.command.ReposListCommand;
import com.github.reviversmc.modget.minecraft.command.ReposRemoveCommand;
import com.github.reviversmc.modget.minecraft.command.SearchCommand;
import com.github.reviversmc.modget.minecraft.command.UpgradeCommand;
import com.github.reviversmc.modget.minecraft.manager.ModgetManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

public class Modget implements ModInitializer {
    public static final String NAMESPACE = "modget";
    public static final String NAMESPACE_SERVER = "modgetserver";
    public static final String LOGGER_NAME = "Modget";
    public static boolean modPresentOnServer = false;


    private static Logger getLogger() {
        return LogManager.getLogger(LOGGER_NAME);
    }

    public static void logWarn(String name) {
        getLogger().warn(name);
    }
    public static void logWarn(String name, String msg) {
        getLogger().warn(String.format("%s: %s", name, msg));
    }

    public static void logInfo(String info) {
        getLogger().info(info);
    }

    @Override
    public void onInitialize() {
        new Thread(() -> ModgetManager.init()).start();

        // Register Commands
        String env = FabricLoader.getInstance().getEnvironmentType().name(); // Returns client or server
        new ListCommand().register(env);
        new SearchCommand().register(env);
        new RefreshCommand().register(env);
        new UpgradeCommand().register(env);
        new ReposListCommand().register(env);
        new ReposAddCommand().register(env);
        new ReposEnableCommand().register(env);
        new ReposDisableCommand().register(env);
        new ReposRemoveCommand().register(env);

        // Check if the client sees this mod on a server
        Identifier identifier = new Identifier(NAMESPACE);
        ServerPlayNetworking.registerGlobalReceiver(identifier, (server, player, handler, buf, responseSender) -> { });

        try {
            ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
                modPresentOnServer = false;
                if (!client.isInSingleplayer()) {
                    modPresentOnServer = ClientPlayNetworking.canSend(identifier);
                }

            });

        } catch (Exception e) {}
    }
}
