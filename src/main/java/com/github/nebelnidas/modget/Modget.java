package com.github.nebelnidas.modget;

import com.github.nebelnidas.modget.command.ModgetCommand;
import com.github.nebelnidas.modget.manager.ModgetManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ModInitializer;

public class Modget implements ModInitializer {
    public static final String NAMESPACE = "modget";
    public static final String LOGGER_NAME = "Modget";
    public static final ModgetManager MODGET_MANAGER = new ModgetManager();

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
        new Thread(() -> MODGET_MANAGER.init()).start();
        ModgetCommand.register();
    }
}
