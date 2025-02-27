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
				"/Root",
				"/Root/Child1",
				"/Root/Child2",
				"/Root/*",
				"/Root/Child2/GrandChild2",
				// Wildcard expressions
				"/*/Child2",
				"/*/*/GrandChild2",
				"/*/*/*",
				// GrandChild1 selectors
				"/Root/*/GrandChild1",      // Should find both GrandChild1 nodes
				"/Root/Child1/GrandChild1", // Should find one GrandChild1
				"/Root/Child2/GrandChild1"  // Should find one GrandChild1
		};

		for (String expression : expressions) {
			System.out.println("\nSelecting with expression: " + expression);
			List<TreeNode> result = selector.select(expression);

			System.out.println("Result nodes (" + result.size() + "):");
			for (TreeNode node : result) {
				System.out.println("- " + node + (node.getVariant() != null ? " [variant: " + node.getVariant() + "]" : ""));
			}
		}
	}
}