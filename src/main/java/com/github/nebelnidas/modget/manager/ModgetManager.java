package com.github.nebelnidas.modget.manager;

import java.net.UnknownHostException;
import java.util.ArrayList;

import com.github.nebelnidas.modget.config.ModgetConfig;
import com.github.nebelnidas.modget.util.Util;
import com.github.nebelnidas.modgetlib.data.RecognizedMod;
import com.github.nebelnidas.modgetlib.manager.ModgetLibManager;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

public class ModgetManager {
	private ArrayList<RecognizedMod> installedMods = new ArrayList<RecognizedMod>();
	public final ModgetLibManager MODGET_LIB_MANAGER = new ModgetLibManager();


	public void init() {
		scanMods();
		try {
			MODGET_LIB_MANAGER.init(Util.getMinecraftVersion().getId(), ModgetConfig.DEFAULT_REPOS, installedMods);
		} catch (Exception e) {}
	}

	public void reload() throws UnknownHostException, Exception {
		scanMods();
		MODGET_LIB_MANAGER.reload(installedMods);
	}

	public void scanMods() {
		installedMods.clear();

		for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
			if (ModgetConfig.IGNORED_MODS.contains(mod.getMetadata().getId())) {
				continue;
			} else {
				installedMods.add(new RecognizedMod() {{
					setId(mod.getMetadata().getId());
					setCurrentVersion(mod.getMetadata().getVersion().getFriendlyString());
				}});
			}
		}
	}
}