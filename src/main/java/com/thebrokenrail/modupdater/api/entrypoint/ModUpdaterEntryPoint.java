package com.thebrokenrail.modupdater.api.entrypoint;

public interface ModUpdaterEntryPoint {
    boolean isVersionCompatible(String version);
}
