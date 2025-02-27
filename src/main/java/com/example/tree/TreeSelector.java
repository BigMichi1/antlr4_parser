package com.example.tree;

import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

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
	 *
	 * @param selectorExpression the selector expression (e.g. "/Root/Child2")
	 * @return a list of matching nodes
	 */
	public List<TreeNode> select(String selectorExpression) {
		try {
			// Create lexer and parser
			TreeSelectorLexer lexer = new TreeSelectorLexer(CharStreams.fromString(selectorExpression));
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			TreeSelectorParser parser = new TreeSelectorParser(tokens);

			// Parse the expression
			TreeSelectorParser.SelectorContext selectorContext = parser.selector();

			// Create a listener to walk the parse tree
			TreeSelectorListenerImpl listener = new TreeSelectorListenerImpl(rootNode);
			ParseTreeWalker walker = new ParseTreeWalker();
			walker.walk(listener, selectorContext);

			// Return the result nodes
			return listener.getResultNodes();
		} catch (Exception e) {
			System.err.println("Error parsing selector expression: " + e.getMessage());
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
}