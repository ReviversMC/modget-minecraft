package com.nebelnidas.modget.strategy.util;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;

import javax.annotation.Nullable;

import com.nebelnidas.modget.Modget;
import com.nebelnidas.modget.api.ConfigObject;
import com.nebelnidas.modget.api.UpdateStrategy;
import com.nebelnidas.modget.api.impl.ConfigObjectCustom;
import com.nebelnidas.modget.data.ModUpdate;
import com.nebelnidas.modget.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class UpdateStrategyRunner {
    @Nullable
    private static ModUpdate checkModForUpdate(ModMetadata metadata, Consumer<String> scan) {
        String name = metadata.getName() + " (" + metadata.getId() + ')';

        ConfigObject obj;
        if (metadata.containsCustomValue(Modget.NAMESPACE)) {
            try {
                obj = new ConfigObjectCustom(metadata.getCustomValue(Modget.NAMESPACE).getAsObject());
            } catch (ClassCastException e) {
                Modget.logWarn(name, String.format("\"%s\" Is Not An Object", Modget.NAMESPACE));
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
            Modget.logWarn(name, e.getMessage());
            return null;
        }

        UpdateStrategy strategyObj = UpdateStrategyRegistry.get(strategy);
        if (strategyObj == null) {
            Modget.logWarn(name, "Invalid Strategy: " + name);
            return null;
        }

        scan.accept(name);

        return strategyObj.run(obj, oldVersion, name, metadata.getId());
    }

    public static ModUpdate[] checkAllModsForUpdates() {
        Modget.logInfo("Checking For Mod Updates...");

        List<ModUpdate> updates = new ArrayList<>();
        List<String> scannedMods = new ArrayList<>();

        AtomicInteger remaining = new AtomicInteger(0);

        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            Thread thread = new Thread(() -> {
                try {
                    ModMetadata metadata = mod.getMetadata();

                    ModUpdate update = checkModForUpdate(metadata, name -> {
                        synchronized (scannedMods) {
                            scannedMods.add(name);
                        }
                    });

                    if (update != null) {
                        Modget.logInfo(update.text + " (" + update.downloadURL + ')');
                        synchronized (updates) {
                            updates.add(update);
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                } finally {
                    synchronized (remaining) {
                        remaining.decrementAndGet();
                        remaining.notifyAll();
                    }
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

        Modget.logInfo(updates.size() + String.format(" Mod Update%s Found", updates.size() == 1 ? "" : "s"));

        Modget.logInfo("Scanned " + scannedMods.size() + " Mods: " + String.join(", ", scannedMods));

        return updates.toArray(new ModUpdate[0]);
    }
}
