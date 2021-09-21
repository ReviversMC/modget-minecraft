package com.github.nebelnidas.modget.manager;

import com.github.nebelnidas.modget.manager.base.RepoManagerBase;

public class RepoManager extends RepoManagerBase {

	public RepoManager() {
		// Add default repository
		addRepo("https://raw.githubusercontent.com/ReviversMC/modget-manifests/master");
	}

}
