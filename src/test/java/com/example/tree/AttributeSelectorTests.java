package com.example.tree;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class specifically for attribute selector functionality.
 */
public class AttributeSelectorTests extends TreeSelectorTestBase {

	@Test
	@DisplayName("Test attribute selector with type")
	void testAttributeSelectorWithType() {
		List<TreeNode> results = selector.select("/Root/*{type=component}");

		assertEquals(2, results.size());
		assertTrue(results.stream().allMatch(node -> "component".equals(node.getType())));
		assertTrue(results.stream().anyMatch(node -> "Child1".equals(node.getName())));
		assertTrue(results.stream().anyMatch(node -> "Child2".equals(node.getName())));
	}

	@Test
	@DisplayName("Test attribute selector with specific node")
	void testAttributeSelectorWithSpecificNode() {
		List<TreeNode> results = selector.select("/Root/Child2{type=component}");

		assertEquals(1, results.size());
		assertEquals("Child2", results.get(0).getName());
		assertEquals("component", results.get(0).getType());
	}

	@Test
	@DisplayName("Test attribute selector with non-matching value")
	void testAttributeSelectorWithNonMatchingValue() {
		List<TreeNode> results = selector.select("/Root/Child2{type=foo}");

		assertTrue(results.isEmpty());
	}

	@Test
	@DisplayName("Test attribute selector with quotes")
	void testAttributeSelectorWithQuotes() {
		List<TreeNode> results = selector.select("/Root/*{'type'='component'}");

		assertEquals(2, results.size());
		assertTrue(results.stream().allMatch(node -> "component".equals(node.getType())));
	}

	@Test
	@DisplayName("Test attribute selector with variant")
	void testAttributeSelectorWithVariant() {
		List<TreeNode> results = selector.select("/Root/*{variant=primary}");

		assertEquals(1, results.size());
		assertEquals("Child1", results.get(0).getName());
		assertEquals("primary", results.get(0).getVariant());
	}

	@Test
	@DisplayName("Test attribute selector with custom attribute")
	void testAttributeSelectorWithCustomAttribute() {
		List<TreeNode> results = selector.select("/Root/*{visible=true}");

		assertEquals(2, results.size());
		assertTrue(results.stream().allMatch(node -> "true".equals(node.getAttribute("visible"))));
	}

	@Test
	@DisplayName("Test combined selectors with attributes")
	void testCombinedSelectorsWithAttributes() {
		List<TreeNode> results = selector.select("/Root/*{type=component}/GrandChild1");

		assertEquals(2, results.size());
		assertTrue(results.stream().allMatch(node -> "GrandChild1".equals(node.getName())));
	}

	@Test
	@DisplayName("Test complex selector with wildcards and attributes")
	void testComplexSelectorWithWildcardsAndAttributes() {
		List<TreeNode> results = selector.select("/*{type=container}/*/*{variant=button}");

		assertEquals(1, results.size());
		assertEquals("GrandChild1", results.get(0).getName());
		assertEquals("button", results.get(0).getVariant());
	}
}