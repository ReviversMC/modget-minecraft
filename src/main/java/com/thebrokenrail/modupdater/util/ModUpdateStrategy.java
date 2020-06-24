package com.thebrokenrail.modupdater.util;

public interface ModUpdateStrategy {
    ModUpdate checkForUpdate(ConfigObject obj, String oldVersion, String name);
}
