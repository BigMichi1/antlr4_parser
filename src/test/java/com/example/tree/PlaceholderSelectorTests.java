package com.example.tree;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for placeholder selector functionality using the '~~' operator.
 */
public class PlaceholderSelectorTests extends TreeSelectorTestBase {

	@Test
	@DisplayName("Test placeholder at top level")
	void testPlaceholderAtTopLevel() {
		List<TreeNode> results = selector.select("/~~");

		// Should return all nodes in the tree except the root
		assertEquals(6, results.size()); // 2 children + 4 grandchildren
	}

	@Test
	@DisplayName("Test placeholder with attribute filter")
	void testPlaceholderWithAttributeFilter() {
		List<TreeNode> results = selector.select("/Root/~~/*{version=3.0.0}");

		assertEquals(2, results.size());
		assertTrue(results.stream().allMatch(node -> "3.0.0".equals(node.getVersion())));
	}

	@Test
	@DisplayName("Test placeholder after specific node")
	void testPlaceholderAfterSpecificNode() {
		List<TreeNode> results = selector.select("/Root/Child2/~~");

		assertEquals(3, results.size()); // All grandchildren under Child2
		assertTrue(results.stream().anyMatch(node -> "GrandChild1".equals(node.getName())));
		assertTrue(results.stream().anyMatch(node -> "GrandChild2".equals(node.getName())));
		assertTrue(results.stream().anyMatch(node -> "GrandChild3".equals(node.getName())));
	}

	@Test
	@DisplayName("Test placeholder between nodes")
	void testPlaceholderBetweenNodes() {
		List<TreeNode> results = selector.select("/Root/~~/GrandChild2");

		assertEquals(1, results.size());
		assertEquals("GrandChild2", results.get(0).getName());
	}

	@Test
	@DisplayName("Test placeholder with multiple attribute filters")
	void testPlaceholderWithMultipleAttributeFilters() {
		List<TreeNode> results = selector.select("/Root/~~{type=element,variant=button}");

		assertEquals(1, results.size());
		assertEquals("GrandChild1", results.get(0).getName());
		assertEquals("button", results.get(0).getVariant());
	}

	@Test
	@DisplayName("Test chained placeholders")
	void testChainedPlaceholders() {
		// This doesn't make much sense but should work - get all descendants
		List<TreeNode> results = selector.select("/Root/~~/~~");

		// Should get all descendants of all descendants of Root, which is basically all grandchildren
		assertEquals(4, results.size());
	}

	@Test
	@DisplayName("Test placeholder with version attribute")
	void testPlaceholderWithVersionAttribute() {
		List<TreeNode> results = selector.select("/~~{version=2.0.0}");

		assertEquals(1, results.size());
		assertEquals("Child1", results.get(0).getName());
		assertEquals("2.0.0", results.get(0).getVersion());
	}

	@Test
	@DisplayName("Test placeholder combined with multiple selectors")
	void testPlaceholderCombinedWithMultipleSelectors() {
		List<TreeNode> results = selector.select("/Root/Child1/~~|/Root/Child2/~~");

		assertEquals(4, results.size()); // All grandchildren
	}

	@Test
	@DisplayName("Test placeholder with wildcard attributes")
	void testPlaceholderWithWildcardAttributes() {
		List<TreeNode> results = selector.select("/Root/~~{type=element,variant=*}");

		assertEquals(4, results.size());
		assertTrue(results.stream().allMatch(node -> "element".equals(node.getType())));
	}
}