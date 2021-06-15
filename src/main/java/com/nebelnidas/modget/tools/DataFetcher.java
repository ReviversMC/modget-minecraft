package com.nebelnidas.modget.tools;

import java.net.URL;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.nebelnidas.modget.Modget;
import com.nebelnidas.modget.data.LookupTableEntry;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

public class DataFetcher {
	private LookupTableEntry[] lookupTableEntries;
	private ArrayList<ModContainer> recognizedModContainers;

	public DataFetcher() {
		recognizedModContainers = new ArrayList<ModContainer>();
		refreshLookupTable();
		scanMods();
	}

	public void refreshLookupTable() {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		try {
            lookupTableEntries = mapper.readValue(new URL("https://raw.githubusercontent.com/ReviversMC/modget-manifests/master/lookup-table.yaml"), LookupTableEntry[].class);
        } catch (Exception e) {
			Modget.logWarn("An error occurred while trying to access the mod lookup table", "\n" + e.toString());
        }
	}

	public void scanMods() {
		recognizedModContainers.clear();
		for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
			Modget.logInfo("Scanning mod: " + mod.getMetadata().getId());
			for (LookupTableEntry lookupTableEntry : lookupTableEntries) {
				if (lookupTableEntry.getId().equalsIgnoreCase(mod.getMetadata().getId())) {
					Modget.logInfo("Recognized mod: " + mod.getMetadata().getId());
					recognizedModContainers.add(mod);
					continue;
				}
			}
		}
		Modget.logInfo("Recognized mods: " + Integer.toString(recognizedModContainers.size()));
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

}