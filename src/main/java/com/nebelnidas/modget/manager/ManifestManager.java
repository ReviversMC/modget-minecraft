package com.nebelnidas.modget.manager;

import java.net.URL;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.nebelnidas.modget.Modget;
import com.nebelnidas.modget.data.LookupTableEntry;
import com.nebelnidas.modget.data.Manifest;
import com.nebelnidas.modget.data.Package;
import com.nebelnidas.modget.data.RecognizedMod;

import org.apache.commons.text.WordUtils;

public class ManifestManager {

	public Manifest downloadManifest(URL url, String modId) throws Exception {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		Manifest manifestMod;
		try {
			manifestMod = mapper.readValue(url, Manifest.class);
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

	public ArrayList<RecognizedMod> downloadManifests(ArrayList<RecognizedMod> recognizedMods) {
		ArrayList<LookupTableEntry> recognizedLookupTableEntries = Modget.MAIN_MANAGER.getRecognizedLookupTableEntries();

		for (int i = 0; i < recognizedLookupTableEntries.size(); i++) {
			LookupTableEntry entry = recognizedLookupTableEntries.get(i);

			for (int j = 0; j < entry.getPackages().size(); j++) {
				String[] parts = entry.getPackages().get(j).toString().split("\\.");
				URL url = assembleManifestUrl(parts[0], parts[1]);

				try {
					Manifest manifest = downloadManifest(url, parts[1]);
					RecognizedMod mod = recognizedMods.get(i);

					Package p = mod.getAvailablePackages().get(j);
						p.setPublisher(manifest.getPublisher());
						p.setName(manifest.getName());
						p.setLicense(manifest.getLicense());
						p.setDescription(manifest.getDescription());
						p.setHome(manifest.getHome());
						p.setSource(manifest.getSource());
						p.setIssues(manifest.getIssues());
						p.setSupport(manifest.getSupport());
						p.setModType(manifest.getModType());
						p.setSide(manifest.getSide());
						p.setManifestModVersions(manifest.getDownloads());
					ArrayList<Package> newPackages = mod.getAvailablePackages();
					newPackages.set(j, p);
					mod.setAvailablePackages(newPackages);

				} catch (Exception e) {
					Modget.logWarn(String.format("An error occurred while parsing the %s manifest", WordUtils.capitalize(entry.getId())), e.getMessage());
				}
			}
		}
		return recognizedMods;
	}

}
