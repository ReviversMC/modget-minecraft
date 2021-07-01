package com.nebelnidas.modget.manager;

import java.util.ArrayList;

import com.nebelnidas.modget.Modget;
import com.nebelnidas.modget.config.ModgetConfig;
import com.nebelnidas.modget.data.LookupTableEntry;
import com.nebelnidas.modget.data.Manifest;
import com.nebelnidas.modget.data.ManifestModVersion;
import com.nebelnidas.modget.data.ManifestModVersionDownload;
import com.nebelnidas.modget.data.Package;
import com.nebelnidas.modget.data.RecognizedMod;
import com.nebelnidas.modget.util.Util;

import org.apache.commons.text.WordUtils;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.VersionParsingException;

public class MainManager {
	public final LookupTableManager LOOKUP_TABLE_MANAGER = new LookupTableManager();
	public final ManifestManager MANIFEST_MANAGER = new ManifestManager();
	private ArrayList<LookupTableEntry> recognizedLookupTableEntries = new ArrayList<LookupTableEntry>();
	private ArrayList<RecognizedMod> recognizedMods = new ArrayList<RecognizedMod>();
	private ArrayList<RecognizedMod> modsWithUpdates = new ArrayList<RecognizedMod>();

	public void reload() {
		LOOKUP_TABLE_MANAGER.refreshLookupTableNoException();
		scanMods();
		recognizedMods = MANIFEST_MANAGER.downloadManifests(recognizedMods);
		findUpdates();
	}

	public void scanMods() {
		if (LOOKUP_TABLE_MANAGER.getLookupTableEntries() == null) {return;}
		recognizedLookupTableEntries.clear();
		recognizedMods.clear();

		String recognizedModsList = "";
		for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
			if (!ModgetConfig.IGNORED_MODS.contains(mod.getMetadata().getId())) {
				for (LookupTableEntry lookupTableEntry : LOOKUP_TABLE_MANAGER.getLookupTableEntries()) {
					if (lookupTableEntry.getId().equalsIgnoreCase(mod.getMetadata().getId())) {
						recognizedLookupTableEntries.add(lookupTableEntry);
						recognizedMods.add(new RecognizedMod() {{
							setId(mod.getMetadata().getId());
							setCurrentVersion(mod.getMetadata().getVersion().getFriendlyString());

							ArrayList<Package> packages = new ArrayList<Package>();
							for (String packageName : lookupTableEntry.getPackages()) {
								packages.add(new Package());
							}
							setAvailablePackages(packages);
						}});

						if (recognizedModsList.length() != 0) {recognizedModsList += ", ";}
						recognizedModsList += WordUtils.capitalize(lookupTableEntry.getId());
						continue;
					}
				}
			}
		}
		Modget.logInfo(String.format("Recognized %s out of %s mods: %s", recognizedLookupTableEntries.size(), FabricLoader.getInstance().getAllMods().size(), recognizedModsList));
	}


	public ManifestModVersion findModVersionMatchingCurrentMinecraftVersion(Package p) {
		for (ManifestModVersion version : p.getManifestModVersions()) {
			if (version.getMinecraftVersions().contains(Util.getMinecraftVersion().getId())) {
				Modget.logInfo(String.format("Found %s version (%s) supporting the installed Minecraft version", p.getName(), version.getVersion()));
				return(version);
			}
		}
		return null;
	}

	public void findUpdates() {
		RecognizedMod mod;
		SemanticVersion currentVersion;
		ManifestModVersion latestManifestModVersion;
		SemanticVersion latestVersion;

		modsWithUpdates.clear();
		for (int i = 0; i < recognizedMods.size(); i++) {
			mod = recognizedMods.get(i);

			for (int j = 0; j < mod.getAvailablePackages().size(); j++) {
				Package p = mod.getAvailablePackages().get(j);

				latestManifestModVersion = findModVersionMatchingCurrentMinecraftVersion(p);
				p.setLatestCompatibleModVersion(latestManifestModVersion);

				// Try parsing the semantic manifest and mod versions
				try {
					currentVersion = SemanticVersion.parse(mod.getCurrentVersion());
				} catch (VersionParsingException e) {
					Modget.logWarn(String.format("%s doesn't respect semantic versioning!", p.getName()), e.getMessage());
					return;
				}
				try {
					latestVersion = SemanticVersion.parse(latestManifestModVersion.getVersion());
				} catch (VersionParsingException e) {
					Modget.logWarn(String.format("The %s manifest doesn't respect semantic versioning!", p.getName()), e.getMessage());
					return;
				}
	
				if (latestManifestModVersion != null && latestVersion.compareTo(currentVersion) > 0) {
					Modget.logInfo(String.format("Found %s update (%s) for the installed Minecraft version", p.getName(), latestVersion.getFriendlyString()));
					try {
						modsWithUpdates.add(mod);
					} catch (Exception e) {
						Modget.logWarn("An error occurred", e.getMessage());
					}
				} else {
					Modget.logInfo(String.format("Already using the latest %s version supporting the installed Minecraft version", p.getName()));
				}
			}
		}
	}

	public ArrayList<RecognizedMod> getRecognizedMods() {
		return this.recognizedMods;
	}

	public ArrayList<LookupTableEntry> getRecognizedLookupTableEntries() {
		return this.recognizedLookupTableEntries;
	}

	public ArrayList<RecognizedMod> getModsWithUpdates() {
		return this.modsWithUpdates;
	}
}