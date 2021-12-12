package com.github.reviversmc.modget.minecraft.api.impl;

import com.github.reviversmc.modget.minecraft.api.CustomModMetadata;

import net.fabricmc.loader.api.metadata.CustomValue;

public class CustomModMetadataImpl implements CustomModMetadata {
    private final CustomValue.CvObject metadata;

    public CustomModMetadataImpl(CustomValue.CvObject metadata) {
        this.metadata = metadata;
    }


    @Override
    public String getString(String str) throws MissingValueException {
        if (metadata.containsKey(str)) {
            try {
                return metadata.get(str).getAsString();
            } catch (ClassCastException e) {
                throw new MissingValueException(true, str);
            }
        } else {
            throw new MissingValueException(false, str);
        }
    }

    @Override
    public int getInt(String str) throws MissingValueException {
        if (metadata.containsKey(str)) {
            try {
                return metadata.get(str).getAsNumber().intValue();
            } catch (ClassCastException e) {
                throw new MissingValueException(true, str);
            }
        } else {
            throw new MissingValueException(false, str);
        }
    }

    @Override
    public boolean getBoolean(String str) throws MissingValueException {
        if (metadata.containsKey(str)) {
            try {
                return metadata.get(str).getAsBoolean();
            } catch (ClassCastException e) {
                throw new MissingValueException(true, str);
            }
        } else {
            throw new MissingValueException(false, str);
        }
    }

}
