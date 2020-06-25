package com.thebrokenrail.modupdater;

import com.thebrokenrail.modupdater.strategy.util.UpdateStrategyRunner;
import com.thebrokenrail.modupdater.data.ModUpdate;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class ModUpdater implements ModInitializer {
    public static final String NAMESPACE = "modupdater";

    private static final String LOGGER_NAME = "ModUpdater";

    private static Logger getLogger() {
        return LogManager.getLogger(LOGGER_NAME);
    }

    public static void log(String name, String msg) {
        getLogger().warn(String.format("%s: %s", name, msg));
    }

    private static volatile ModUpdate[] updates;

    private static Thread updateThread;

    public static ModUpdate[] getUpdates() {
        if (updates == null) {
            if (Thread.currentThread() == updateThread) {
                updates = UpdateStrategyRunner.checkAllModsForUpdates();
            } else {
                return null;
            }
        }
        return updates;
    }

    @Override
    public void onInitialize() {
        updateThread = new Thread(() -> {
            getLogger().info("Checking For Mod Updates...");
            for (ModUpdate update : Objects.requireNonNull(getUpdates())) {
                getLogger().info(update.text + " (" + update.downloadURL + ')');
            }
            getLogger().info(updates.length + " Mod Update(s) Found");
        });
        updateThread.start();
    }
}
