package com.github.nebelnidas.modget.data;

import java.io.File;
import java.net.URL;
import java.net.UnknownHostException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.nebelnidas.modget.Modget;

public class Repository {
	private final int id;
	private final String uri;
	private LookupTable lookupTable;
	private boolean enabled = true;

	public Repository(int id, String uri) {
		this.id = id;
		if (uri.endsWith("/")) {
			uri = uri.substring(0, uri.length() - 1);
		}
		this.uri = uri;
		refreshLookupTableNoException();
	}

	public void refreshLookupTable() throws UnknownHostException, Exception {
		lookupTable = downloadLookupTable();
	}
	public void refreshLookupTableNoException() {
		try {
			refreshLookupTable();
		} catch (Exception e) {}
	}

	private LookupTable downloadLookupTable() throws Exception {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

		try {
			LookupTableEntry[] entries = mapper.readValue(new URL(uri + "/lookup-table.yaml"), LookupTableEntry[].class);

			LookupTable newLookupTable = new LookupTable(this, entries);
			for (LookupTableEntry entry : entries) {
				entry.setParentLookupTable(newLookupTable);
			}
			return newLookupTable;
        } catch (Exception e) {
			if (e instanceof UnknownHostException) {
				// Modget.logWarn(new TranslatableText("error." + Modget.NAMESPACE + ".github_connection_error"));
				Modget.logWarn("Couldn't connect to the manifest repository. Please check your Internet connection!");
			} else {
				// Modget.logWarn(new TranslatableText("error." + Modget.NAMESPACE + ".lookup_table_access_error"), e.getMessage());
				Modget.logWarn("Couldn't connect to the manifest repository", e.getMessage());
			}
			throw e;
        }
	}


	public int getId() {
		return this.id;
	}

	public String getUri() {
		return this.uri;
	}

	public LookupTable getLookupTable() {
		return this.lookupTable;
	}

	public void setLookupTable(LookupTable lookupTable) {
		this.lookupTable = lookupTable;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
