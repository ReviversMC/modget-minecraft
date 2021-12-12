package com.github.reviversmc.modget.minecraft.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.reviversmc.modget.library.util.ModUpdateChecker;
import com.github.reviversmc.modget.manifests.spec4.api.data.manifest.common.NameUrlPair;
import com.github.reviversmc.modget.manifests.spec4.api.data.manifest.version.ModVersionVariant;
import com.github.reviversmc.modget.manifests.spec4.api.data.mod.InstalledMod;
import com.github.reviversmc.modget.manifests.spec4.impl.data.manifest.common.NameUrlPairImpl;
import com.github.reviversmc.modget.minecraft.util.Utils;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class UpdateManager {
    private List<Pair<ModVersionVariant, List<Exception>>> updates;
    private boolean searchedForUpdatesOnce = false;

    public UpdateManager() {
        updates = new ArrayList<>(15);
    }


    public void searchForUpdates(List<InstalledMod> installedMods) {
        updates.clear();

        for (InstalledMod mod : installedMods) {
            Pair<ModVersionVariant, List<Exception>> update;
            try {
                update = ModUpdateChecker.create().searchForModUpdate(mod, ModgetManager.REPO_MANAGER.getRepos(), Utils.create().getMinecraftVersion(), "fabric");
            } catch (Exception e) {
                updates.add(new MutablePair<>(null, Arrays.asList(e)));
                continue;
            }
            updates.add(update);
        }
        searchedForUpdatesOnce = true;
    }

    public NameUrlPair getPreferredDownloadPage(ModVersionVariant modVersionVariant) {
        NameUrlPair downloadNameUrlPair = null;
        if (modVersionVariant.getDownloadPageUrls().getModrinth() != null) {
            downloadNameUrlPair = new NameUrlPairImpl("Modrinth", modVersionVariant.getDownloadPageUrls().getModrinth());
        } else if (modVersionVariant.getDownloadPageUrls().getCurseforge() != null) {
            downloadNameUrlPair = new NameUrlPairImpl("CurseForge", modVersionVariant.getDownloadPageUrls().getCurseforge());
        } else if (modVersionVariant.getDownloadPageUrls().getSourceControl() != null) {
            downloadNameUrlPair = new NameUrlPairImpl("Source Control", modVersionVariant.getDownloadPageUrls().getSourceControl());
        } else if (modVersionVariant.getDownloadPageUrls().getOther() != null) {
            for (NameUrlPair nameUrlPair : modVersionVariant.getDownloadPageUrls().getOther()) {
                if (nameUrlPair.getUrl() != null) {
                    downloadNameUrlPair = new NameUrlPairImpl(nameUrlPair.getName(), nameUrlPair.getUrl());
                }
            }
        }
        return downloadNameUrlPair;
    }

    public List<Pair<ModVersionVariant,List<Exception>>> searchForUpdates() {
        if (searchedForUpdatesOnce == false) {
            searchForUpdates(ModgetManager.getRecognizedMods());
        }
        return updates;
    }

    public List<Pair<ModVersionVariant,List<Exception>>> searchForNotOptOutedUpdates() {
        // TODO
        return null;
    }

}
