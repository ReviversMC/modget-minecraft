package com.nebelnidas.modget.manager;

import java.net.URL;
import java.net.UnknownHostException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.nebelnidas.modget.Modget;
import com.nebelnidas.modget.data.LookupTableEntry;

public class LookupTableManager {
	private static LookupTableEntry[] lookupTableEntries;

	public LookupTableManager() {
		refreshLookupTableNoException();
	}


	public void refreshLookupTable() throws UnknownHostException, Exception {
		LookupTableEntry[] newLookupTableEntries = downloadLookupTableEntries();
		if (newLookupTableEntries != null) {
			lookupTableEntries = newLookupTableEntries;
		}
	}
	public void refreshLookupTableNoException() {
		try {
			refreshLookupTable();
		} catch (Exception e) {}
	}

	private static LookupTableEntry[] downloadLookupTableEntries() throws Exception {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		try {
            return mapper.readValue(new URL("https://raw.githubusercontent.com/ReviversMC/modget-manifests/master/lookup-table.yaml"), LookupTableEntry[].class);
        } catch (Exception e) {
			if (e instanceof UnknownHostException) {
				// Modget.logWarn(new TranslatableText("error." + Modget.NAMESPACE + ".github_connection_error"));
				Modget.logWarn("Couldn't connect to GitHub. Please check your Internet connection!");
			} else {
				// Modget.logWarn(new TranslatableText("error." + Modget.NAMESPACE + ".lookup_table_access_error"), e.getMessage());
				Modget.logWarn("Couldn't connect to GitHub. Please check your Internet connection!");
			}
			throw e;
        }
	}

	public LookupTableEntry[] getLookupTableEntries() {
		return LookupTableManager.lookupTableEntries;
	}

}
