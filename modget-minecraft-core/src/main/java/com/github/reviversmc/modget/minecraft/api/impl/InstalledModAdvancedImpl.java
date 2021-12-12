package com.github.reviversmc.modget.minecraft.api.impl;

import com.github.reviversmc.modget.manifests.spec4.impl.data.mod.InstalledModImpl;
import com.github.reviversmc.modget.minecraft.api.CustomModMetadata;
import com.github.reviversmc.modget.minecraft.api.InstalledModAdvanced;

public class InstalledModAdvancedImpl extends InstalledModImpl implements InstalledModAdvanced {
    private CustomModMetadata customMetadata;

    public InstalledModAdvancedImpl(String id) {
        super(id);
    }

    @Override
    public CustomModMetadata getCustomMetadata() {
        return customMetadata;
    }

    @Override
    public void setCustomMetadata(CustomModMetadata customModMetadata) {
        this.customMetadata = customModMetadata;
    }

}
