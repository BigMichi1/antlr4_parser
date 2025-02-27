package com.example.tree;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

/**
 * Advanced tests for tree selector expressions including wildcards and error cases.
 */
public class AdvancedSelectorTests extends TreeSelectorTestBase {

	@Test
	@DisplayName("Select multiple nodes with same name")
	void testSelectMultipleNodesWithSameName() {
		// Add another Child1 node to root
		TreeNode anotherChild1 = new TreeNode("Child1", "component", "tertiary");
		rootNode.addChild(anotherChild1);

		List<TreeNode> results = selector.select("/Root/Child1");

		assertEquals(2, results.size());
		assertEquals("Child1", results.get(0).getName());
		assertEquals("Child1", results.get(1).getName());
	}

	@Test
	@DisplayName("Test wildcard selector")
	void testWildcardSelector() {
		List<TreeNode> results = selector.select("/Root/*");

		assertEquals(2, results.size());
		assertTrue(results.stream().anyMatch(node -> node.getName().equals("Child1")));
		assertTrue(results.stream().anyMatch(node -> node.getName().equals("Child2")));
	}

	@Test
	@DisplayName("Test wildcard at root level")
	void testWildcardAtRootLevel() {
		List<TreeNode> results = selector.select("/*/Child2");

		assertEquals(1, results.size());
		assertEquals("Child2", results.get(0).getName());
	}

	@Test
	@DisplayName("Test multiple wildcards in path")
	void testMultipleWildcardsInPath() {
		List<TreeNode> results = selector.select("/*/*/GrandChild2");

		assertEquals(1, results.size());
		assertEquals("GrandChild2", results.get(0).getName());
	}

	@Test
	@DisplayName("Test invalid selector syntax")
	void testInvalidSelectorSyntax() {
		List<TreeNode> results = selector.select("Root/Child1");  // Missing leading slash

		assertTrue(results.isEmpty());
	}

	@Test
	@DisplayName("Test empty selector")
	void testEmptySelector() {
		List<TreeNode> results = selector.select("");

		assertTrue(results.isEmpty());
	}

	@Test
	@DisplayName("Test null selector")
	void testNullSelector() {
		Exception exception = assertThrows(NullPointerException.class, () -> {
			selector.select(null);
		});

		assertNotNull(exception);
	}

	@Test
	@DisplayName("Test root mismatch")
	void testRootMismatch() {
		List<TreeNode> results = selector.select("/NotRoot");

		assertTrue(results.isEmpty());
	}

	@Test
	@DisplayName("Nested wildcard selectors")
	void testNestedWildcardSelectors() {
		List<TreeNode> results = selector.select("/Root/*/GrandChild2");

		assertEquals(1, results.size());
		assertEquals("GrandChild2", results.get(0).getName());
	}

	@Test
	@DisplayName("Root wildcard selectors")
	void testRootWildcard() {
		List<TreeNode> results = selector.select("/*");

		assertEquals(1, results.size());
		assertEquals("Root", results.get(0).getName());
	}

	@Test
	@DisplayName("Complete wildcard path")
	void testCompleteWildcardPath() {
		List<TreeNode> results = selector.select("/*/*/*");

		assertEquals(3, results.size());
		assertTrue(results.stream().anyMatch(node -> node.getName().equals("GrandChild1")));
		assertTrue(results.stream().anyMatch(node -> node.getName().equals("GrandChild2")));
		assertTrue(results.stream().anyMatch(node -> node.getName().equals("GrandChild3")));
	}
}