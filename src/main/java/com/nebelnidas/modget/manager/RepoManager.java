package com.nebelnidas.modget.manager;

import com.nebelnidas.modget.manager.base.RepoManagerBase;

public class RepoManager extends RepoManagerBase {

	public RepoManager() {
		// Add default repository
		addRepo("https://raw.githubusercontent.com/ReviversMC/modget-manifests/master");
	}

}
