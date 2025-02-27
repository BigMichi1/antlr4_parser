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

				// Deep traversal selectors
				"**/*",                           // Select all nodes in the tree
				"**/Child2",                      // Select Child2 node
				"**/*{variant=secondary}",        // Select nodes with variant=secondary

				// Placeholder selectors
				"/~~",                            // All nodes except root
				"/Root/~~/*{version=3.0.0}",      // Nodes with version=3.0.0 under Root
				"/Root/Child2/~~",                // All nodes under Child2

				// Relative path operators
				"/Root/./*",                      // Stay at Root level, then select all children
				"/Root/../*",                     // Go up from Root, select all at that level
				"/Root/Child2/./GrandChild2",     // Current level no-op
				"/Root/Child2/../Child1",         // Go to sibling via parent
				"/Root/Child2/../*{variant=primary}", // Go up and filter by attribute
				"/Root/Child2/GrandChild2/../../Child1", // Multiple parent operators
				"/./*",                           // Current level at start
				"/Root/Child1/../Child2/./GrandChild2", // Combined operators
				"/Root/Child2/*/../*"             // Select children then their siblings via parent
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