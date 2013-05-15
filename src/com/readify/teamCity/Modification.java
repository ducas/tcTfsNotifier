package com.readify.teamCity;

import com.microsoft.tfs.core.clients.workitem.WorkItem;

public abstract class Modification {
	private Integer id;

	Modification(Integer id) {
		this.setId(id);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public abstract void performAction(WorkItem workItem);

}
