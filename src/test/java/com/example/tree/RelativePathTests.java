package com.example.tree;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for relative path operators (. and ..).
 */
public class RelativePathTests extends TreeSelectorTestBase {

	@Test
	@DisplayName("Test current level operator")
	void testCurrentLevelOperator() {
		List<TreeNode> results = selector.select("/Root/./*");
		List<TreeNode> directResults = selector.select("/Root/*");

		assertEquals(directResults.size(), results.size());
		assertEquals(2, results.size());
		assertTrue(results.stream().anyMatch(node -> "Child1".equals(node.getName())));
		assertTrue(results.stream().anyMatch(node -> "Child2".equals(node.getName())));
	}

	@Test
	@DisplayName("Test parent level operator")
	void testParentLevelOperator() {
		List<TreeNode> results = selector.select("/Root/../*");

		assertEquals(1, results.size());
		assertEquals("Root", results.get(0).getName());
	}

	@Test
	@DisplayName("Test current level operator in middle of path")
	void testCurrentLevelOperatorInMiddle() {
		List<TreeNode> results = selector.select("/Root/Child2/./GrandChild2");
		List<TreeNode> directResults = selector.select("/Root/Child2/GrandChild2");

		assertEquals(directResults.size(), results.size());
		assertEquals(1, results.size());
		assertEquals("GrandChild2", results.get(0).getName());
	}

	@Test
	@DisplayName("Test parent level operator in middle of path")
	void testParentLevelOperatorInMiddle() {
		List<TreeNode> results = selector.select("/Root/Child2/../Child1");

		assertEquals(1, results.size());
		assertEquals("Child1", results.get(0).getName());
	}

	@Test
	@DisplayName("Test relative path with attribute selector")
	void testRelativePathWithAttributeSelector() {
		List<TreeNode> results = selector.select("/Root/Child2/../*{variant=primary}");

		assertEquals(1, results.size());
		assertEquals("Child1", results.get(0).getName());
		assertEquals("primary", results.get(0).getVariant());
	}

	@Test
	@DisplayName("Test multiple parent level operators")
	void testMultipleParentLevelOperators() {
		List<TreeNode> results = selector.select("/Root/Child2/GrandChild2/../../Child1");

		assertEquals(1, results.size());
		assertEquals("Child1", results.get(0).getName());
	}

	@Test
	@DisplayName("Test parent operator at start returns empty")
	void testParentOperatorAtStartReturnsEmpty() {
		List<TreeNode> results = selector.select("/../*");

		assertTrue(results.isEmpty());
	}

	@Test
	@DisplayName("Test current operator at start")
	void testCurrentOperatorAtStart() {
		List<TreeNode> results = selector.select("/.");

		assertEquals(1, results.size());
		assertEquals("Root", results.get(0).getName());
	}

	@Test
	@DisplayName("Test current operator at start with wildcard")
	void testCurrentOperatorAtStartWithWildcard() {
		List<TreeNode> results = selector.select("/./*");

		assertEquals(2, results.size());
		assertTrue(results.stream().anyMatch(node -> "Child1".equals(node.getName())));
		assertTrue(results.stream().anyMatch(node -> "Child2".equals(node.getName())));
	}

	@Test
	@DisplayName("Test combined relative path operators")
	void testCombinedRelativePathOperators() {
		List<TreeNode> results = selector.select("/Root/Child1/../Child2/./GrandChild2");

		assertEquals(1, results.size());
		assertEquals("GrandChild2", results.get(0).getName());
	}

	@Test
	@DisplayName("Test relative paths with wildcard")
	void testRelativePathsWithWildcard() {
		List<TreeNode> results = selector.select("/Root/Child2/*/../*");

		assertEquals(3, results.size());
		assertTrue(results.stream().allMatch(node -> node.getParent().getName().equals("Child2")));
	}
}