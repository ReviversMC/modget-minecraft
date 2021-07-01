package com.nebelnidas.modget.data;

import java.util.ArrayList;

public class RecognizedMod {
	private String id;
	private String currentVersion;
	private ArrayList<Package> availablePackages = new ArrayList<Package>();


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

	public ArrayList<Package> getAvailablePackages() {
		return this.availablePackages;
	}

	public void setAvailablePackages(ArrayList<Package> availablePackages) {
		this.availablePackages = availablePackages;
	}

}
