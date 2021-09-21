package com.github.nebelnidas.modget.data;

import java.util.ArrayList;

public class Package {
	private final LookupTableEntry parentLookupTableEntry;
	private String publisher;
	private String name;
	private String license;
	private String description;
	private String home;
	private String source;
	private String issues;
	private String support;
	private String modType;
	private String side;
	private ArrayList<ManifestModVersion> modVersions = new ArrayList<ManifestModVersion>();
	private ManifestModVersion latestCompatibleModVersion;


	public Package(LookupTableEntry parentLookupTableEntry) {
		this.parentLookupTableEntry = parentLookupTableEntry;
	}


	public LookupTableEntry getParentLookupTableEntry() {
		return this.parentLookupTableEntry;
	}


	public String getPublisher() {
		return this.publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLicense() {
		return this.license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getHome() {
		return this.home;
	}

	public void setHome(String home) {
		this.home = home;
	}

	public String getSource() {
		return this.source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getIssues() {
		return this.issues;
	}

	public void setIssues(String issues) {
		this.issues = issues;
	}

	public String getSupport() {
		return this.support;
	}

	public void setSupport(String support) {
		this.support = support;
	}

	public String getModType() {
		return this.modType;
	}

	public void setModType(String modType) {
		this.modType = modType;
	}

	public String getSide() {
		return this.side;
	}

	public void setSide(String side) {
		this.side = side;
	}
	
	public ArrayList<ManifestModVersion> getManifestModVersions() {
		return this.modVersions;
	}

	public void setManifestModVersions(ArrayList<ManifestModVersion> modVersions) {
		this.modVersions = modVersions;
	}

	public ManifestModVersion getLatestCompatibleModVersion() {
		return this.latestCompatibleModVersion;
	}

	public void setLatestCompatibleModVersion(ManifestModVersion latestCompatibleModVersion) {
		this.latestCompatibleModVersion = latestCompatibleModVersion;
	}

}
