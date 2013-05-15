package com.readify.teamCity;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.microsoft.tfs.core.TFSTeamProjectCollection;
import com.microsoft.tfs.core.clients.workitem.WorkItem;
import com.microsoft.tfs.core.clients.workitem.WorkItemClient;
import com.microsoft.tfs.core.clients.workitem.project.Project;
import com.microsoft.tfs.core.clients.workitem.query.WorkItemCollection;
import com.microsoft.tfs.core.httpclient.Credentials;
import com.microsoft.tfs.core.httpclient.UsernamePasswordCredentials;

public class TfsWorkItemManipulator {

	private TFSTeamProjectCollection teamProject;
	private Project project;
	private WorkItemClient client;

	public void open(String uri, String username, String password,	String projectName)throws URISyntaxException {
		Credentials creds = new UsernamePasswordCredentials(username, password);
		teamProject = new TFSTeamProjectCollection(new URI(uri), creds);
		teamProject.authenticate();
		project = teamProject.getWorkItemClient().getProjects().get(projectName);
		client = project.getWorkItemClient();
	}
	
	public void close() {
		client.close();
		teamProject.close();
	}

	public void updateWorkItems(Map<Integer, List<Modification>> modifications) {
		Set<Integer> keys = modifications.keySet();
		String wiql = buildQuery(keys);
		WorkItemCollection items = client.query(wiql);
		
		for (int i = 0; i < items.size(); i++) {
			WorkItem item = items.getWorkItem(i);
			item.open();
			List<Modification> itemModifications = modifications.get(item.getID());
			for (Modification mod : itemModifications) {
				mod.performAction(item);
			}
			item.save();
		}
	}

	private String buildQuery(Set<Integer> tfsIds) {
		String wiql = "SELECT ID, Title FROM WorkItem WHERE";
		int i = 0;
		for (Integer id : tfsIds) {
			wiql += " ID = " + id.toString();
			if (i != tfsIds.size() - 1)
				wiql += " OR";
			i++;
		}
		return wiql;
	}

}
