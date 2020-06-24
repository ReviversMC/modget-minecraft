package com.thebrokenrail.modupdater.util;

import net.fabricmc.loader.api.metadata.CustomValue;

import java.util.Map;

public interface ConfigObject {
    String getString(String str) throws MissingValueException;
    int getInt(String str) throws MissingValueException;

    class ConfigObjectCustom implements ConfigObject {
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
                    throw new MissingValueException();
                }
            } else {
                throw new MissingValueException();
            }
        }

        @Override
        public int getInt(String str) throws MissingValueException {
            if (obj.containsKey(str)) {
                try {
                    return obj.get(str).getAsNumber().intValue();
                } catch (ClassCastException e) {
                    throw new MissingValueException();
                }
            } else {
                throw new MissingValueException();
            }
        }
    }

    class ConfigObjectHardcoded implements ConfigObject {
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
                    throw new MissingValueException();
                }
            } else {
                throw new MissingValueException();
            }
        }

        @Override
        public int getInt(String str) throws MissingValueException {
            if (map.containsKey(str)) {
                try {
                    return (Integer) map.get(str);
                } catch (ClassCastException e) {
                    throw new MissingValueException();
                }
            } else {
                throw new MissingValueException();
            }
        }
    }

    class MissingValueException extends Exception {
    }
}
