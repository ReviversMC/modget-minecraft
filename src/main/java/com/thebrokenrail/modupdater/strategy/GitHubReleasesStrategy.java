package com.thebrokenrail.modupdater.strategy;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;
import com.thebrokenrail.modupdater.ModUpdater;
import com.thebrokenrail.modupdater.util.ConfigObject;
import com.thebrokenrail.modupdater.util.ModUpdate;
import com.thebrokenrail.modupdater.util.ModUpdateStrategy;
import com.thebrokenrail.modupdater.util.Util;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.util.version.VersionParsingException;

import java.io.IOException;

public class GitHubReleasesStrategy implements ModUpdateStrategy {
    @SuppressWarnings({"unused", "MismatchedReadAndWriteOfArray"})
    private static class GitHubRelease {
        private GitHubReleaseAsset[] assets;
    }

    @SuppressWarnings("unused")
    private static class GitHubReleaseAsset {
        private String name;
        private String browser_download_url;
    }

    @Override
    public ModUpdate checkForUpdate(ConfigObject obj, String oldVersion, String name) {
        String owner;
        String repo;
        try {
            owner = obj.getString("owner");
            repo = obj.getString("repository");
        } catch (ConfigObject.MissingValueException e) {
            ModUpdater.invalidModUpdaterConfig(name);
            return null;
        }

        String data;
        try {
            data = Util.urlToString(String.format("https://api.github.com/repos/%s/%s/releases", owner, repo));
        } catch (IOException e) {
            ModUpdater.getLogger().warn("Unable To Access GitHub: " + name);
            return null;
        }

        GitHubRelease[] releases;
        try {
            Moshi moshi = new Moshi.Builder().build();
            JsonAdapter<GitHubRelease[]> jsonAdapter = moshi.adapter(GitHubRelease[].class);

            // GitHub's API never omits values, they're always null
            releases = jsonAdapter.nonNull().fromJson(data);
        } catch (JsonDataException | IOException e) {
            ModUpdater.getLogger().warn("GitHub Sent Invalid Data: ", e);
            return null;
        }

        if (releases == null) {
            return null;
        }

        GitHubReleaseAsset newestFile = null;
        for (GitHubRelease release : releases) {
            for (GitHubReleaseAsset asset : release.assets) {
                if (Util.isFileCompatible(asset.name)) {
                    String fileVersion = Util.getVersionFromFileName(asset.name);
                    if (Util.isVersionCompatible(fileVersion)) {
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
