package com.github.nebelnidas.modget.modget_minecraft.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Optional;

import com.mojang.bridge.game.GameVersion;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.MinecraftVersion;

public class Utils {
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

    public static boolean isFileCompatible(String fileName) {
        return !fileName.endsWith("-dev" + JAR_EXTENSION) && !fileName.endsWith("-sources" + JAR_EXTENSION) && !fileName.endsWith("-sources-dev" + JAR_EXTENSION) && fileName.endsWith(JAR_EXTENSION);
    }

    public static GameVersion getMinecraftVersion() {
        updateMinecraftVersion();
        return minecraftVersion;
    }
}
