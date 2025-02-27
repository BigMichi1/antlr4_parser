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

		// Get the root node name
		String rootName = ctx.nodeName().getText();
		if (rootNode.getName().equals(rootName)) {
			currentNodes.add(rootNode);

			// If there are no nodeSelectors, the root itself is the result
			if (ctx.nodeSelector().isEmpty()) {
				resultNodes.add(rootNode);
			}
		}
	}

	@Override
	public void enterNodeSelector(TreeSelectorParser.NodeSelectorContext ctx) {
		List<TreeNode> matchingNodes = new ArrayList<>();

		// Check if this is a wildcard
		boolean isWildcard = ctx.wildcard() != null;
		String nodeName = isWildcard ? "*" : ctx.nodeName().getText();

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

		currentNodes = matchingNodes;

		// Check if this is the last nodeSelector
		TreeSelectorParser.SelectorContext parentCtx = (TreeSelectorParser.SelectorContext) ctx.getParent();
		int lastNodeSelectorIndex = parentCtx.nodeSelector().size() - 1;
		int currentIndex = parentCtx.nodeSelector().indexOf(ctx);

		if (currentIndex == lastNodeSelectorIndex) {
			resultNodes.addAll(matchingNodes);
		}
	}

	public List<TreeNode> getResultNodes() {
		return resultNodes;
	}
}