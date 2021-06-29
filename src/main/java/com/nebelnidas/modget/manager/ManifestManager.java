package com.nebelnidas.modget.manager;

import java.net.URL;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.nebelnidas.modget.Modget;
import com.nebelnidas.modget.data.LookupTableEntry;
import com.nebelnidas.modget.data.ManifestMod;

import org.apache.commons.text.WordUtils;

import net.fabricmc.loader.api.ModContainer;

public class ManifestManager {
	private final ArrayList<ManifestMod> recognizedManifestMods = new ArrayList<ManifestMod>();


	public ManifestMod downloadManifest(URL url, String modId) throws Exception {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		ManifestMod manifestMod;
		try {
			manifestMod = mapper.readValue(url, ManifestMod.class);
		} catch (Exception e) {
			Modget.logWarn(String.format("An error occurred while parsing the %s manifest", WordUtils.capitalize(modId)), e.getMessage());
			throw e;
		}
		Modget.logInfo("Fetched Manifest: " + manifestMod.getName());
		return manifestMod;
	}

	public URL assembleManifestUrl(String publisher, String modId) {
		try {
			return new URL(String.format("https://raw.githubusercontent.com/ReviversMC/modget-manifests/master/manifests/%s/%s/%s/%s.%s.yaml", publisher.charAt(0), publisher, modId, publisher, modId));
		} catch (Exception e) {
			Modget.logWarn(String.format("An error occurred while assembling the %s manifest url", WordUtils.capitalize(modId), e.getMessage()));
			return null;
		}
	}

	public void mapRecognizedModContainersToManifests() {
		ArrayList<ModContainer> recognizedModContainers = Modget.MAIN_MANAGER.getRecognizedModContainers();
		ArrayList<LookupTableEntry> recognizedLookupTableEntries = Modget.MAIN_MANAGER.getRecognizedLookupTableEntries();
		URL url;

		recognizedManifestMods.clear();
		for (int i = 0; i < recognizedModContainers.size(); i++) {
			if (recognizedLookupTableEntries.get(i).getPackages().size() <= 1) {
				String[] parts = recognizedLookupTableEntries.get(i).getPackages().get(0).toString().split("\\.");
				url = assembleManifestUrl(parts[0], parts[1]);

				try {
					ManifestMod manifestMod;
					manifestMod = downloadManifest(url, parts[1]);
					recognizedManifestMods.add(manifestMod);
				} catch (Exception e) {
					Modget.logWarn(String.format("An error occurred while parsing the %s manifest", recognizedModContainers.get(i).getMetadata().getName()), e.getMessage());
				}

			} else {
				Modget.logWarn("An error occurred", "There are two or more packages available with this ID. Modget doesn't support this yet!");
				break;
			}
		}
	}


	public ArrayList<ManifestMod> getRecognizedManifestMods() {
		return this.recognizedManifestMods;
	}

}
