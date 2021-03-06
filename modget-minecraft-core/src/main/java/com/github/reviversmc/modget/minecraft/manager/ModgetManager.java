package com.github.reviversmc.modget.minecraft.manager;

import java.util.ArrayList;
import java.util.List;

import com.github.reviversmc.modget.library.manager.RepoManager;
import com.github.reviversmc.modget.library.util.ModScanner;
import com.github.reviversmc.modget.manifests.spec4.api.data.ManifestRepository;
import com.github.reviversmc.modget.manifests.spec4.util.ManifestRepositoryUtils;
import com.github.reviversmc.modget.minecraft.Modget;
import com.github.reviversmc.modget.minecraft.api.InstalledModAdvanced;
import com.github.reviversmc.modget.minecraft.api.impl.CustomModMetadataImpl;
import com.github.reviversmc.modget.minecraft.api.impl.InstalledModAdvancedImpl;
import com.github.reviversmc.modget.minecraft.command.CommandBase;
import com.github.reviversmc.modget.minecraft.config.ModgetConfig;

import org.apache.commons.lang3.exception.ExceptionUtils;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

public class ModgetManager {
	public final static RepoManager REPO_MANAGER = new RepoManager();
	public final static UpdateManager UPDATE_MANAGER = new UpdateManager();
	private static volatile List<InstalledModAdvanced> installedMods = new ArrayList<>(20);
	private static volatile List<InstalledModAdvanced> recognizedMods = new ArrayList<>(10);
	private static volatile boolean initializationError = false;


	public static void init() {
		scanMods();
		try {
            try {
                ModgetConfig.INSTANCE.load();
            } catch (Exception e) {}
            List<String> repos = ModgetConfig.INSTANCE.DEFAULT_REPOS;
			REPO_MANAGER.init(repos);
			reload();
            if (ModgetConfig.INSTANCE.getAutoCheck() == true) {
                UPDATE_MANAGER.searchForNotOptOutedUpdates();
            }
		} catch (Exception e) {
			initializationError = true;
			Modget.logWarn("An error occurred while initializing Modget", ExceptionUtils.getStackTrace(e));
		}
	}

	public static void reload() throws Exception {
		try {
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

            recognizedMods = ModScanner.create().scanMods(installedMods, ModgetConfig.INSTANCE.IGNORED_MODS, REPO_MANAGER.getRepos());
			initializationError = false;
		} catch (Exception e) {
			throw e;
		}
	}


	public static void scanMods() {
		installedMods.clear();

		for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
			if (ModgetConfig.INSTANCE.IGNORED_MODS.contains(mod.getMetadata().getId())) {
				continue;
			}
			installedMods.add(new InstalledModAdvancedImpl(mod.getMetadata().getId()) {{
				setInstalledVersion(mod.getMetadata().getVersion().getFriendlyString());

                if (mod.getMetadata().containsCustomValue(Modget.NAMESPACE)) {
                    try {
                        setCustomMetadata(new CustomModMetadataImpl(mod.getMetadata().getCustomValue(Modget.NAMESPACE).getAsObject()));
                    } catch (ClassCastException e) {
                        Modget.logWarn(getId(), String.format("\"%s\" Is Not An Object", Modget.NAMESPACE));
                    }
                }
			}});
		}
	}



	public static List<InstalledModAdvanced> getInstalledMods() {
		return installedMods;
	}

	public static List<InstalledModAdvanced> getRecognizedMods() {
		return recognizedMods;
	}

	public static boolean getInitializationError() {
		return initializationError;
	}

}
