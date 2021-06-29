package com.nebelnidas.modget;

import javax.annotation.Nullable;

import com.nebelnidas.modget.command.ModgetCommand;
import com.nebelnidas.modget.legacy.data.ModUpdate;
import com.nebelnidas.modget.legacy.strategy.util.UpdateStrategyRunner;
import com.nebelnidas.modget.manager.MainManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ModInitializer;

public class Modget implements ModInitializer {
    public static final String NAMESPACE = "modget";
    public static final String LOGGER_NAME = "Modget";
    public static MainManager MAIN_MANAGER = new MainManager();

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

    private static volatile ModUpdate[] updates = null;

    public static void findUpdates() {
        updates = null;
        new Thread(() -> updates = UpdateStrategyRunner.checkAllModsForUpdates()).start();
    }

    @Nullable
    public static ModUpdate[] getUpdates() {
        return updates;
    }

    @Override
    public void onInitialize() {
        new Thread(() -> MAIN_MANAGER.reload()).start();
        new Thread(() -> MAIN_MANAGER.findUpdates()).start();
        findUpdates();
        ModgetCommand.register();
    }
}
