package com.nebelnidas.modget.data;

public class LookupTable {
	private final Repository parentRepository;
	private final LookupTableEntry[] lookupTableEntries;

	public LookupTable(Repository parentRepository, LookupTableEntry[] lookupTableEntries) {
		this.parentRepository = parentRepository;
		this.lookupTableEntries = lookupTableEntries;
	}

	public Repository getParentRepository() {
		return this.parentRepository;
	}

	public LookupTableEntry[] getLookupTableEntries() {
		return this.lookupTableEntries;
	}

}
