package com.github.reviversmc.modget.minecraft.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class ModgetConfig {
    public static final List<String> DEFAULT_REPOS = new ArrayList<>(
        Arrays.asList(
            "https://raw.githubusercontent.com/ReviversMC/modget-manifests"
            // ,
            // "https://raw.githubusercontent.com/thefirethirteen/modget-manifests"
        )
	);
	public static final List<String> IGNORED_MODS = new ArrayList<>(
		Arrays.asList(
			"minecraft",
            "toml4j",
			"crowdin-translate",
			"fabric-api-base",
            "fabric-api-lookup-api-v1",
            "fabric-biome-api-v1",
            "fabric-blockrenderlayer-v1",
            "fabric-command-api-v1",
            "fabric-commands-v0",
            "fabric-containers-v0",
            "fabric-content-registries-v0",
            "fabric-crash-report-info-v1",
            "fabric-dimensions-v1",
            "fabric-entity-events-v1",
            "fabric-events-interaction-v0",
            "fabric-events-lifecycle-v0",
            "fabric-game-rule-api-v1",
            "fabric-gametest-api-v1",
            "fabric-item-api-v1",
            "fabric-item-groups-v0",
            "fabric-key-binding-api-v1",
            "fabric-keybindings-v0",
            "fabric-lifecycle-events-v1",
            "fabric-loot-tables-v1",
            "fabric-mining-levels-v0",
            "fabric-models-v0",
            "fabric-networking-api-v1",
            "fabric-networking-blockentity-v0",
            "fabric-networking-v0",
            "fabric-object-builder-api-v1",
            "fabric-object-builders-v0",
            "fabric-particles-v1",
            "fabric-registry-sync-v0",
            "fabric-renderer-api-v1",
            "fabric-renderer-indigo",
            "fabric-renderer-registries-v1",
            "fabric-rendering-data-attachment-v1",
            "fabric-rendering-fluids-v1",
            "fabric-rendering-v0",
            "fabric-rendering-v1",
            "fabric-resource-loader-v0",
            "fabric-screen-api-v1",
            "fabric-screen-handler-api-v1",
            "fabric-structure-api-v1",
            "fabric-tag-extensions-v0",
            "fabric-textures-v0",
            "fabric-tool-attribute-api-v1",
            "fabric-transfer-api-v1"
		)
	);
    public static final ModgetConfig INSTANCE = new ModgetConfig();

    private final File file = new File("./config/modget/config.properties");
    private final Properties properties = new Properties();
    private Boolean loaded = false;


    // Property getters
    public Boolean getBooleanProperty(String key) {
        load();
        return java.lang.Boolean.parseBoolean(properties.getProperty(key));
    }

    public int getStringProperty(String key) {
        load();
        return Integer.valueOf(properties.getProperty(key));
    }


    // Write values to disk
    public void setValue(String key, String value) throws IOException {
        properties.setProperty(key, value);
        FileOutputStream writer = new FileOutputStream(file);
        file.createNewFile();
        properties.store(writer, "Modget Config");
        writer.close();
    }


    // Load values from disk
    public void reload() {
        loaded = false;
        load();
    }

    private void load() {
        if (loaded == true) {
            return;
        }

        try {
            new File("./config/modget").mkdir();
            if (file.exists()) {
                FileReader reader = new FileReader(file);
                properties.load(reader);
                reader.close();
            } else {
                FileOutputStream writer = new FileOutputStream(file);
                file.createNewFile();
                properties.setProperty("autoCheck", "true");
                properties.setProperty("autoCheckRequestingMods", "true");
                properties.store(writer, "Modget Config");
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
