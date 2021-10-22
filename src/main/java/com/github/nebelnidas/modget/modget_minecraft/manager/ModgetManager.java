package com.github.nebelnidas.modget.modget_minecraft.manager;

import java.util.ArrayList;
import java.util.List;

import com.github.nebelnidas.modget.manifest_api.api.v0.def.data.RecognizedMod;
import com.github.nebelnidas.modget.manifest_api.api.v0.impl.data.RecognizedModImpl;
import com.github.nebelnidas.modget.modget_lib.api.def.RepoManager;
import com.github.nebelnidas.modget.modget_lib.api.impl.ModgetLibUtilsImpl;
import com.github.nebelnidas.modget.modget_lib.api.impl.RepoManagerImpl;
import com.github.nebelnidas.modget.modget_minecraft.Modget;
import com.github.nebelnidas.modget.modget_minecraft.config.ModgetConfig;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

public class ModgetManager {
	public final RepoManager REPO_MANAGER = new RepoManagerImpl();
	private volatile List<RecognizedMod> installedMods = new ArrayList<>();
	private volatile List<RecognizedMod> recognizedMods = new ArrayList<>();
	private volatile boolean initializationError = false;


	public void init() {
		scanMods();
		try {
			reload();
		} catch (Exception e) {
			initializationError = true;
			Modget.logWarn("An error occurred while initializing Modget", e.getMessage());
		}
	}

	public void reload() throws Exception {
		try {
			REPO_MANAGER.reload(ModgetConfig.DEFAULT_REPOS);
			REPO_MANAGER.initRepos();
			recognizedMods = ModgetLibUtilsImpl.create().scanMods(installedMods, ModgetConfig.IGNORED_MODS, REPO_MANAGER.getRepos());
			initializationError = false;
		} catch (Exception e) {
			throw e;
		}
	}


	public void scanMods() {
		installedMods.clear();

		for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
			if (ModgetConfig.IGNORED_MODS.contains(mod.getMetadata().getId())) {
				continue;
			}
			installedMods.add(new RecognizedModImpl(mod.getMetadata().getId(), mod.getMetadata().getVersion().getFriendlyString()));
		}
	}



	public List<RecognizedMod> getInstalledMods() {
		return this.installedMods;
	}

	public List<RecognizedMod> getRecognizedMods() {
		return this.recognizedMods;
	}

	public boolean getInitializationError() {
		return this.initializationError;
	}

}