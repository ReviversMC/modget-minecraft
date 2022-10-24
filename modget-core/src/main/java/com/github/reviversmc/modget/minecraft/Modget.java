package com.github.reviversmc.modget.minecraft;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.reviversmc.modget.library.manager.RepoManager;
import com.github.reviversmc.modget.library.util.ModScanner;
import com.github.reviversmc.modget.manifests.spec4.api.data.ManifestRepository;
import com.github.reviversmc.modget.manifests.spec4.util.ManifestRepositoryUtils;
import com.github.reviversmc.modget.minecraft.api.InstalledModAdvanced;
import com.github.reviversmc.modget.minecraft.api.impl.CustomModMetadataImpl;
import com.github.reviversmc.modget.minecraft.api.impl.InstalledModAdvancedImpl;
import com.github.reviversmc.modget.minecraft.command.CommandBase;
import com.github.reviversmc.modget.minecraft.config.ModgetConfig;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.util.Identifier;

public class Modget {
    public static final Modget INSTANCE = new Modget();
    public static final String NAMESPACE = "modget";
    public static final String NAMESPACE_SERVER = "modgetserver";
    public static final Logger LOGGER = LogManager.getLogger("Modget");
	public final RepoManager REPO_MANAGER = new RepoManager();
	public final UpdateManager UPDATE_MANAGER = new UpdateManager();
	private volatile List<InstalledModAdvanced> installedMods = new ArrayList<>(20);
	private volatile List<InstalledModAdvanced> recognizedMods = new ArrayList<>(10);
	private volatile boolean initializationError = false;
    private boolean modPresentOnServer = false;


    public static void logWarn(String name) {
        LOGGER.warn(name);
    }
    public static void logWarn(String name, String msg) {
        LOGGER.warn(String.format("%s: %s", name, msg));
    }

    public static void logInfo(String info) {
        LOGGER.info(info);
    }


    public void init() {
        // Check if the client sees this mod on a server
        Identifier identifier = new Identifier(NAMESPACE);
        ServerPlayNetworking.registerGlobalReceiver(identifier, (server, player, handler, buf, responseSender) -> { });

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (!client.isInSingleplayer()) {
                try {
                    modPresentOnServer = ClientPlayNetworking.canSend(identifier);
                } catch (Exception e) {
                    // ignored
                }
            }
        });

		scanMods();

		try {
            try {
                ModgetConfig.INSTANCE.load();
            } catch (Exception e) {
                // ignored
            }

            List<String> repos = ModgetConfig.INSTANCE.DEFAULT_REPOS;
			REPO_MANAGER.init(repos);
			reload();

            if (ModgetConfig.INSTANCE.getAutoCheck()) {
                UPDATE_MANAGER.searchForNotOptOutedUpdates();
            }
		} catch (Exception e) {
			initializationError = true;
			Modget.logWarn("An error occurred while initializing Modget", ExceptionUtils.getStackTrace(e));
		}
	}

	public void reload() throws Exception {
		try {
			try {
				ManifestRepositoryUtils utils = new ManifestRepositoryUtils();

				for (ManifestRepository repo : REPO_MANAGER.getRepos()) {
					if (utils.checkForNewVersion(repo)) {
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

	public void scanMods() {
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


	public List<InstalledModAdvanced> getInstalledMods() {
		return installedMods;
	}

	public List<InstalledModAdvanced> getRecognizedMods() {
		return recognizedMods;
	}

	public boolean hasInitializationError() {
		return initializationError;
	}

	public boolean isModPresentOnServer() {
		return modPresentOnServer;
	}
}
