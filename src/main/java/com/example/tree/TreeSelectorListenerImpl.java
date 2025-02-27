package com.example.tree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A listener for the ANTLR-generated parse tree to process tree selector expressions.
 */
public class TreeSelectorListenerImpl extends TreeSelectorBaseListener {

	private final TreeNode rootNode;
	private List<TreeNode> currentNodes = new ArrayList<>();
	private final List<TreeNode> resultNodes = new ArrayList<>();
	private final Set<TreeNode> processedNodes = new HashSet<>();

	public TreeSelectorListenerImpl(TreeNode rootNode) {
		this.rootNode = rootNode;
	}

	@Override
	public void enterSelector(TreeSelectorParser.SelectorContext ctx) {
		currentNodes.clear();
		resultNodes.clear();
		processedNodes.clear();

		// Handle the root part (first segment) of the path
		if (ctx.nodeName() == null && ctx.wildcard() == null && ctx.placeholder() == null &&
				ctx.currentNode() == null && ctx.parentNode() == null) {
			return;
		}

		boolean isWildcard = ctx.wildcard() != null;
		boolean isPlaceholder = ctx.placeholder() != null;
		boolean isCurrentNode = ctx.currentNode() != null;
		boolean isParentNode = ctx.parentNode() != null;

		if (isWildcard) {
			// Wildcard at root matches the root node itself
			currentNodes.add(rootNode);
		} else if (isPlaceholder) {
			// Placeholder at root - get all nodes in the tree except the root itself
			List<TreeNode> allNodes = new ArrayList<>();
			for (TreeNode child : rootNode.getChildren()) {
				allNodes.add(child);
				collectDescendantsRecursively(child, allNodes);
			}
			currentNodes.addAll(allNodes);
		} else if (isCurrentNode) {
			// Current node (.) - stay at the same level (root in this case)
			currentNodes.add(rootNode);
		} else if (isParentNode) {
			// Parent node (..) - one level up from root would be nothing
			// Root has no parent, so we leave currentNodes empty
		} else {
			// Named node at root
			String rootName = ctx.nodeName().getText();
			if (rootNode.getName().equals(rootName)) {
				currentNodes.add(rootNode);
			}
		}

		// Apply attribute selector if present
		if (ctx.attributeSelector() != null) {
			currentNodes = filterNodesByAttributes(currentNodes, ctx.attributeSelector());
		}

		// If there are no nodeSelectors, the current nodes are the result
		if (ctx.nodeSelector().isEmpty()) {
			resultNodes.addAll(currentNodes);
		}
	}

	@Override
	public void enterNodeSelector(TreeSelectorParser.NodeSelectorContext ctx) {
		List<TreeNode> matchingNodes = new ArrayList<>();

		// Check if this is a wildcard, placeholder, current node, parent node, or named node
		boolean isWildcard = ctx.wildcard() != null;
		boolean isPlaceholder = ctx.placeholder() != null;
		boolean isCurrentNode = ctx.currentNode() != null;
		boolean isParentNode = ctx.parentNode() != null;
		String nodeName = isWildcard ? "*" : (isPlaceholder ? "~~" :
				isCurrentNode ? "." : isParentNode ? ".." :
						(ctx.nodeName() != null ? ctx.nodeName().getText() : ""));

		if (isPlaceholder) {
			// For placeholder, we need to find all descendants of current nodes
			for (TreeNode node : currentNodes) {
				// For chained placeholders, we need a fresh set of processed nodes
				Set<TreeNode> localProcessedNodes = new HashSet<>();
				List<TreeNode> descendants = new ArrayList<>();
				collectDescendantsRecursivelyLocal(node, descendants, localProcessedNodes);
				matchingNodes.addAll(descendants);
			}
		} else if (isCurrentNode) {
			// Current node (.) - stay at the same level
			matchingNodes.addAll(currentNodes);
		} else if (isParentNode) {
			// Parent node (..) - go up one level
			boolean hasRootNode = false;

			for (TreeNode node : currentNodes) {
				TreeNode parent = node.getParent();
				if (parent != null) {
					matchingNodes.add(parent);
				} else if (node == rootNode) {
					// Special case: if we're looking for the parent of root,
					// we'll mark that we have the root node but don't add it to matching nodes yet
					hasRootNode = true;
				}
			}

			// If we have the root node and we're going to apply wildcard next,
			// we need special handling to ensure "/Root/../*" returns the root
			if (hasRootNode && currentNodes.contains(rootNode)) {
				// We're at the root level, so for parent level + wildcard,
				// we need to make the root node available for wildcard selection
				matchingNodes.add(null); // Special marker to handle root specially in wildcard
			}
		} else {
			// For normal wildcard or named node
			for (TreeNode node : currentNodes) {
				if (node == null) {
					// Special case for "/Root/../*": null marker means we're at the root's parent level
					// and need to return the root node as a child of this level
					matchingNodes.add(rootNode);
				} else if (isWildcard) {
					// Wildcard matches all children
					matchingNodes.addAll(node.getChildren());
				} else {
					// Standard name matching
					for (TreeNode child : node.getChildren()) {
						if (child.getName().equals(nodeName)) {
							matchingNodes.add(child);
						}
					}
				}
			}
		}

		// Apply attribute selector if present
		if (ctx.attributeSelector() != null) {
			matchingNodes = filterNodesByAttributes(matchingNodes, ctx.attributeSelector());
		}

		currentNodes = matchingNodes;

		// Check if this is the last nodeSelector
		TreeSelectorParser.SelectorContext parentCtx = (TreeSelectorParser.SelectorContext) ctx.getParent();
		if (parentCtx != null) {
			int lastNodeSelectorIndex = parentCtx.nodeSelector().size() - 1;
			int currentIndex = parentCtx.nodeSelector().indexOf(ctx);

			if (currentIndex == lastNodeSelectorIndex) {
				resultNodes.addAll(matchingNodes);
			}
		}
	}

	/**
	 * Recursively collect all nodes in the tree.
	 *
	 * @param node The current node to process
	 * @param allNodes The list to collect all nodes into
	 */
	private void collectNodesRecursively(TreeNode node, List<TreeNode> allNodes) {
		// Prevent processing the same node twice
		if (processedNodes.contains(node)) {
			return;
		}

		processedNodes.add(node);
		allNodes.add(node);

		for (TreeNode child : node.getChildren()) {
			collectNodesRecursively(child, allNodes);
		}
	}

	/**
	 * Recursively collect all descendants of a node (excluding the node itself).
	 *
	 * @param node The current node to process
	 * @param descendants The list to collect descendants into
	 */
	private void collectDescendantsRecursively(TreeNode node, List<TreeNode> descendants) {
		for (TreeNode child : node.getChildren()) {
			// Prevent processing the same node twice
			if (!processedNodes.contains(child)) {
				processedNodes.add(child);
				descendants.add(child);
				collectDescendantsRecursively(child, descendants);
			}
		}
	}

	/**
	 * Recursively collect all descendants of a node (excluding the node itself).
	 * Uses a local set of processed nodes to allow for chained placeholders.
	 *
	 * @param node The current node to process
	 * @param descendants The list to collect descendants into
	 * @param localProcessedNodes A local set to track processed nodes for this operation
	 */
	private void collectDescendantsRecursivelyLocal(TreeNode node, List<TreeNode> descendants, Set<TreeNode> localProcessedNodes) {
		for (TreeNode child : node.getChildren()) {
			// Prevent processing the same node twice within this collection operation
			if (!localProcessedNodes.contains(child)) {
				localProcessedNodes.add(child);
				descendants.add(child);
				collectDescendantsRecursivelyLocal(child, descendants, localProcessedNodes);
			}
		}
	}

	/**
	 * Filter nodes based on multiple attribute expressions.
	 * A node matches only if it matches ALL attribute expressions.
	 * An attribute with value "*" is treated as a wildcard and always matches.
	 *
	 * @param nodes The list of nodes to filter
	 * @param attrSelector The attribute selector containing multiple expressions
	 * @return A filtered list of nodes that match all attribute expressions
	 */
	public List<TreeNode> filterNodesByAttributes(List<TreeNode> nodes, TreeSelectorParser.AttributeSelectorContext attrSelector) {
		List<TreeNode> filteredNodes = new ArrayList<>();

		// If there are no expressions, return the original list
		if (attrSelector.attributeExpr().isEmpty()) {
			return nodes;
		}

		// For each node, check if it matches ALL attribute expressions
		for (TreeNode node : nodes) {
			boolean matchesAllAttributes = true;

			// Check each attribute expression
			for (TreeSelectorParser.AttributeExprContext exprCtx : attrSelector.attributeExpr()) {
				String attrName = cleanAttributeValue(exprCtx.attributeName().getText());
				String attrValue = cleanAttributeValue(exprCtx.attributeValue().getText());

				// If attribute value is "*", treat it as a wildcard (always matches)
				if ("*".equals(attrValue)) {
					continue; // Skip this attribute check, equivalent to ignoring it
				}

				// Check if the node matches this specific attribute expression
				boolean matchesThisAttribute = false;

				if ("type".equals(attrName) && attrValue.equals(node.getType())) {
					matchesThisAttribute = true;
				} else if ("variant".equals(attrName) && attrValue.equals(node.getVariant())) {
					matchesThisAttribute = true;
				} else if ("version".equals(attrName) && compareVersions(attrValue, node.getVersion())) {
					matchesThisAttribute = true;
				} else if (attrValue.equals(node.getAttribute(attrName))) {
					matchesThisAttribute = true;
				}

				// If any attribute expression doesn't match, the node doesn't match
				if (!matchesThisAttribute) {
					matchesAllAttributes = false;
					break;
				}
			}

			// Add node only if it matches all attribute expressions
			if (matchesAllAttributes) {
				filteredNodes.add(node);
			}
		}

		return filteredNodes;
	}

	/**
	 * Compare version strings.
	 *
	 * @param selectorVersion Version from selector
	 * @param nodeVersion Version from node
	 * @return true if versions match, false otherwise
	 */
	private boolean compareVersions(String selectorVersion, String nodeVersion) {
		if (selectorVersion == null || nodeVersion == null) {
			return false;
		}

		// Direct string match
		return selectorVersion.equals(nodeVersion);
	}

	/**
	 * Clean an attribute value by removing surrounding quotes if present.
	 *
	 * @param value The raw attribute value from the parser
	 * @return The cleaned attribute value
	 */
	private String cleanAttributeValue(String value) {
		if (value.startsWith("'") && value.endsWith("'")) {
			return value.substring(1, value.length() - 1);
		}
		return value;
	}

	public List<TreeNode> getResultNodes() {
		return resultNodes;
	}
}