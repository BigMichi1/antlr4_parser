package com.example.tree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A listener for the ANTLR-generated parse tree to process deep tree selector expressions
 * using the '**' operator.
 */
public class DeepTreeSelectorListenerImpl extends TreeSelectorBaseListener {

	private TreeNode rootNode;
	private List<TreeNode> resultNodes = new ArrayList<>();
	private Set<TreeNode> processedNodes = new HashSet<>();

	public DeepTreeSelectorListenerImpl(TreeNode rootNode) {
		this.rootNode = rootNode;
	}

	@Override
	public void enterDeepSelector(TreeSelectorParser.DeepSelectorContext ctx) {
		resultNodes.clear();
		processedNodes.clear();

		// First, collect all nodes in the tree recursively
		List<TreeNode> allNodes = new ArrayList<>();
		collectNodesRecursively(rootNode, allNodes);

		// Get the node name or check if it's a wildcard from the first segment after **
		boolean isWildcard = ctx.wildcard() != null;
		String nodeName = isWildcard ? "*" : (ctx.nodeName() != null ? ctx.nodeName().getText() : "");

		// Filter nodes by name
		List<TreeNode> matchingNodes = new ArrayList<>();
		for (TreeNode node : allNodes) {
			if (isWildcard || node.getName().equals(nodeName)) {
				matchingNodes.add(node);
			}
		}

		// Apply attribute selector if present
		if (ctx.attributeSelector() != null) {
			TreeSelectorListenerImpl helper = new TreeSelectorListenerImpl(rootNode);
			matchingNodes = helper.filterNodesByAttributes(matchingNodes, ctx.attributeSelector());
		}

		// If there are no nodeSelectors, these are the result nodes
		if (ctx.nodeSelector().isEmpty()) {
			resultNodes.addAll(matchingNodes);
			return;
		}

		// Process further nodeSelectors if present
		List<TreeNode> currentNodes = matchingNodes;

		for (int i = 0; i < ctx.nodeSelector().size(); i++) {
			TreeSelectorParser.NodeSelectorContext nsCtx = ctx.nodeSelector(i);

			List<TreeNode> nextNodes = new ArrayList<>();
			boolean nsIsWildcard = nsCtx.wildcard() != null;
			String nsNodeName = nsIsWildcard ? "*" : (nsCtx.nodeName() != null ? nsCtx.nodeName().getText() : "");

			for (TreeNode node : currentNodes) {
				for (TreeNode child : node.getChildren()) {
					if (nsIsWildcard || child.getName().equals(nsNodeName)) {
						nextNodes.add(child);
					}
				}
			}

			// Apply attribute selector for this node selector if present
			if (nsCtx.attributeSelector() != null) {
				TreeSelectorListenerImpl helper = new TreeSelectorListenerImpl(rootNode);
				nextNodes = helper.filterNodesByAttributes(nextNodes, nsCtx.attributeSelector());
			}

			currentNodes = nextNodes;

			// If this is the last nodeSelector, these are the result nodes
			if (i == ctx.nodeSelector().size() - 1) {
				resultNodes.addAll(currentNodes);
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

	public List<TreeNode> getResultNodes() {
		return resultNodes;
	}
}