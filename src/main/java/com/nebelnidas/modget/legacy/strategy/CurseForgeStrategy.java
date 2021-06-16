package com.nebelnidas.modget.legacy.strategy;

import com.mojang.bridge.game.GameVersion;
import com.nebelnidas.modget.Modget;
import com.nebelnidas.modget.api.ConfigObject;
import com.nebelnidas.modget.api.UpdateStrategy;
import com.nebelnidas.modget.legacy.data.ModUpdate;
import com.nebelnidas.modget.util.Util;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;

import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.VersionParsingException;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Arrays;

public class CurseForgeStrategy implements UpdateStrategy {
    @SuppressWarnings({"unused", "MismatchedReadAndWriteOfArray"})
    private static class CurseForgeFile {
        private String fileName;
        private String downloadUrl;
        private String[] gameVersion;
    }

    private final JsonAdapter<CurseForgeFile[]> jsonAdapter;

    public CurseForgeStrategy() {
        Moshi moshi = new Moshi.Builder().build();
        jsonAdapter = moshi.adapter(CurseForgeFile[].class);
    }

    @Override
    @Nullable
    public ModUpdate run(ConfigObject obj, String oldVersion, String name, String id) {
        int projectID;
        try {
            projectID = obj.getInt("projectID");
        } catch (ConfigObject.MissingValueException e) {
            Modget.logWarn(name, e.getMessage());
            return null;
        }

        String data;
        try {
            data = Util.urlToString("https://addons-ecs.forgesvc.net/api/v2/addon/" + projectID + "/files");
        } catch (IOException e) {
            Modget.logWarn(name, e.toString());
            return null;
        }

        CurseForgeFile[] files;
        try {
            files = jsonAdapter.fromJson(data);
        } catch (JsonDataException | IOException e) {
            Modget.logWarn(name, e.toString());
            return null;
        }

        if (files == null) {
            return null;
        }

        String versionStr;
        GameVersion version = Util.getMinecraftVersion();
        if (version.isStable()) {
            versionStr = version.getId();
        } else {
            versionStr = version.getReleaseTarget() + "-Snapshot";
        }

        boolean strict = isStrict(obj);

        CurseForgeFile newestFile = null;
        for (CurseForgeFile file : files) {
            if (Util.isFileCompatible(file.fileName)) {
                String fileVersion = Util.getVersionFromFileName(file.fileName);
                if ((Arrays.asList(file.gameVersion).contains(versionStr) && !strict) || Util.isVersionCompatible(id, fileVersion, strict)) {
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
