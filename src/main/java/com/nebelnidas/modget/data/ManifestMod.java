package com.nebelnidas.modget.data;

import java.util.ArrayList;

public class ManifestMod {
	private String publisher;
	private String name;
	private String id;
	private String home;
	private String issues;
	private String modType;
	private ArrayList<ManifestModVersion> downloads;

	public ManifestMod() {
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

	public String getHome() {
		return this.home;
	}

	public void setHome(String home) {
		this.home = home;
	}

	public String getIssues() {
		return this.issues;
	}

	public void setIssues(String issues) {
		this.issues = issues;
	}

	public String getModType() {
		return this.modType;
	}

	public void setModType(String modType) {
		this.modType = modType;
	}

	public ArrayList<ManifestModVersion> getDownloads() {
		return this.downloads;
	}

	public void setDownloads(ArrayList<ManifestModVersion> downloads) {
		this.downloads = downloads;
	}

}

