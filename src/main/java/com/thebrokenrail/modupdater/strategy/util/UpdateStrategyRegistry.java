package com.thebrokenrail.modupdater.strategy.util;

import com.thebrokenrail.modupdater.api.UpdateStrategy;
import com.thebrokenrail.modupdater.strategy.CurseForgeStrategy;
import com.thebrokenrail.modupdater.strategy.GitHubReleasesStrategy;
import com.thebrokenrail.modupdater.strategy.JSONStrategy;
import com.thebrokenrail.modupdater.strategy.MavenStrategy;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class UpdateStrategyRegistry {
    private static final Map<String, UpdateStrategy> data = new HashMap<>();

    @Nullable
    static UpdateStrategy get(String name) {
        return data.get(name);
    }

    static {
        data.put("curseforge", new CurseForgeStrategy());
        data.put("maven", new MavenStrategy());
        data.put("github", new GitHubReleasesStrategy());
        data.put("json", new JSONStrategy());
    }
}
