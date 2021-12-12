package com.github.reviversmc.modget.minecraft.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.electronwill.nightconfig.yaml.YamlWriter;
import com.github.reviversmc.modget.minecraft.Modget;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;

import net.fabricmc.loader.api.FabricLoader;

public class ModgetConfig {
    public static final ModgetConfig INSTANCE = new ModgetConfig();
    public final List<String> DEFAULT_REPOS = new ArrayList<>(
        Arrays.asList(
            "https://raw.githubusercontent.com/ReviversMC/modget-manifests"
            // ,
            // "https://raw.githubusercontent.com/thefirethirteen/modget-manifests"
        )
	);
	public final List<String> IGNORED_MODS = new ArrayList<>(
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
    private final File configFile;
    private final FileConfig config;


    public ModgetConfig() {
        configFile = new File(FabricLoader.getInstance().getConfigDir() + "/modget/config.yaml");
        try {
            configFile.createNewFile();
        } catch (IOException e) {
            Modget.logWarn("Couldn't load Modget config file! Your settings won't be saved", ExceptionUtils.getStackTrace(e));
        }
        config = FileConfig.of(configFile);
    }


    private boolean autoCheck = true;


    public void load() {
        config.load();
        autoCheck = config.get("autoCheck");
    }

    private void save() {
        config.set("autoCheck", autoCheck);

        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setPrettyFlow(true);
        dumperOptions.setDefaultFlowStyle(FlowStyle.BLOCK);
        YamlWriter writer = new YamlWriter(dumperOptions);
        writer.write(config, configFile, WritingMode.REPLACE);
    }


    public boolean getAutoCheck() {
        return autoCheck;
    }

    public void setAutoCheck(boolean autoCheck) {
        this.autoCheck = autoCheck;
        save();
    }

}
