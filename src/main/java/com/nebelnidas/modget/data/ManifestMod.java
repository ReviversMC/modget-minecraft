package com.nebelnidas.modget.data;

import java.util.ArrayList;

public class ManifestMod {
	private String manifestSpecVersion;
	private String publisher;
	private String name;
	private String id;
	private ManifestModThirdPartyIds thirdPartyIds;
	private String license;
	private String description;
	private String home;
	private String source;
	private String issues;
	private String support;
	private String modType;
	private String side;
	private ArrayList<ManifestModVersion> downloads;

	public String getManifestSpecVersion() {
		return this.manifestSpecVersion;
	}

	public void setManifestSpecVersion(String manifestSpecVersion) {
		this.manifestSpecVersion = manifestSpecVersion;
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

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ManifestModThirdPartyIds getThirdPartyIds() {
		return this.thirdPartyIds;
	}

	public void setThirdPartyIds(ManifestModThirdPartyIds thirdPartyIds) {
		this.thirdPartyIds = thirdPartyIds;
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

	public ArrayList<ManifestModVersion> getDownloads() {
		return this.downloads;
	}

	public void setDownloads(ArrayList<ManifestModVersion> downloads) {
		this.downloads = downloads;
	}

}

