package com.example.tree;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class specifically for version attribute selector functionality.
 */
public class VersionSelectorTests extends TreeSelectorTestBase {

	@Test
	@DisplayName("Test simple version matching")
	void testSimpleVersionMatching() {
		List<TreeNode> results = selector.select("/Root/*{version=2.0.0}");

		assertEquals(1, results.size());
		assertEquals("Child1", results.get(0).getName());
		assertEquals("2.0.0", results.get(0).getVersion());
	}

	@Test
	@DisplayName("Test finding all nodes with same major version")
	void testFindingAllNodesWithSameMajorVersion() {
		// Add a node with version 3.5.0 to distinguish it from 2.x nodes
		TreeNode child3 = new TreeNode("Child3", "component", "tertiary", "3.5.0");
		rootNode.addChild(child3);

		List<TreeNode> results = selector.select("/Root/*{version=2.0.0}");
		assertEquals(1, results.size());
		assertEquals("Child1", results.get(0).getName());

		results = selector.select("/Root/*{version=2.1.0}");
		assertEquals(1, results.size());
		assertEquals("Child2", results.get(0).getName());

		// Test added node
		results = selector.select("/Root/*{version=3.5.0}");
		assertEquals(1, results.size());
		assertEquals("Child3", results.get(0).getName());
	}

	@Test
	@DisplayName("Test complex version selectors")
	void testComplexVersionSelectors() {
		List<TreeNode> results = selector.select("/*/*{version=2.1.0}/*{version=3.0.0}");

		assertEquals(1, results.size());
		assertEquals("GrandChild2", results.get(0).getName());
		assertEquals("3.0.0", results.get(0).getVersion());
	}

	@Test
	@DisplayName("Test quoted version numbers")
	void testQuotedVersionNumbers() {
		List<TreeNode> results = selector.select("/Root/*{'version'='2.0.0'}");

		assertEquals(1, results.size());
		assertEquals("Child1", results.get(0).getName());
		assertEquals("2.0.0", results.get(0).getVersion());
	}

	@Test
	@DisplayName("Test version with wildcard path")
	void testVersionWithWildcardPath() {
		List<TreeNode> results = selector.select("/*/*/*{version=3.0.0}");

		assertEquals(2, results.size());
		assertTrue(results.stream().allMatch(node -> "3.0.0".equals(node.getVersion())));
	}

	@Test
	@DisplayName("Test nested version attribute selectors")
	void testNestedVersionAttributeSelectors() {
		List<TreeNode> results = selector.select("/Root{version=1.0.0}/Child2{version=2.1.0}/GrandChild1{version=3.2.0}");

		assertEquals(1, results.size());
		assertEquals("GrandChild1", results.get(0).getName());
		assertEquals("3.2.0", results.get(0).getVersion());
	}
}