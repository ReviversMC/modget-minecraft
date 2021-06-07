package com.nebelnidas.modget;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

import com.nebelnidas.modget.command.ModgetCommand;
import com.nebelnidas.modget.data.ModUpdate;
import com.nebelnidas.modget.strategy.util.UpdateStrategyRunner;

public class Modget implements ModInitializer {
    public static final String NAMESPACE = "modget";

    private static final String LOGGER_NAME = "Modget";

    private static Logger getLogger() {
        return LogManager.getLogger(LOGGER_NAME);
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
        findUpdates();
        ModgetCommand.register();
    }
}
