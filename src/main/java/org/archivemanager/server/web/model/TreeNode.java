package org.archivemanager.server.web.model;

import java.util.ArrayList;
import java.util.List;


public class TreeNode {
	private long id;
	private String text;
	private String state;
	private String iconCls;
	private List<TreeNode> children = new ArrayList<TreeNode>();
	
	public TreeNode(long id, String text, String state) {
		this.id = id;
		this.text = text;
		this.state = state;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}	
	public String getIconCls() {
		return iconCls;
	}
	public void setIconCls(String icon) {
		this.iconCls = icon;
	}
	public List<TreeNode> getChildren() {
		return children;
	}
	public void setChildren(List<TreeNode> children) {
		this.children = children;
	}
	
}
