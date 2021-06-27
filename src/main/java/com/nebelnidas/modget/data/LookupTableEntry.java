package com.nebelnidas.modget.data;

import java.util.ArrayList;

public class LookupTableEntry {
	private String id;
	private ArrayList<String> names;
	private ArrayList<String> packages;
	private ArrayList<String> tags;

	public LookupTableEntry() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ArrayList<String> getNames() {
		return this.names;
	}

	public void setNames(ArrayList<String> names) {
		this.names = names;
	}

	public ArrayList<String> getPackages() {
		return this.packages;
	}

	public void setPackages(ArrayList<String> packages) {
		this.packages = packages;
	}

	public ArrayList<String> getTags() {
		return this.tags;
	}

	public void setTags(ArrayList<String> tags) {
		this.tags = tags;
	}

}
