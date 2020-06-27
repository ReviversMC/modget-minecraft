package com.thebrokenrail.modupdater.strategy.util;

import com.thebrokenrail.modupdater.ModUpdater;
import com.thebrokenrail.modupdater.api.ConfigObject;
import com.thebrokenrail.modupdater.api.impl.ConfigObjectCustom;
import com.thebrokenrail.modupdater.data.ModUpdate;
import com.thebrokenrail.modupdater.api.UpdateStrategy;
import com.thebrokenrail.modupdater.util.Util;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class UpdateStrategyRunner {
    @Nullable
    private static ModUpdate checkModForUpdate(ModMetadata metadata) {
        String name = metadata.getName() + " (" + metadata.getId() + ')';

        ConfigObject obj;
        if (metadata.containsCustomValue(ModUpdater.NAMESPACE)) {
            try {
                obj = new ConfigObjectCustom(metadata.getCustomValue(ModUpdater.NAMESPACE).getAsObject());
            } catch (ClassCastException e) {
                ModUpdater.logWarn(name, String.format("\"%s\" Is Not An Object", ModUpdater.NAMESPACE));
                return null;
            }
        } else {
            obj = Util.getHardcodedConfig(metadata.getId());
            if (obj == null) {
                return null;
            }
        }

        String oldVersion = metadata.getVersion().toString();

        String strategy;
        try {
            strategy = obj.getString("strategy");
        } catch (ConfigObject.MissingValueException e) {
            ModUpdater.logWarn(name, e.getMessage());
            return null;
        }

        UpdateStrategy strategyObj = UpdateStrategyRegistry.get(strategy);
        if (strategyObj == null) {
            ModUpdater.logWarn(name, "Invalid Strategy: " + name);
            return null;
        }

        return strategyObj.run(obj, oldVersion, name);
    }

    public static ModUpdate[] checkAllModsForUpdates() {
        ModUpdater.logInfo("Checking For Mod Updates...");

        List<ModUpdate> updates = new ArrayList<>();

        AtomicInteger remaining = new AtomicInteger(0);

        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            Thread thread = new Thread(() -> {
                ModMetadata metadata = mod.getMetadata();

                ModUpdate update = checkModForUpdate(metadata);

                if (update != null) {
                    ModUpdater.logInfo(update.text + " (" + update.downloadURL + ')');
                    synchronized (updates) {
                        updates.add(update);
                    }
                }

                synchronized (remaining) {
                    remaining.decrementAndGet();
                    remaining.notifyAll();
                }
            });
            synchronized (remaining) {
                remaining.incrementAndGet();
            }
            thread.start();
        }

        synchronized (remaining) {
            while (remaining.get() > 0) {
                try {
                    remaining.wait();
                } catch (InterruptedException ignored) {
                }
            }
        }

        ModUpdater.logInfo(updates.size() + String.format(" Mod Update%s Found", updates.size() == 1 ? "" : "s"));

        return updates.toArray(new ModUpdate[0]);
    }
}
