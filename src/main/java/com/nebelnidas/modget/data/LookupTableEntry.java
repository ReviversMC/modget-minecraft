package com.nebelnidas.modget.data;

import java.util.ArrayList;

public class LookupTableEntry {
	private String id;
	private ArrayList<String> names;
	private ArrayList<String> variants;

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

	public ArrayList<String> getVariants() {
		return this.variants;
	}

	public void setVariants(ArrayList<String> variants) {
		this.variants = variants;
	}

}
