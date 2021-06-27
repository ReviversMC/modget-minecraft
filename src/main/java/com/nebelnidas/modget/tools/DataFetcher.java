package com.nebelnidas.modget.tools;

import java.net.URL;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.nebelnidas.modget.Modget;
import com.nebelnidas.modget.config.ModgetConfig;
import com.nebelnidas.modget.data.LookupTableEntry;
import com.nebelnidas.modget.data.ManifestMod;
import com.nebelnidas.modget.data.ManifestModVersion;
import com.nebelnidas.modget.util.Util;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.VersionParsingException;

public class DataFetcher {
	private LookupTableEntry[] lookupTableEntries;
	private ArrayList<ModContainer> recognizedModContainers;
	private ArrayList<LookupTableEntry> recognizedLookupTableEntries;
	private ArrayList<ManifestMod> recognizedManifestMods;
	private ArrayList<ManifestMod> modManifestsWithUpdates;

	public DataFetcher() {
		recognizedModContainers = new ArrayList<ModContainer>();
		recognizedLookupTableEntries = new ArrayList<LookupTableEntry>();
		recognizedManifestMods = new ArrayList<ManifestMod>();
		modManifestsWithUpdates = new ArrayList<ManifestMod>();
		refreshLookupTable();
		scanMods();
		mapRecognizedModContainersToManifests();
		findUpdates();
	}

	public void refreshLookupTable() {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		try {
            lookupTableEntries = mapper.readValue(new URL("https://raw.githubusercontent.com/ReviversMC/modget-manifests/master/lookup-table.yaml"), LookupTableEntry[].class);
        } catch (Exception e) {
			Modget.logWarn("An error occurred while trying to access the mod lookup table", e.toString());
        }
	}

	public void scanMods() {
		recognizedModContainers.clear();
		recognizedLookupTableEntries.clear();
		for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
			if (!ModgetConfig.IGNORED_MODS.contains(mod.getMetadata().getId())) {
				Modget.logInfo("Scanning mod: " + mod.getMetadata().getName());
				for (LookupTableEntry lookupTableEntry : lookupTableEntries) {
					if (lookupTableEntry.getId().equalsIgnoreCase(mod.getMetadata().getId())) {
						Modget.logInfo("Recognized mod: " + mod.getMetadata().getName());
						recognizedModContainers.add(mod);
						recognizedLookupTableEntries.add(lookupTableEntry);
						continue;
					}
				}
			}
		}
		Modget.logInfo("Recognized mods: " + Integer.toString(recognizedModContainers.size()));
	}

	public void mapRecognizedModContainersToManifests() {
		URL url;
		for (int i = 0; i < recognizedModContainers.size(); i++) {
			if (recognizedLookupTableEntries.get(i).getPackages().size() <= 1) {
				String[] parts = recognizedLookupTableEntries.get(i).getPackages().get(0).toString().split("\\.");
				String publisher = parts[0];
				String id = parts[1];
				try {
					url = new URL(String.format("https://raw.githubusercontent.com/ReviversMC/modget-manifests/master/manifests/%s/%s/%s/%s.%s.yaml", publisher.charAt(0), publisher, id, publisher, id));
				} catch (Exception e) {
					Modget.logWarn(String.format("An error occurred while assembling the %s manifest url", recognizedModContainers.get(i).getMetadata().getName()), e.toString());
					break;
				}
			} else {
				Modget.logWarn("An error occurred", "There are two or more packages available with this ID. Modget doesn't support this yet!");
				break;
			}
			ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
			ManifestMod manifestMod;
			try {
				manifestMod = mapper.readValue(url, ManifestMod.class);
			} catch (Exception e) {
				Modget.logWarn(String.format("An error occurred while parsing the %s manifest", recognizedModContainers.get(i).getMetadata().getName()), e.toString());
				break;
			}
			Modget.logInfo("Fetched Manifest: " + manifestMod.getName());
			recognizedManifestMods.add(manifestMod);
		}
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
		ManifestMod mod;
		SemanticVersion oldVersion;
		ManifestModVersion newManifestModVersion;
		SemanticVersion newVersion;
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

	public LookupTableEntry[] getLookupTableEntries() {
		return this.lookupTableEntries;
	}

	public void setLookupTableEntries(LookupTableEntry[] lookupTableEntries) {
		this.lookupTableEntries = lookupTableEntries;
	}

	public ArrayList<ModContainer> getRecognizedModContainers() {
		return this.recognizedModContainers;
	}

	public void setRecognizedModContainers(ArrayList<ModContainer> recognizedModContainers) {
		this.recognizedModContainers = recognizedModContainers;
	}

	public ArrayList<LookupTableEntry> getRecognizedLookupTableEntries() {
		return this.recognizedLookupTableEntries;
	}

	public void setRecognizedLookupTableEntries(ArrayList<LookupTableEntry> recognizedLookupTableEntries) {
		this.recognizedLookupTableEntries = recognizedLookupTableEntries;
	}

	public ArrayList<ManifestMod> getRecognizedManifestMods() {
		return this.recognizedManifestMods;
	}

	public void setRecognizedManifestMods(ArrayList<ManifestMod> recognizedManifestMods) {
		this.recognizedManifestMods = recognizedManifestMods;
	}

	public ArrayList<ManifestMod> getModManifestsWithUpdates() {
		return this.modManifestsWithUpdates;
	}

	public void setModManifestsWithUpdates(ArrayList<ManifestMod> modManifestsWithUpdates) {
		this.modManifestsWithUpdates = modManifestsWithUpdates;
	}

}