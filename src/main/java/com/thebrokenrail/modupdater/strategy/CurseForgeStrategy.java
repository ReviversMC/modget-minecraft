package com.thebrokenrail.modupdater.strategy;

import com.mojang.bridge.game.GameVersion;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.thebrokenrail.modupdater.ModUpdater;
import com.thebrokenrail.modupdater.util.ConfigObject;
import com.thebrokenrail.modupdater.util.ModUpdate;
import com.thebrokenrail.modupdater.util.ModUpdateStrategy;
import com.thebrokenrail.modupdater.util.Util;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.util.version.VersionParsingException;

import java.io.IOException;
import java.util.Arrays;

class CurseForgeStrategy implements ModUpdateStrategy {
    @SuppressWarnings({"unused", "MismatchedReadAndWriteOfArray"})
    private static class CurseForgeFile {
        private String fileName;
        private String downloadUrl;
        private String[] gameVersion;
    }

    @Override
    public ModUpdate checkForUpdate(ConfigObject obj, String oldVersion, String name) {
        int projectID;
        try {
            projectID = obj.getInt("projectID");
        } catch (ConfigObject.MissingValueException e) {
            ModUpdater.invalidModUpdaterConfig(name);
            return null;
        }

        String data = Util.urlToString("https://addons-ecs.forgesvc.net/api/v2/addon/" + projectID + "/files");

        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<CurseForgeFile[]> jsonAdapter = moshi.adapter(CurseForgeFile[].class);

        CurseForgeFile[] files;
        try {
            files = jsonAdapter.fromJson(data);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        if (files == null) {
            return null;
        }

        String versionStr;
        GameVersion version = Util.getMinecraftVersion();
        if (version.isStable()) {
            versionStr = version.getName();
        } else {
            versionStr = version.getReleaseTarget() + "-Snapshot";
        }

        CurseForgeFile newestFile = null;
        for (CurseForgeFile file : files) {
            String fileVersion = Util.getVersionFromFileName(file.fileName);
            if (Arrays.asList(file.gameVersion).contains(versionStr) || Util.isVersionCompatible(fileVersion)) {
                if (newestFile != null) {
                    String newestFileVersion = Util.getVersionFromFileName(newestFile.fileName);
                    try {
                        if (SemanticVersion.parse(fileVersion).compareTo(SemanticVersion.parse(newestFileVersion)) > 0) {
                            newestFile = file;
                        }
                    } catch (VersionParsingException ignored) {
                    }
                } else {
                    newestFile = file;
                }
            }
        }

        if (newestFile != null) {
            String newestFileVersion = Util.getVersionFromFileName(newestFile.fileName);
            try {
                if (SemanticVersion.parse(newestFileVersion).compareTo(SemanticVersion.parse(oldVersion)) > 0) {
                    return new ModUpdate(oldVersion, newestFileVersion, newestFile.downloadUrl, name);
                } else {
                    return null;
                }
            } catch (VersionParsingException e) {
                return null;
            }
        } else {
            return null;
        }
    }
}
