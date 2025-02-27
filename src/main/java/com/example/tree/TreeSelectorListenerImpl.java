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
			currentNodes = filterNodesByAttribute(currentNodes, ctx.attributeSelector());
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
			matchingNodes = filterNodesByAttribute(matchingNodes, ctx.attributeSelector());
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
	 * Filter a list of nodes based on attribute selectors.
	 *
	 * @param nodes The list of nodes to filter
	 * @param attrSelector The attribute selector from the parse tree
	 * @return A filtered list of nodes that match the attribute selector
	 */
	private List<TreeNode> filterNodesByAttribute(List<TreeNode> nodes, TreeSelectorParser.AttributeSelectorContext attrSelector) {
		List<TreeNode> filteredNodes = new ArrayList<>();

		TreeSelectorParser.AttributeExprContext exprCtx = attrSelector.attributeExpr();
		if (exprCtx == null) {
			return nodes; // No filtering needed
		}

		// Get attribute name (remove quotes if present)
		String attrName = cleanAttributeValue(exprCtx.attributeName().getText());

		// Get attribute value (remove quotes if present)
		String attrValue = cleanAttributeValue(exprCtx.attributeValue().getText());

		// Filter nodes based on the attribute name and value
		for (TreeNode node : nodes) {
			if ("type".equals(attrName) && attrValue.equals(node.getType())) {
				filteredNodes.add(node);
			} else if ("variant".equals(attrName) && attrValue.equals(node.getVariant())) {
				filteredNodes.add(node);
			} else if ("version".equals(attrName) && compareVersions(attrValue, node.getVersion())) {
				filteredNodes.add(node);
			} else if (attrValue.equals(node.getAttribute(attrName))) {
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

		// Note: This could be extended to support semantic version comparison
		// like comparing major.minor.patch or using version ranges
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