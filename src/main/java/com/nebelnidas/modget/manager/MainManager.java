package com.nebelnidas.modget.manager;

import java.util.ArrayList;

import com.nebelnidas.modget.Modget;
import com.nebelnidas.modget.config.ModgetConfig;
import com.nebelnidas.modget.data.LookupTableEntry;
import com.nebelnidas.modget.data.ManifestMod;
import com.nebelnidas.modget.data.ManifestModVersion;
import com.nebelnidas.modget.util.Util;

import org.apache.commons.text.WordUtils;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.VersionParsingException;

public class MainManager {
	public final LookupTableManager LOOKUP_TABLE_MANAGER = new LookupTableManager();
	public final ManifestManager MANIFEST_MANAGER = new ManifestManager();
	private final ArrayList<ModContainer> recognizedModContainers = new ArrayList<ModContainer>();
	private final ArrayList<LookupTableEntry> recognizedLookupTableEntries = new ArrayList<LookupTableEntry>();
	private final ArrayList<ManifestMod> modManifestsWithUpdates = new ArrayList<ManifestMod>();

	public void reload() {
		LOOKUP_TABLE_MANAGER.refreshLookupTableNoException();
		scanMods();
		MANIFEST_MANAGER.mapRecognizedModContainersToManifests();
		findUpdates();
	}

	public void scanMods() {
		recognizedModContainers.clear();
		recognizedLookupTableEntries.clear();
		if (LOOKUP_TABLE_MANAGER.getLookupTableEntries() == null) {return;}

		String recognizedModsList = "";
		for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
			if (!ModgetConfig.IGNORED_MODS.contains(mod.getMetadata().getId())) {
				for (LookupTableEntry lookupTableEntry : LOOKUP_TABLE_MANAGER.getLookupTableEntries()) {
					if (lookupTableEntry.getId().equalsIgnoreCase(mod.getMetadata().getId())) {
						recognizedModContainers.add(mod);
						recognizedLookupTableEntries.add(lookupTableEntry);

						if (recognizedModsList.length() != 0) {recognizedModsList += ", ";}
						recognizedModsList += WordUtils.capitalize(lookupTableEntry.getId());
						continue;
					}
				}
			}
		}
		Modget.logInfo(String.format("Recognized %s out of %s mods: %s", recognizedModContainers.size(), FabricLoader.getInstance().getAllMods().size(), recognizedModsList));
	}


	public ManifestModVersion findManifestModVersionMatchingCurrentMinecraftVersion(ManifestMod mod) {
		for (ManifestModVersion version : mod.getDownloads()) {
			if (version.getMinecraftVersions().contains(Util.getMinecraftVersion().getId())) {
				Modget.logInfo(String.format("Found %s version (%s) supporting the installed Minecraft version", mod.getName(), version.getVersion()));
				return(version);
			}
		}
		return null;
	}

	public void findUpdates() {
		ArrayList<ManifestMod> recognizedManifestMods = MANIFEST_MANAGER.getRecognizedManifestMods();
		ManifestMod mod;
		SemanticVersion oldVersion;
		ManifestModVersion newManifestModVersion;
		SemanticVersion newVersion;

		modManifestsWithUpdates.clear();
		for (int i = 0; i < recognizedManifestMods.size(); i++) {
			mod = recognizedManifestMods.get(i);
			newManifestModVersion = findManifestModVersionMatchingCurrentMinecraftVersion(mod);
			// Try parsing the semantic manifest and mod versions
			try {
				oldVersion = SemanticVersion.parse(recognizedModContainers.get(i).getMetadata().getVersion().getFriendlyString());
			} catch (VersionParsingException e) {
				Modget.logWarn(String.format("%s doesn't respect semantic versioning!", mod.getName()), e.getMessage());
				return;
			}
			try {
				newVersion = SemanticVersion.parse(newManifestModVersion.getVersion());
			} catch (VersionParsingException e) {
				Modget.logWarn(String.format("The %s manifest doesn't respect semantic versioning!", mod.getName()), e.getMessage());
				return;
			}

			if (newManifestModVersion != null && newVersion.compareTo(oldVersion) > 0) {
				Modget.logInfo(String.format("Found %s update (%s) for the installed Minecraft version", mod.getName(), newVersion.getFriendlyString()));
				try {
					modManifestsWithUpdates.add(mod);
				} catch (Exception e) {
					Modget.logWarn("An error occurred", e.getMessage());
				}
			} else {
				Modget.logInfo(String.format("Already using the latest %s version supporting the installed Minecraft version", mod.getName()));
			}
		}
	}

	public ArrayList<ModContainer> getRecognizedModContainers() {
		return this.recognizedModContainers;
	}

	public ArrayList<LookupTableEntry> getRecognizedLookupTableEntries() {
		return this.recognizedLookupTableEntries;
	}

	public ArrayList<ManifestMod> getModManifestsWithUpdates() {
		return this.modManifestsWithUpdates;
	}

}