package com.example.tree;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for deep traversal selector functionality using the '**' operator.
 */
public class DeepTraversalTests extends TreeSelectorTestBase {

	@Test
	@DisplayName("Test deep traversal with wildcard")
	void testDeepTraversalWithWildcard() {
		List<TreeNode> results = selector.select("**/*");

		// Should return all nodes in the tree
		assertEquals(7, results.size()); // Root + 2 children + 4 grandchildren
	}

	@Test
	@DisplayName("Test deep traversal with specific node name")
	void testDeepTraversalWithSpecificNodeName() {
		List<TreeNode> results = selector.select("**/Child2");

		assertEquals(1, results.size());
		assertEquals("Child2", results.get(0).getName());
	}

	@Test
	@DisplayName("Test deep traversal with attribute selector")
	void testDeepTraversalWithAttributeSelector() {
		List<TreeNode> results = selector.select("**/*{variant=secondary}");

		assertEquals(1, results.size());
		assertEquals("Child2", results.get(0).getName());
		assertEquals("secondary", results.get(0).getVariant());
	}

	@Test
	@DisplayName("Test deep traversal with GrandChild nodes")
	void testDeepTraversalWithGrandChildNodes() {
		List<TreeNode> results = selector.select("**/GrandChild1");

		assertEquals(2, results.size());
		assertTrue(results.stream().allMatch(node -> "GrandChild1".equals(node.getName())));
	}

	@Test
	@DisplayName("Test deep traversal with specific attribute")
	void testDeepTraversalWithSpecificAttribute() {
		List<TreeNode> results = selector.select("**/Child2{type=component}");

		assertEquals(1, results.size());
		assertEquals("Child2", results.get(0).getName());
	}

	@Test
	@DisplayName("Test deep traversal with version attribute")
	void testDeepTraversalWithVersionAttribute() {
		List<TreeNode> results = selector.select("**/*{version=3.0.0}");

		assertEquals(2, results.size());
		assertTrue(results.stream().allMatch(node -> "3.0.0".equals(node.getVersion())));
	}

	@Test
	@DisplayName("Test deep traversal with child path")
	void testDeepTraversalWithChildPath() {
		List<TreeNode> results = selector.select("**/Child2/GrandChild2");

		assertEquals(1, results.size());
		assertEquals("GrandChild2", results.get(0).getName());
	}

	@Test
	@DisplayName("Test deep traversal with multiple attribute filters")
	void testDeepTraversalWithMultipleAttributeFilters() {
		List<TreeNode> results = selector.select("**/*{type=element,variant=button}");

		assertEquals(1, results.size());
		assertEquals("GrandChild1", results.get(0).getName());
		assertEquals("button", results.get(0).getVariant());
	}

	@Test
	@DisplayName("Test deep traversal combined with multiple selectors")
	void testDeepTraversalCombinedWithMultipleSelectors() {
		List<TreeNode> results = selector.select("**/GrandChild1|**/GrandChild2");

		assertEquals(3, results.size());
		assertEquals(2, results.stream().filter(node -> "GrandChild1".equals(node.getName())).count());
		assertEquals(1, results.stream().filter(node -> "GrandChild2".equals(node.getName())).count());
	}

	@Test
	@DisplayName("Test deep traversal with wildcard attributes")
	void testDeepTraversalWithWildcardAttributes() {
		List<TreeNode> results = selector.select("**/*{type=element,variant=*}");

		assertEquals(4, results.size());
		assertTrue(results.stream().allMatch(node -> "element".equals(node.getType())));
	}
}