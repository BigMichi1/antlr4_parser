package com.example.tree;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Main class for selecting nodes from a tree using selector expressions.
 */
public class TreeSelector {

	private TreeNode rootNode;

	public TreeSelector(TreeNode rootNode) {
		this.rootNode = rootNode;
	}

	/**
	 * Select nodes from the tree using a selector expression.
	 * Supports multiple selectors separated by pipe (|) character.
	 *
	 * @param selectorExpression the selector expression (e.g. "/Root/Child2" or "/Root/Child1|/Root/Child2")
	 * @return a list of matching nodes
	 * @throws NullPointerException if selectorExpression is null
	 */
	public List<TreeNode> select(String selectorExpression) {
		// Explicitly check for null to ensure the NullPointerException is thrown consistently
		if (selectorExpression == null) {
			throw new NullPointerException("Selector expression cannot be null");
		}

		// Check for empty string
		if (selectorExpression.isEmpty()) {
			return new ArrayList<>();
		}

		try {
			// Create lexer and parser
			TreeSelectorLexer lexer = new TreeSelectorLexer(CharStreams.fromString(selectorExpression));
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			TreeSelectorParser parser = new TreeSelectorParser(tokens);

			// Parse the expression as a multiSelector
			TreeSelectorParser.MultiSelectorContext multiSelectorContext = parser.multiSelector();

			// Use a set to avoid duplicate results when multiple selectors match the same nodes
			Set<TreeNode> resultSet = new HashSet<>();

			// Process each selector expression
			for (TreeSelectorParser.SelectorExpressionContext selectorExprContext : multiSelectorContext.selectorExpression()) {
				TreeSelectorParser.SelectorContext selectorContext = selectorExprContext.selector();

				// Skip invalid selectors (those that don't start with /)
				if (!selectorExpression.startsWith("/") && multiSelectorContext.selectorExpression().size() == 1) {
					return new ArrayList<>();
				}

				// Create a listener to walk the parse tree for this selector
				TreeSelectorListenerImpl listener = new TreeSelectorListenerImpl(rootNode);
				ParseTreeWalker walker = new ParseTreeWalker();
				walker.walk(listener, selectorContext);

				// Add results from this selector to the set
				resultSet.addAll(listener.getResultNodes());
			}

			// Convert set to list to maintain the existing API
			return new ArrayList<>(resultSet);

		} catch (NullPointerException e) {
			// Re-throw NullPointerException to maintain expected behavior
			throw e;
		} catch (Exception e) {
			System.err.println("Error parsing selector expression: " + e.getMessage());
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
}