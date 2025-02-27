package com.example;

import com.example.tree.TreeNode;
import java.util.List;
import java.util.Map;

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
		TreeNode root = new TreeNode("Root", "container", "default", "1.0.0");

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

		root.addChild(child1);
		root.addChild(child2);

		return root;
	}

	/**
	 * Print a tree graphically to the console.
	 *
	 * @param root the root node of the tree to print
	 */
	public static void printTree(TreeNode root) {
		System.out.println("\nTree Structure:");
		System.out.println("==============");
		printNodeRecursive(root, "", true);
		System.out.println("==============\n");
	}

	/**
	 * Helper method to recursively print a node and its children with proper indentation.
	 *
	 * @param node the current node to print
	 * @param prefix the prefix to use for indentation
	 * @param isTail whether this node is the last child of its parent
	 */
	private static void printNodeRecursive(TreeNode node, String prefix, boolean isTail) {
		// Print the current node
		System.out.println(prefix + (isTail ? "└── " : "├── ") + formatNode(node));

		// Calculate new prefix for children
		String newPrefix = prefix + (isTail ? "    " : "│   ");

		// Print children
		List<TreeNode> children = node.getChildren();
		for (int i = 0; i < children.size(); i++) {
			boolean isLastChild = (i == children.size() - 1);
			printNodeRecursive(children.get(i), newPrefix, isLastChild);
		}
	}

	/**
	 * Format a node for display including name and attributes.
	 *
	 * @param node the node to format
	 * @return a formatted string representing the node
	 */
	private static String formatNode(TreeNode node) {
		StringBuilder sb = new StringBuilder();
		sb.append(node.getName());

		// Add type and variant
		if (node.getType() != null || node.getVariant() != null) {
			sb.append(" (");
			if (node.getType() != null) {
				sb.append("type: ").append(node.getType());
				if (node.getVariant() != null) {
					sb.append(", ");
				}
			}
			if (node.getVariant() != null) {
				sb.append("variant: ").append(node.getVariant());
			}
			sb.append(")");
		}

		// Add attributes if any
		if (!node.getAttributes().isEmpty()) {
			sb.append(" {");
			boolean first = true;
			for (Map.Entry<String, String> entry : node.getAttributes().entrySet()) {
				if (!first) {
					sb.append(", ");
				}
				sb.append(entry.getKey()).append(": ").append(entry.getValue());
				first = false;
			}
			sb.append("}");
		}

		return sb.toString();
	}
}