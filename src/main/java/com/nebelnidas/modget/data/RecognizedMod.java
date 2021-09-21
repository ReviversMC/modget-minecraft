package com.nebelnidas.modget.data;

import java.util.ArrayList;

public class RecognizedMod {
	private String id;
	private String currentVersion;
	private ArrayList<LookupTableEntry> lookupTableEntries = new ArrayList<LookupTableEntry>();
	private ArrayList<Package> availablePackages = new ArrayList<Package>();
	private boolean updateAvailable = false;


	public RecognizedMod() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCurrentVersion() {
		return this.currentVersion;
	}

	public void setCurrentVersion(String currentVersion) {
		this.currentVersion = currentVersion;
	}

	public ArrayList<LookupTableEntry> getLookupTableEntries() {
		return this.lookupTableEntries;
	}

	public void addLookupTableEntry(LookupTableEntry lookupTableEntry) {
		this.lookupTableEntries.add(lookupTableEntry);
	}

	public ArrayList<Package> getAvailablePackages() {
		return this.availablePackages;
	}

	public void setAvailablePackages(ArrayList<Package> availablePackages) {
		this.availablePackages = availablePackages;
	}

	public void addAvailablePackage(Package availablePackage) {
		this.availablePackages.add(availablePackage);
	}

	public boolean isUpdateAvailable() {
		return this.updateAvailable;
	}

	public void setUpdateAvailable(boolean updateAvailable) {
		this.updateAvailable = updateAvailable;
	}

}
