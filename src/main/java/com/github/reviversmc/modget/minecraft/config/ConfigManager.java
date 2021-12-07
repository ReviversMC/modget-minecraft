package com.github.reviversmc.modget.minecraft.config;

import java.io.*;
import java.util.*;


class ConfigManager {
    public static ConfigManager INSTANCE = new ConfigManager();
    private Boolean loaded = false;
    private final Properties prop = new Properties();


    Boolean getBooleanProperty(String key) {
        if (!loaded) load();
        return java.lang.Boolean.parseBoolean(prop.getProperty(key));
    }

/*    fun getStringProperty(key: String?): String? {
        if (!loaded) load()
        return prop.getProperty(key)
    }

    fun getNumberProperty(key: String?): Int {
        if (!loaded) load()
        return prop.getProperty(key).toInt()
    }

    fun getDoubleProperty(key: String?): Double {
        if (!loaded) load()
        return prop.getProperty(key).toDouble()
    }*/

    private final File file = new File("./config/modget/config.properties");
    void setValue(String key, String value) throws IOException {
        prop.setProperty(key, value);
        FileOutputStream writer = new FileOutputStream(file);
        file.createNewFile();
        prop.store(writer, "ModGet Config");
        writer.close();
    }
    private void load() {
        loaded = true;
        try {
            new File("./config/modget").mkdir();
            if (file.exists()) {
                FileReader reader = new FileReader(file);
                prop.load(reader);
                reader.close();
            } else {
                FileOutputStream writer = new FileOutputStream(file);
                file.createNewFile();
                prop.setProperty("autoCheck", "true");
                prop.store(writer, "ModGet Config");
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
