package com.thebrokenrail.modupdater;

import com.thebrokenrail.modupdater.command.ModUpdaterCommand;
import com.thebrokenrail.modupdater.data.ModUpdate;
import com.thebrokenrail.modupdater.strategy.util.UpdateStrategyRunner;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

public class ModUpdater implements ModInitializer {
    public static final String NAMESPACE = "modupdater";

    private static final String LOGGER_NAME = "ModUpdater";

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
        ModUpdaterCommand.register();
    }
}
