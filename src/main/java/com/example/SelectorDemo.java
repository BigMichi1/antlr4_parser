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

				// Version attribute selectors
				"/Root/*{version=2.0.0}",
				"/*/*/*{version=3.0.0}",
				"/Root{version=1.0.0}/Child2{version=2.1.0}/GrandChild1{version=3.2.0}",
				"/Root/*{'version'='2.1.0'}",

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
						(node.getVersion() != null ? " [version: " + node.getVersion() + "]" : "") +
						(!node.getAttributes().isEmpty() ? " [attrs: " + node.getAttributes() + "]" : ""));
			}
		}
	}
}