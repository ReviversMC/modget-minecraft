package com.thebrokenrail.modupdater;

import com.thebrokenrail.modupdater.strategy.ModUpdateStrategies;
import com.thebrokenrail.modupdater.util.ModUpdate;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class ModUpdater implements ModInitializer {
    public static final String NAMESPACE = "modupdater";

    private static final String LOGGER_NAME = "ModUpdater";

    public static Logger getLogger() {
        return LogManager.getLogger(LOGGER_NAME);
    }

    public static void invalidModUpdaterConfig(String modID) {
        getLogger().warn("Invalid JSON Configuration: " + modID);
    }

    private static volatile ModUpdate[] updates;

    private static Thread updateThread;

    public static ModUpdate[] getUpdates() {
        if (updates == null) {
            if (Thread.currentThread() == updateThread) {
                updates = ModUpdateStrategies.findAvailableUpdates();
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
