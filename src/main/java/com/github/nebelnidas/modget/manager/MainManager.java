package com.github.nebelnidas.modget.manager;

import java.util.ArrayList;

import com.github.nebelnidas.modget.Modget;
import com.github.nebelnidas.modget.config.ModgetConfig;
import com.github.nebelnidas.modget.data.LookupTableEntry;
import com.github.nebelnidas.modget.data.ManifestModVersion;
import com.github.nebelnidas.modget.data.Package;
import com.github.nebelnidas.modget.data.RecognizedMod;
import com.github.nebelnidas.modget.data.Repository;
import com.github.nebelnidas.modget.util.Util;

import org.apache.commons.text.WordUtils;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.VersionParsingException;

public class MainManager {
	public final RepoManager REPO_MANAGER = new RepoManager();
	public final ManifestManager MANIFEST_MANAGER = new ManifestManager();

	private ArrayList<RecognizedMod> recognizedMods = new ArrayList<RecognizedMod>();
	private int ignoredModsCount = 0;

	
	public void init() {
		reload();
	}

	public void reload() {
		for (Repository repo : REPO_MANAGER.getRepos()) {
			repo.refreshLookupTableNoException();
		}
		scanMods();
		recognizedMods = MANIFEST_MANAGER.downloadManifests(recognizedMods);
		findUpdates();
	}

	public void scanMods() {
		ArrayList<Repository> repos = REPO_MANAGER.getRepos();
		recognizedMods.clear();

		for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
			// If mod is contained in built-in ignored list, skip it
			if (ModgetConfig.IGNORED_MODS.contains(mod.getMetadata().getId())) {
				ignoredModsCount++;
				continue;
			}
			// Otherwise, loop over each repository...
			for (int i = 0; i < repos.size(); i++) {
				if (repos.get(i).getLookupTable() == null) {continue;}

				// ...and each lookup table entry within...
				lookupTableEntryLoop:
				for (LookupTableEntry lookupTableEntry : repos.get(i).getLookupTable().getLookupTableEntries()) {
					// ...to check if the mod IDs match.
					if (lookupTableEntry.getId().equalsIgnoreCase(mod.getMetadata().getId())) {
						// If they match, check if it has already been found before (in a different repo)
						for (RecognizedMod recognizedMod : recognizedMods) {
							if (recognizedMod.getId().equals(mod.getMetadata().getId())) {
								// If so, just add the data to the existing recognized mod
								recognizedMod.addLookupTableEntry(lookupTableEntry);
								// ...and skip ahead to the next mod.
								break lookupTableEntryLoop;
							}
						}
						// Otherwise, create a new one
						recognizedMods.add(new RecognizedMod() {{
							setId(mod.getMetadata().getId());
							setCurrentVersion(mod.getMetadata().getVersion().getFriendlyString());
							addLookupTableEntry(lookupTableEntry);
						}});
						// ...and skip ahead to the next mod.
						break lookupTableEntryLoop;
					}
				}
			}
		}
		// Log which mods have been recognized
		int modCount = 0;
		StringBuilder message = new StringBuilder();
		for (RecognizedMod mod : recognizedMods) {
			modCount++;
			if (modCount > 1) {
				message.append("; ");
			}
			String modId = WordUtils.capitalize(mod.getId());
			if (!message.toString().contains(modId)) {
				message.append(modId);
			}
		}
		if (message.length() != 0) {message.insert(0, ": ");}
		Modget.logInfo(String.format("Recognized %s out of %s mods%s", modCount, FabricLoader.getInstance().getAllMods().size() - ignoredModsCount, message.toString()));
	}


	public ManifestModVersion findModVersionMatchingCurrentMinecraftVersion(Package p) {
		for (ManifestModVersion version : p.getManifestModVersions()) {
			if (version.getMinecraftVersions().contains(Util.getMinecraftVersion().getId())) {
				return(version);
			}
		}
		return null;
	}

	public void findUpdates() {
		RecognizedMod mod;
		ManifestModVersion latestManifestModVersion;

		for (int i = 0; i < recognizedMods.size(); i++) {
			mod = recognizedMods.get(i);
			mod.setUpdateAvailable(false);

			if (mod.getAvailablePackages().size() > 1) {
				Modget.logInfo(String.format("There are multiple packages available for %s", WordUtils.capitalize(mod.getId())));
			}
			for (int j = 0; j < mod.getAvailablePackages().size(); j++) {
				Package p = mod.getAvailablePackages().get(j);

				latestManifestModVersion = findModVersionMatchingCurrentMinecraftVersion(p);
				if (latestManifestModVersion == null) {continue;}
				p.setLatestCompatibleModVersion(latestManifestModVersion);

				// Try parsing the semantic manifest and mod versions
				SemanticVersion currentVersion;
				try {
					currentVersion = SemanticVersion.parse(mod.getCurrentVersion());
				} catch (VersionParsingException e) {
					Modget.logWarn(String.format("%s doesn't respect semantic versioning, an update check is therefore not possible! %s", p.getName(), e.getMessage()));
					break;
				}
				SemanticVersion latestVersion;
				try {
					latestVersion = SemanticVersion.parse(latestManifestModVersion.getVersion());
				} catch (VersionParsingException e) {
					Modget.logWarn(String.format("The %s manifest doesn't respect semantic versioning, an update check is therefore not possible!", p.getName()), e.getMessage());
					continue;
				}

				// Check for updates
				String packageId = String.format("Repo%s.%s.%s",
					p.getParentLookupTableEntry().getParentLookupTable().getParentRepository().getId(),
					p.getPublisher(), p.getParentLookupTableEntry().getId());
				if (latestManifestModVersion != null && latestVersion.compareTo(currentVersion) > 0) {
					Modget.logInfo(String.format("Found an update for %s: %s %s", p.getName(),
						packageId, latestVersion.getFriendlyString()));
					mod.setUpdateAvailable(true);

				} else {
					Modget.logInfo(String.format("No update has been found at %s", packageId));
				}
			}
		}
	}

	public ArrayList<RecognizedMod> getRecognizedMods() {
		return this.recognizedMods;
	}

	public ArrayList<RecognizedMod> getModsWithUpdates() {
		ArrayList<RecognizedMod> modsWithUpdates = new ArrayList<RecognizedMod>();
		for (RecognizedMod mod : recognizedMods) {
			if (mod.isUpdateAvailable() == true) {
				modsWithUpdates.add(mod);
			}
		}
		return modsWithUpdates;
	}
}