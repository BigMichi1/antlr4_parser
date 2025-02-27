package com.example;

import com.example.tree.TreeNode;

/**
 * Utility class for building tree structures easily for testing or examples.
 */
public class TreeBuilder {

	/**
	 * Create a sample tree for testing.
	 *
	 * @return the root node of a sample tree
	 */
	public static TreeNode createSampleTree() {
		TreeNode root = new TreeNode("Root", "container", "default");

		TreeNode child1 = new TreeNode("Child1", "component", "primary");
		child1.addAttribute("visible", "true");

		TreeNode child2 = new TreeNode("Child2", "component", "secondary");
		child2.addAttribute("visible", "true");

		TreeNode grandchild1 = new TreeNode("GrandChild1", "element", "button");
		TreeNode grandchild2 = new TreeNode("GrandChild2", "element", "text");
		TreeNode grandchild3 = new TreeNode("GrandChild3", "element", "input");

		child1.addChild(grandchild1);
		child2.addChild(grandchild2);
		child2.addChild(grandchild3);

		root.addChild(child1);
		root.addChild(child2);

		return root;
	}
}