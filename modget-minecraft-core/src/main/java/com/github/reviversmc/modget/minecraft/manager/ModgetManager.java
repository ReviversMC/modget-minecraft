package com.github.reviversmc.modget.minecraft.manager;

import java.util.ArrayList;
import java.util.List;

import com.github.reviversmc.modget.library.manager.RepoManager;
import com.github.reviversmc.modget.library.util.ModScanner;
import com.github.reviversmc.modget.manifests.spec4.api.data.ManifestRepository;
import com.github.reviversmc.modget.manifests.spec4.api.data.mod.InstalledMod;
import com.github.reviversmc.modget.manifests.spec4.impl.data.mod.InstalledModImpl;
import com.github.reviversmc.modget.manifests.spec4.util.ManifestRepositoryUtils;
import com.github.reviversmc.modget.minecraft.Modget;
import com.github.reviversmc.modget.minecraft.command.CommandBase;
import com.github.reviversmc.modget.minecraft.config.ModgetConfig;

import org.apache.commons.lang3.exception.ExceptionUtils;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

public class ModgetManager {
	public final static RepoManager REPO_MANAGER = new RepoManager();
	private static volatile List<InstalledMod> installedMods = new ArrayList<>(20);
	private static volatile List<InstalledMod> recognizedMods = new ArrayList<>(10);
	private static volatile boolean initializationError = false;


	public static void init() {
		scanMods();
		try {
            List<String> repos = ModgetConfig.DEFAULT_REPOS;
			REPO_MANAGER.init(repos);
			reload(false);
		} catch (Exception e) {
			initializationError = true;
			Modget.logWarn("An error occurred while initializing Modget", ExceptionUtils.getStackTrace(e));
		}
	}

	public static void reload(boolean refreshRepos) throws Exception {
		try {
            if (refreshRepos == true) {
                REPO_MANAGER.refresh();
            }

			try {
				ManifestRepositoryUtils utils = new ManifestRepositoryUtils();
				for (ManifestRepository repo : REPO_MANAGER.getRepos()) {
					if (utils.checkForNewVersion(repo) == true) {
						Modget.logInfo(String.format("A new version of Repo %s has been detected! Please update Modget to be able to use it.", repo.getId()));
						CommandBase.setManifestApiOutdated(true);
					}
				}
			} catch (Exception e) {
				Modget.logWarn("Error while checking for repo updates", ExceptionUtils.getStackTrace(e));
			}

			recognizedMods = ModScanner.create().scanMods(installedMods, ModgetConfig.IGNORED_MODS, REPO_MANAGER.getRepos());
			initializationError = false;
		} catch (Exception e) {
			throw e;
		}
	}


	public static void scanMods() {
		installedMods.clear();

		for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
			if (ModgetConfig.IGNORED_MODS.contains(mod.getMetadata().getId())) {
				continue;
			}
			installedMods.add(new InstalledModImpl(mod.getMetadata().getId()) {{
				setInstalledVersion(mod.getMetadata().getVersion().getFriendlyString());
			}});
		}
	}



	public static List<InstalledMod> getInstalledMods() {
		return installedMods;
	}

	public static List<InstalledMod> getRecognizedMods() {
		return recognizedMods;
	}

	public static boolean getInitializationError() {
		return initializationError;
	}

}
