package com.example.tree;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class specifically for wildcard selector functionality.
 */
public class WildcardSelectorTests extends TreeSelectorTestBase {

	@Test
	@DisplayName("Test wildcard selector at child level")
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

		assertEquals(4, results.size());
		assertTrue(results.stream().filter(node -> node.getName().equals("GrandChild1")).count() == 2);
		assertTrue(results.stream().anyMatch(node -> node.getName().equals("GrandChild2")));
		assertTrue(results.stream().anyMatch(node -> node.getName().equals("GrandChild3")));
	}

	@Test
	@DisplayName("Find all GrandChild1 nodes using wildcards")
	void testFindAllGrandChild1Nodes() {
		List<TreeNode> results = selector.select("/Root/*/*/GrandChild1");

		assertEquals(0, results.size(), "Should not find any GrandChild1 at this level");

		results = selector.select("/Root/*/GrandChild1");

		assertEquals(2, results.size(), "Should find both GrandChild1 nodes");
		assertTrue(results.stream().allMatch(node -> node.getName().equals("GrandChild1")));

		// Verify that both nodes are different by checking their variants
		boolean foundButton = false;
		boolean foundButtonAlt = false;

		for (TreeNode node : results) {
			if ("button".equals(node.getVariant())) {
				foundButton = true;
			} else if ("button-alt".equals(node.getVariant())) {
				foundButtonAlt = true;
			}
		}

		assertTrue(foundButton, "Should find GrandChild1 with 'button' variant");
		assertTrue(foundButtonAlt, "Should find GrandChild1 with 'button-alt' variant");
	}

	@Test
	@DisplayName("Test wildcard at all positions")
	void testWildcardAtAllPositions() {
		// Test wildcards at intermediate levels
		List<TreeNode> results = selector.select("/Root/*/GrandChild2");

		assertEquals(1, results.size());
		assertEquals("GrandChild2", results.get(0).getName());

		// Test specific root with wildcards for the rest
		results = selector.select("/Root/*/*");

		assertEquals(4, results.size());

		// Count specific node types
		long grandChild1Count = results.stream().filter(node -> node.getName().equals("GrandChild1")).count();
		long grandChild2Count = results.stream().filter(node -> node.getName().equals("GrandChild2")).count();
		long grandChild3Count = results.stream().filter(node -> node.getName().equals("GrandChild3")).count();

		assertEquals(2, grandChild1Count);
		assertEquals(1, grandChild2Count);
		assertEquals(1, grandChild3Count);
	}
}