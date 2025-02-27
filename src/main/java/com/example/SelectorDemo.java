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

				// Attribute selectors
				"/Root/*{type=component}",
				"/Root/Child2{type=component}",
				"/Root/Child2{type=foo}",
				"/Root/*{'type'='component'}",
				"/Root/*{variant=primary}",
				"/Root/*{visible=true}",

				// Combined selectors
				"/Root/*{type=component}/GrandChild1",
				"/*{type=container}/*/*{variant=button}"
		};

		for (String expression : expressions) {
			System.out.println("\nSelecting with expression: " + expression);
			List<TreeNode> result = selector.select(expression);

			System.out.println("Result nodes (" + result.size() + "):");
			for (TreeNode node : result) {
				System.out.println("- " + node +
						(node.getVariant() != null ? " [variant: " + node.getVariant() + "]" : "") +
						(!node.getAttributes().isEmpty() ? " [attrs: " + node.getAttributes() + "]" : ""));
			}
		}
	}
}