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
		rootNode = new TreeNode("Root", "container", "default", "1.0.0");

		TreeNode child1 = new TreeNode("Child1", "component", "primary", "2.0.0");
		child1.addAttribute("visible", "true");

		TreeNode child2 = new TreeNode("Child2", "component", "secondary", "2.1.0");
		child2.addAttribute("visible", "true");

		TreeNode grandchild1_1 = new TreeNode("GrandChild1", "element", "button", "3.0.0");
		TreeNode grandchild2 = new TreeNode("GrandChild2", "element", "text", "3.0.0");
		TreeNode grandchild3 = new TreeNode("GrandChild3", "element", "input", "3.1.0");

		// Create another GrandChild1 node for Child2
		TreeNode grandchild1_2 = new TreeNode("GrandChild1", "element", "button-alt", "3.2.0");

		child1.addChild(grandchild1_1);
		child2.addChild(grandchild1_2); // Add GrandChild1 to Child2
		child2.addChild(grandchild2);
		child2.addChild(grandchild3);

		rootNode.addChild(child1);
		rootNode.addChild(child2);

		// Create selector
		selector = new TreeSelector(rootNode);
	}
}