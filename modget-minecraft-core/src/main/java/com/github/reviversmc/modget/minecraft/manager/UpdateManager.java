package com.github.reviversmc.modget.minecraft.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.reviversmc.modget.library.data.ModUpdate;
import com.github.reviversmc.modget.library.util.ModUpdateChecker;
import com.github.reviversmc.modget.manifests.spec4.api.data.manifest.common.NameUrlPair;
import com.github.reviversmc.modget.manifests.spec4.api.data.manifest.version.ModVersionVariant;
import com.github.reviversmc.modget.manifests.spec4.impl.data.manifest.common.NameUrlPairImpl;
import com.github.reviversmc.modget.minecraft.api.CustomModMetadata.MissingValueException;
import com.github.reviversmc.modget.minecraft.api.InstalledModAdvanced;
import com.github.reviversmc.modget.minecraft.util.Utils;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class UpdateManager {
    private List<Pair<ModUpdate, List<Exception>>> updates;
    private boolean searchedForUpdatesOnce = false;

    public UpdateManager() {
        updates = new ArrayList<>(15);
    }


    public void searchForUpdates(List<InstalledModAdvanced> installedMods) {
        updates.clear();

        for (InstalledModAdvanced mod : installedMods) {
            Pair<ModUpdate, List<Exception>> update;
            try {
                update = ModUpdateChecker.create().searchForModUpdate(mod, ModgetManager.REPO_MANAGER.getRepos(), Utils.create().getMinecraftVersion(), "fabric");
            } catch (Exception e) {
                updates.add(new MutablePair<>(null, Arrays.asList(e)));
                continue;
            }
            updates.add(update);
        }
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

    public List<Pair<ModUpdate, List<Exception>>> searchForUpdates() {
        if (searchedForUpdatesOnce == false) {
            searchForUpdates(ModgetManager.getRecognizedMods());
            searchedForUpdatesOnce = true;
        }
        return updates;
    }

    public List<Pair<ModUpdate, List<Exception>>> searchForNotOptOutedUpdates() {
        List<InstalledModAdvanced> nonOptedOutMods = new ArrayList<>(10);

        for (InstalledModAdvanced mod : ModgetManager.getRecognizedMods()) {
            if (mod.getCustomMetadata() != null) {
                try {
                    if (mod.getCustomMetadata().getBoolean("noAutoCheck") == true) {
                        continue;
                    }
                } catch (MissingValueException e) {}
            }
            nonOptedOutMods.add(mod);
        }
        searchForUpdates(nonOptedOutMods);
        return updates;
    }

}
