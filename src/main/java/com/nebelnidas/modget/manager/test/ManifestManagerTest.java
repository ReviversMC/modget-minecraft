// *************************************************************************
// This class is only temporary, until a proper test system is implemented!
// *************************************************************************


package com.nebelnidas.modget.manager.test;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.nebelnidas.modget.Modget;
import com.nebelnidas.modget.data.Manifest;
import com.nebelnidas.modget.data.Repository;
import com.nebelnidas.modget.manager.base.ManifestManagerBase;

public class ManifestManagerTest extends ManifestManagerBase {

	@Override
	public Manifest downloadManifest(Repository repo, String publisher, String modId) {
		String packageId = String.format("Repo%s.%s.%s", repo.getId(), publisher, modId);
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		String uri = assembleManifestUri(repo, publisher, modId);
		Manifest manifest;

		try {
			manifest = mapper.readValue(new File(uri), Manifest.class);
		} catch (Exception e) {
			if (e instanceof IOException) {
				Modget.logWarn(String.format("An error occurred while fetching the %s manifest. Please check your Internet connection!", packageId));
			} else {
				Modget.logWarn(String.format("An error occurred while parsing the %s manifest", packageId), e.getMessage());
			}
			return null;
		}
		Modget.logInfo(String.format("Fetched Manifest: %s", packageId));
		return manifest;
	}

}
