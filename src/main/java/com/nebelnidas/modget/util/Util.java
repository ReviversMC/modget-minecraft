package com.nebelnidas.modget.util;

import com.mojang.bridge.game.GameVersion;
import com.nebelnidas.modget.Modget;
import com.nebelnidas.modget.api.ConfigObject;
import com.nebelnidas.modget.api.entrypoint.ModgetEntryPoint;
import com.nebelnidas.modget.api.impl.ConfigObjectHardcoded;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.MinecraftVersion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Util {
    public static String urlToString(String urlStr) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        URL url = new URL(urlStr);

        try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append('\n');
            }
        }

        return stringBuilder.toString();
    }

    public static final String JAR_EXTENSION = ".jar";

    public static String getVersionFromFileName(String fileName) {
        while (!Character.isDigit(fileName.charAt(0))) {
            int index = fileName.indexOf("-");
            fileName = fileName.substring(index != -1 ? index + 1 : 0);
        }
        if (fileName.endsWith(JAR_EXTENSION)) {
            fileName = fileName.substring(0, fileName.length() - JAR_EXTENSION.length());
        }
        return fileName;
    }

    private static String getMinecraftSemanticVersion() {
        Optional<ModContainer> mod = FabricLoader.getInstance().getModContainer("minecraft");
        if (mod.isPresent()) {
            return mod.get().getMetadata().getVersion().getFriendlyString();
        } else {
            return "";
        }
    }

    private static String minecraftVersionSemantic = null;
    private static GameVersion minecraftVersion = null;

    private static void updateMinecraftVersion() {
        if (minecraftVersionSemantic == null) {
            minecraftVersionSemantic = getMinecraftSemanticVersion();
        }
        if (minecraftVersion == null) {
            minecraftVersion = MinecraftVersion.create();
        }
    }

    private static String getMajorVersion() {
        updateMinecraftVersion();
        String[] parts = minecraftVersion.getReleaseTarget().split("\\.");
        if (parts.length > 1) {
            return String.format("%s.%s", parts[0], parts[1]);
        } else {
            return minecraftVersion.getId();
        }
    }

    private static boolean isVersionCompatible(String versionStr, char prefix, boolean strict) {
        updateMinecraftVersion();
        return versionStr.endsWith(prefix + minecraftVersionSemantic) || versionStr.endsWith(prefix + minecraftVersion.getId()) || (!strict && (versionStr.endsWith(prefix + minecraftVersion.getReleaseTarget()) || versionStr.endsWith(prefix + getMajorVersion())));
    }

    public static boolean isVersionCompatible(String id, String versionStr, boolean strict) {
        List<EntrypointContainer<ModgetEntryPoint>> list = FabricLoader.getInstance().getEntrypointContainers(Modget.NAMESPACE, ModgetEntryPoint.class);
        for (EntrypointContainer<ModgetEntryPoint> container : list) {
            if (container.getProvider().getMetadata().getId().equals(id)) {
                return container.getEntrypoint().isVersionCompatible(versionStr);
            }
        }

        return isVersionCompatible(versionStr, '+', strict) || isVersionCompatible(versionStr, '-', strict);
    }

    public static boolean isFileCompatible(String fileName) {
        return !fileName.endsWith("-dev" + JAR_EXTENSION) && !fileName.endsWith("-sources" + JAR_EXTENSION) && !fileName.endsWith("-sources-dev" + JAR_EXTENSION) && fileName.endsWith(JAR_EXTENSION);
    }

    public static GameVersion getMinecraftVersion() {
        updateMinecraftVersion();
        return minecraftVersion;
    }

    public static ConfigObject getHardcodedConfig(String modID) {
        switch (modID) {
            case "fabric": {
                Map<String, Object> map = new HashMap<>();
                map.put("strategy", "curseforge");
                map.put("projectID", 306612);
                map.put("strict", false);
                return new ConfigObjectHardcoded(map);
            }
            case "modmenu": {
                Map<String, Object> map = new HashMap<>();
                map.put("strategy", "curseforge");
                map.put("projectID", 308702);
                map.put("strict", false);
                return new ConfigObjectHardcoded(map);
            }
            default: {
                return null;
            }
        }
    }
}
