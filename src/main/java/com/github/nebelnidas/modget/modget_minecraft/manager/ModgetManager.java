package com.github.nebelnidas.modget.modget_minecraft.manager;

import java.util.ArrayList;
import java.util.List;

import com.github.nebelnidas.modget.manifest_api.api.v0.def.RepositoryUtils;
import com.github.nebelnidas.modget.manifest_api.api.v0.def.data.RecognizedMod;
import com.github.nebelnidas.modget.manifest_api.api.v0.def.data.Repository;
import com.github.nebelnidas.modget.manifest_api.api.v0.impl.RepositoryUtilsImpl;
import com.github.nebelnidas.modget.manifest_api.api.v0.impl.data.RecognizedModImpl;
import com.github.nebelnidas.modget.modget_lib.manager.RepoManager;
import com.github.nebelnidas.modget.modget_lib.util.ModgetLibUtils;
import com.github.nebelnidas.modget.modget_minecraft.Modget;
import com.github.nebelnidas.modget.modget_minecraft.command.CommandBase;
import com.github.nebelnidas.modget.modget_minecraft.config.ModgetConfig;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

public class ModgetManager {
	public final RepoManager REPO_MANAGER = new RepoManager();
	private volatile List<RecognizedMod> installedMods = new ArrayList<>();
	private volatile List<RecognizedMod> recognizedMods = new ArrayList<>();
	private volatile boolean initializationError = false;


	public void init() {
		scanMods();
		try {
			REPO_MANAGER.init(ModgetConfig.DEFAULT_REPOS);
			REPO_MANAGER.initRepos();
			reload();
		} catch (Exception e) {
			initializationError = true;
			Modget.logWarn("An error occurred while initializing Modget", e.getMessage());
		}
	}

	public void reload() throws Exception {
		try {
			REPO_MANAGER.refresh();

			try {
				RepositoryUtils utils = new RepositoryUtilsImpl();
				for (Repository repo : REPO_MANAGER.getRepos()) {
					if (utils.checkForNewVersion(repo) == true) {
						Modget.logInfo(String.format("A new version of Repo %s has been detected! Please update Modget to be able to use it.", repo.getId()));
						CommandBase.setManifestApiOutdated(true);
					}
				}
			} catch (Exception e) {}

			recognizedMods = ModgetLibUtils.create().scanMods(installedMods, ModgetConfig.IGNORED_MODS, REPO_MANAGER.getRepos());
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