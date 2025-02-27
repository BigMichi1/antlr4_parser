package com.example.tree;

import java.util.ArrayList;
import java.util.List;

/**
 * A listener for the ANTLR-generated parse tree to process tree selector expressions.
 */
public class TreeSelectorListenerImpl extends TreeSelectorBaseListener {

	private TreeNode rootNode;
	private List<TreeNode> currentNodes = new ArrayList<>();
	private List<TreeNode> resultNodes = new ArrayList<>();

	public TreeSelectorListenerImpl(TreeNode rootNode) {
		this.rootNode = rootNode;
	}

	@Override
	public void enterSelector(TreeSelectorParser.SelectorContext ctx) {
		currentNodes.clear();
		resultNodes.clear();

		// Handle the root part (first segment) of the path
		if (ctx.nodeName() == null && ctx.wildcard() == null) {
			return;
		}

		boolean isWildcard = ctx.wildcard() != null;

		if (isWildcard) {
			// Wildcard at root matches the root node itself
			currentNodes.add(rootNode);
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

		// Check if this is a wildcard
		boolean isWildcard = ctx.wildcard() != null;
		String nodeName = isWildcard ? "*" : (ctx.nodeName() != null ? ctx.nodeName().getText() : "");

		for (TreeNode node : currentNodes) {
			if (isWildcard) {
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
	 * Filter nodes based on multiple attribute expressions.
	 * A node matches only if it matches ALL attribute expressions.
	 * An attribute with value "*" is treated as a wildcard and always matches.
	 *
	 * @param nodes The list of nodes to filter
	 * @param attrSelector The attribute selector containing multiple expressions
	 * @return A filtered list of nodes that match all attribute expressions
	 */
	private List<TreeNode> filterNodesByAttributes(List<TreeNode> nodes, TreeSelectorParser.AttributeSelectorContext attrSelector) {
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