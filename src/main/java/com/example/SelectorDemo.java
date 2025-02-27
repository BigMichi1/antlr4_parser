package com.example;

import com.example.tree.TreeNode;
import com.example.tree.TreeSelector;

import java.util.List;

/**
 * Demonstration class with main method for running tree selector examples.
 */
public class SelectorDemo {

	public static void main(String[] args) {
		// Create a sample tree
		TreeNode root = TreeBuilder.createSampleTree();
		TreeSelector selector = new TreeSelector(root);

		// Visualize the tree structure
		TreeBuilder.printTree(root);

		// Test various selector expressions
		String[] expressions = {
				// Basic selectors
				"/Root",
				"/Root/Child1",
				"/Root/Child2",

				// Wildcard selectors
				"/Root/*",
				"/Root/Child2/GrandChild2",
				"/*/Child2",

				// Attribute selectors
				"/Root/*{type=component}",
				"/Root/*{type=component, variant=primary}",
				"/Root/*{type=component,version=*}",

				// Multiple selectors (pipe-separated)
				"/Root/Child1|/Root/Child2",
				"/Root/*{variant=primary}|/Root/*{variant=secondary}",

				// Deep traversal selectors
				"**/*",                           // Select all nodes in the tree
				"**/Child2",                      // Select Child2 node
				"**/*{variant=secondary}",        // Select nodes with variant=secondary
				"**/GrandChild1",                 // Select all GrandChild1 nodes

				// Placeholder selectors
				"/~~",                            // All nodes except root
				"/Root/~~/*{version=3.0.0}",      // Nodes with version=3.0.0 under Root
				"/Root/Child2/~~",                // All nodes under Child2
				"/Root/~~/GrandChild2",           // GrandChild2 at any level under Root
				"/Root/~~{type=element,variant=button}", // Elements with button variant under Root
				"/Root/~~/~~",                    // All grandchildren
				"/~~{version=2.0.0}",             // Nodes with version=2.0.0 at any level
				"/Root/Child1/~~|/Root/Child2/~~" // All grandchildren via multiple selectors
		};

		for (String expression : expressions) {
			System.out.println("\nSelecting with expression: " + expression);
			List<TreeNode> result = selector.select(expression);

			System.out.println("Result nodes (" + result.size() + "):");
			for (TreeNode node : result) {
				System.out.println("- " + node +
						(node.getVariant() != null ? " [variant: " + node.getVariant() + "]" : "") +
						(node.getVersion() != null ? " [version: " + node.getVersion() + "]" : "") +
						(!node.getAttributes().isEmpty() ? " [attrs: " + node.getAttributes() + "]" : ""));
			}
		}
	}
}