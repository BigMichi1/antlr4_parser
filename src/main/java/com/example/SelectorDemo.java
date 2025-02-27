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
				"/*/*/GrandChild2",

				// Single attribute selectors
				"/Root/*{type=component}",
				"/Root/Child2{type=component}",
				"/Root/*{variant=primary}",

				// Multiple attribute selectors
				"/Root/*{type=component, variant=primary}",
				"/Root/*{type=component, visible=true, variant=secondary}",

				// Wildcard attribute value selectors
				"/Root/*{type=*}",
				"/Root/*{type=component,version=*}",
				"/Root/*{type=*,variant=*,version=*}",

				// Multiple selectors (pipe-separated)
				"/Root/Child1|/Root/Child2",
				"/Root/*{variant=primary}|/Root/*{variant=secondary}",
				"/Root/Child1/GrandChild1|/Root/Child2/GrandChild1|/Root/Child2/GrandChild2",

				// Deep traversal selectors
				"**/*",                           // Select all nodes in the tree
				"**/Child2",                      // Select Child2 node
				"**/*{variant=secondary}",        // Select nodes with variant=secondary
				"**/GrandChild1",                 // Select all GrandChild1 nodes
				"**/Child2{type=component}",      // Select Child2 with type=component
				"**/*{version=3.0.0}",           // Select nodes with version=3.0.0
				"**/Child2/GrandChild2",          // Select GrandChild2 under Child2
				"**/*{type=element,variant=button}", // Select elements with button variant
				"**/GrandChild1|**/GrandChild2",   // Combined deep traversal with multiple selectors
				"**/*{type=element,variant=*}"     // Deep traversal with wildcard attribute
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