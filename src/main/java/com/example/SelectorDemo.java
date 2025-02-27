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
				"/Root/*{visible=true}",

				// Version attribute selectors
				"/Root/*{version=2.0.0}",
				"/*/*/*{version=3.0.0}",

				// Multiple attribute selectors
				"/Root/*{type=component, variant=primary}",
				"/Root/*{type=component, variant=secondary}",
				"/Root/*{type=component, visible=true, variant=secondary}",
				"/Root/*{version=2.0.0, type=component}",
				"/Root/*{'type'='component', 'variant'='primary'}",

				// Combined and nested selectors
				"/Root/*{type=component}/GrandChild1",
				"/*{type=container}/Child2/*{type=element, version=3.0.0}",
				"/Root{type=container}/Child2{type=component, version=2.1.0}/GrandChild1{variant=button-alt}"
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