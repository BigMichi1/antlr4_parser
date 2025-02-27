package com.example.tree;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for wildcard attribute value functionality.
 */
public class WildcardAttributeValueTests extends TreeSelectorTestBase {

	@Test
	@DisplayName("Test wildcard attribute value for type")
	void testWildcardAttributeValueForType() {
		// Get results with and without wildcard attribute
		List<TreeNode> resultsWithWildcard = selector.select("/Root/*{type=*}");
		List<TreeNode> resultsWithoutAttribute = selector.select("/Root/*");

		// Both should return the same nodes
		assertEquals(resultsWithoutAttribute.size(), resultsWithWildcard.size());
		assertEquals(2, resultsWithWildcard.size());
	}

	@Test
	@DisplayName("Test wildcard attribute value with other attribute")
	void testWildcardAttributeValueWithOtherAttribute() {
		// Get results with and without wildcard attribute
		List<TreeNode> resultsWithWildcard = selector.select("/Root/*{type=component,version=*}");
		List<TreeNode> resultsWithoutWildcard = selector.select("/Root/*{type=component}");

		// Both should return the same nodes
		assertEquals(resultsWithoutWildcard.size(), resultsWithWildcard.size());
		assertEquals(2, resultsWithWildcard.size());
	}

	@Test
	@DisplayName("Test all wildcard attributes")
	void testAllWildcardAttributes() {
		List<TreeNode> results = selector.select("/Root/*{type=*,variant=*,version=*}");
		List<TreeNode> resultsWithoutAttributes = selector.select("/Root/*");

		// Both should return the same nodes
		assertEquals(resultsWithoutAttributes.size(), results.size());
		assertEquals(2, results.size());
	}

	@Test
	@DisplayName("Test mixed wildcard and specific attributes")
	void testMixedWildcardAndSpecificAttributes() {
		List<TreeNode> results = selector.select("/Root/*{type=component,variant=*,version=2.0.0}");

		assertEquals(1, results.size());
		assertEquals("Child1", results.get(0).getName());
	}

	@Test
	@DisplayName("Test wildcard attribute with other filters in path")
	void testWildcardAttributeWithOtherFiltersInPath() {
		List<TreeNode> results = selector.select("/*{type=*}/Child1/GrandChild1{version=3.0.0}");

		assertEquals(1, results.size());
		assertEquals("GrandChild1", results.get(0).getName());
		assertEquals("3.0.0", results.get(0).getVersion());
	}

	@Test
	@DisplayName("Test quoted wildcard attribute value")
	void testQuotedWildcardAttributeValue() {
		List<TreeNode> results = selector.select("/Root/*{'type'='*','variant'='primary'}");
		List<TreeNode> resultsWithoutWildcard = selector.select("/Root/*{variant=primary}");

		// Both should return the same nodes
		assertEquals(resultsWithoutWildcard.size(), results.size());
		assertEquals(1, results.size());
		assertEquals("Child1", results.get(0).getName());
	}
}