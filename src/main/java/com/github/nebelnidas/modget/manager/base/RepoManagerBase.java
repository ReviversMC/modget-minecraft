package com.github.nebelnidas.modget.manager.base;

import java.util.ArrayList;

import com.github.nebelnidas.modget.Modget;
import com.github.nebelnidas.modget.data.Repository;

public class RepoManagerBase {
	protected ArrayList<Repository> repos = new ArrayList<Repository>();
	protected int lastId = -1;


	public ArrayList<Repository> getRepos() {
		return this.repos;
	}

	public void addRepo(String url) {
		repos.add(new Repository(lastId + 1, url));
		Modget.logInfo(String.format("Repository added: ID: %s; URI: %s", lastId + 1, url));
		lastId++;
	}

	public void removeRepo(int id) {
		this.repos.remove(id);
	}

	public void disableRepo(int id) {
		repos.get(id).setEnabled(false);
	}

	public void enableRepo(int id) {
		repos.get(id).setEnabled(true);
	}
}
