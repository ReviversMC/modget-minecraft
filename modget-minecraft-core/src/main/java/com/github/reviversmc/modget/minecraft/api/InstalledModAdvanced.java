package com.github.reviversmc.modget.minecraft.api;

import com.github.reviversmc.modget.manifests.spec4.api.data.mod.InstalledMod;

public interface InstalledModAdvanced extends InstalledMod {
    public CustomModMetadata getCustomMetadata();
    public void setCustomMetadata(CustomModMetadata customModMetadata);
}
