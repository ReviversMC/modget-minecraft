package com.thebrokenrail.modupdater.strategy;

import com.thebrokenrail.modupdater.ModUpdater;
import com.thebrokenrail.modupdater.util.ConfigObject;
import com.thebrokenrail.modupdater.util.HardcodedData;
import com.thebrokenrail.modupdater.util.ModUpdate;
import com.thebrokenrail.modupdater.util.ModUpdateStrategy;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ModUpdateStrategies {
    private static final Map<String, ModUpdateStrategy> data = new HashMap<>();

    private static ModUpdate checkForUpdate(ModMetadata metadata, ConfigObject obj, String name) {
        String oldVersion = metadata.getVersion().toString();

        String strategy;
        try {
            strategy = obj.getString("strategy");
        } catch (ConfigObject.MissingValueException e) {
            ModUpdater.invalidModUpdaterConfig(name);
            return null;
        }

        ModUpdateStrategy strategyObj = data.get(strategy);
        if (strategyObj == null) {
            ModUpdater.invalidModUpdaterConfig(name);
            return null;
        }

        return strategyObj.checkForUpdate(obj, oldVersion, name);
    }

    public static ModUpdate[] findAvailableUpdates() {
        List<ModUpdate> updates = new ArrayList<>();

        AtomicInteger remaining = new AtomicInteger(0);

        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            Thread thread = new Thread(() -> {
                ModMetadata metadata = mod.getMetadata();
                String name = metadata.getName() + " (" + metadata.getId() + ')';

                ModUpdate update = null;
                if (metadata.containsCustomValue(ModUpdater.NAMESPACE)) {
                    try {
                        update = checkForUpdate(metadata, new ConfigObject.ConfigObjectCustom(metadata.getCustomValue(ModUpdater.NAMESPACE).getAsObject()), name);
                    } catch (ClassCastException e) {
                        ModUpdater.invalidModUpdaterConfig(name);
                    }
                } else {
                    ConfigObject obj = HardcodedData.getData(metadata.getId());
                    if (obj != null) {
                        update = checkForUpdate(metadata, obj, name);
                    }
                }

                if (update != null) {
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

        return updates.toArray(new ModUpdate[0]);
    }

    static {
        data.put("curseforge", new CurseForgeStrategy());
        data.put("maven", new MavenStrategy());
    }
}
