package com.thebrokenrail.modupdater.strategy;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;
import com.thebrokenrail.modupdater.ModUpdater;
import com.thebrokenrail.modupdater.api.ConfigObject;
import com.thebrokenrail.modupdater.api.UpdateStrategy;
import com.thebrokenrail.modupdater.data.ModUpdate;
import com.thebrokenrail.modupdater.util.Util;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.VersionParsingException;

import javax.annotation.Nullable;
import java.io.IOException;

public class GitHubReleasesStrategy implements UpdateStrategy {
    @SuppressWarnings({"unused", "MismatchedReadAndWriteOfArray"})
    private static class GitHubRelease {
        private GitHubReleaseAsset[] assets;
    }

    @SuppressWarnings("unused")
    private static class GitHubReleaseAsset {
        private String name;
        private String browser_download_url;
    }

    private final JsonAdapter<GitHubRelease[]> jsonAdapter;

    public GitHubReleasesStrategy() {
        Moshi moshi = new Moshi.Builder().build();
        jsonAdapter = moshi.adapter(GitHubRelease[].class).nonNull();
    }

    @Override
    @Nullable
    public ModUpdate run(ConfigObject obj, String oldVersion, String name, String id) {
        String owner;
        String repo;
        try {
            owner = obj.getString("owner");
            repo = obj.getString("repository");
        } catch (ConfigObject.MissingValueException e) {
            ModUpdater.logWarn(name, e.getMessage());
            return null;
        }

        String data;
        try {
            data = Util.urlToString(String.format("https://api.github.com/repos/%s/%s/releases", owner, repo));
        } catch (IOException e) {
            ModUpdater.logWarn(name, e.toString());
            return null;
        }

        GitHubRelease[] releases;
        try {
            releases = jsonAdapter.fromJson(data);
        } catch (JsonDataException | IOException e) {
            ModUpdater.logWarn(name, e.toString());
            return null;
        }

        if (releases == null) {
            return null;
        }

        boolean strict = isStrict(obj);

        GitHubReleaseAsset newestFile = null;
        for (GitHubRelease release : releases) {
            for (GitHubReleaseAsset asset : release.assets) {
                if (Util.isFileCompatible(asset.name)) {
                    String fileVersion = Util.getVersionFromFileName(asset.name);
                    if (Util.isVersionCompatible(id, fileVersion, strict)) {
                        if (newestFile != null) {
                            try {
                                if (SemanticVersion.parse(fileVersion).compareTo(SemanticVersion.parse(fileVersion)) > 0) {
                                    newestFile = asset;
                                }
                            } catch (VersionParsingException ignored) {
                            }
                        } else {
                            newestFile = asset;
                        }
                    }
                }
            }
        }

        if (newestFile != null) {
            String newestFileVersion = Util.getVersionFromFileName(newestFile.name);
            try {
                if (SemanticVersion.parse(newestFileVersion).compareTo(SemanticVersion.parse(oldVersion)) > 0) {
                    return new ModUpdate(oldVersion, newestFileVersion, newestFile.browser_download_url, name);
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
