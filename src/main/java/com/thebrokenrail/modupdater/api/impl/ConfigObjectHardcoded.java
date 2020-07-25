package com.thebrokenrail.modupdater.api.impl;

import com.thebrokenrail.modupdater.api.ConfigObject;

import java.util.Map;

public class ConfigObjectHardcoded implements ConfigObject {
    private final Map<String, Object> map;

    public ConfigObjectHardcoded(Map<String, Object> map) {
        this.map = map;
    }

    @Override
    public String getString(String str) throws MissingValueException {
        if (map.containsKey(str)) {
            try {
                return (String) map.get(str);
            } catch (ClassCastException e) {
                throw new MissingValueException(true, str);
            }
        } else {
            throw new MissingValueException(false, str);
        }
    }

    @Override
    public int getInt(String str) throws MissingValueException {
        if (map.containsKey(str)) {
            try {
                return (Integer) map.get(str);
            } catch (ClassCastException e) {
                throw new MissingValueException(true, str);
            }
        } else {
            throw new MissingValueException(false, str);
        }
    }

    @Override
    public boolean getBoolean(String str) throws MissingValueException {
        if (map.containsKey(str)) {
            try {
                return (Boolean) map.get(str);
            } catch (ClassCastException e) {
                throw new MissingValueException(true, str);
            }
        } else {
            throw new MissingValueException(false, str);
        }
    }
}
