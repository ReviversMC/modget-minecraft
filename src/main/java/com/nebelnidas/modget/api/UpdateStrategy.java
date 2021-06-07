package com.nebelnidas.modget.api;

import javax.annotation.Nullable;

import com.nebelnidas.modget.data.ModUpdate;

public interface UpdateStrategy {
    @Nullable
    ModUpdate run(ConfigObject obj, String oldVersion, String name, String id);

    default boolean isStrict(ConfigObject obj) {
        try {
            return obj.getBoolean("strict");
        } catch (ConfigObject.MissingValueException e) {
            return true;
        }
    }
}
