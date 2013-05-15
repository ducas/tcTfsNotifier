package com.readify.teamCity;

import com.microsoft.tfs.core.clients.workitem.WorkItem;
import com.microsoft.tfs.core.clients.workitem.link.Hyperlink;
import com.microsoft.tfs.core.clients.workitem.link.LinkCollection;
import com.microsoft.tfs.core.clients.workitem.link.LinkFactory;

public class AddHyperlinkModification extends Modification {

	private String url;
	private String comment;

	public AddHyperlinkModification(Integer id, String url, String comment) {
		super(id);
		this.setUrl(url);
		this.setComment(comment);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public void performAction(WorkItem workItem) {
		LinkCollection links = workItem.getLinks();
		Hyperlink link = LinkFactory.newHyperlink(getUrl(), getComment(), false);
		links.add(link);
	}

}
