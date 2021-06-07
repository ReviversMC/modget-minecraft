package com.nebelnidas.modget.api.impl;

import com.nebelnidas.modget.api.ConfigObject;

import net.fabricmc.loader.api.metadata.CustomValue;

public class ConfigObjectCustom implements ConfigObject {
    private final CustomValue.CvObject obj;

    public ConfigObjectCustom(CustomValue.CvObject obj) {
        this.obj = obj;
    }

    @Override
    public String getString(String str) throws MissingValueException {
        if (obj.containsKey(str)) {
            try {
                return obj.get(str).getAsString();
            } catch (ClassCastException e) {
                throw new MissingValueException(true, str);
            }
        } else {
            throw new MissingValueException(false, str);
        }
    }

    @Override
    public int getInt(String str) throws MissingValueException {
        if (obj.containsKey(str)) {
            try {
                return obj.get(str).getAsNumber().intValue();
            } catch (ClassCastException e) {
                throw new MissingValueException(true, str);
            }
        } else {
            throw new MissingValueException(false, str);
        }
    }

    @Override
    public boolean getBoolean(String str) throws MissingValueException {
        if (obj.containsKey(str)) {
            try {
                return obj.get(str).getAsBoolean();
            } catch (ClassCastException e) {
                throw new MissingValueException(true, str);
            }
        } else {
            throw new MissingValueException(false, str);
        }
    }
}