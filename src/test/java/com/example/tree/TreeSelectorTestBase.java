package com.example.tree;

import org.junit.jupiter.api.BeforeEach;

/**
 * Base class for tree selector tests with common setup.
 */
public class TreeSelectorTestBase {
	protected TreeNode rootNode;
	protected TreeSelector selector;

	@BeforeEach
	void setUp() {
		// Create a test tree structure
		rootNode = new TreeNode("Root", "container", "default");

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

		rootNode.addChild(child1);
		rootNode.addChild(child2);

		// Create selector
		selector = new TreeSelector(rootNode);
	}
}