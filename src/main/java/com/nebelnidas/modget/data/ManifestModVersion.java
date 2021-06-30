package com.nebelnidas.modget.data;

import java.util.ArrayList;

public class ManifestModVersion {
	private String version;
	private ArrayList<String> minecraftVersions;
	private String md5;
	private ManifestModVersionDownload[] downloadPageUrls;
	private ManifestModVersionDownload[] fileUrls;

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public ArrayList<String> getMinecraftVersions() {
		return this.minecraftVersions;
	}

	public void setMinecraftVersions(ArrayList<String> minecraftVersions) {
		this.minecraftVersions = minecraftVersions;
	}

	public String getMd5() {
		return this.md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public ManifestModVersionDownload[] getDownloadPageUrls() {
		return this.downloadPageUrls;
	}

	public void setDownloadPageUrls(ManifestModVersionDownload[] downloadPageUrls) {
		this.downloadPageUrls = downloadPageUrls;
	}

	public ManifestModVersionDownload[] getFileUrls() {
		return this.fileUrls;
	}

	public void setFileUrls(ManifestModVersionDownload[] fileUrls) {
		this.fileUrls = fileUrls;
	}

}
