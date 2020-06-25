package com.thebrokenrail.modupdater.data;

import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.util.version.VersionParsingException;

public class ModUpdate {
    public final String text;
    public final String downloadURL;

    private String toFriendlyString(String version) {
        try {
            return Version.parse(version).getFriendlyString();
        } catch (VersionParsingException e) {
            return version;
        }
    }

    public ModUpdate(String oldVersion, String newVersion, String downloadURL, String name) {
        this.text = name + ' ' + toFriendlyString(oldVersion) + " -> " + toFriendlyString(newVersion);
        this.downloadURL = downloadURL;
    }
}
