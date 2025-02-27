package com.example.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a node in the tree structure with attributes and children.
 */
public class TreeNode {
	private String name;
	private String type;
	private String variant;
	private String version;
	private Map<String, String> attributes = new HashMap<>();
	private List<TreeNode> children = new ArrayList<>();
	private TreeNode parent;

	public TreeNode(String name) {
		this.name = name;
	}

	public TreeNode(String name, String type, String variant) {
		this.name = name;
		this.type = type;
		this.variant = variant;
	}

	public TreeNode(String name, String type, String variant, String version) {
		this.name = name;
		this.type = type;
		this.variant = variant;
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getVariant() {
		return variant;
	}

	public void setVariant(String variant) {
		this.variant = variant;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void addAttribute(String key, String value) {
		attributes.put(key, value);
	}

	public String getAttribute(String key) {
		return attributes.get(key);
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void addChild(TreeNode child) {
		children.add(child);
		child.parent = this;  // Set the parent reference
	}

	public List<TreeNode> getChildren() {
		return children;
	}

	public TreeNode getParent() {
		return parent;
	}

	@Override
	public String toString() {
		return "TreeNode{name='" + name + "', type='" + type + "', variant='" + variant +
				(version != null ? "', version='" + version : "") +
				"', children=" + children.size() + "}";
	}
}