package com.example.tree;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for multiple selector functionality (using the pipe | character).
 */
public class MultiSelectorTests extends TreeSelectorTestBase {

	@Test
	@DisplayName("Test basic multiple selectors")
	void testBasicMultipleSelectors() {
		List<TreeNode> results = selector.select("/Root/Child1|/Root/Child2");

		assertEquals(2, results.size());
		assertTrue(results.stream().anyMatch(node -> "Child1".equals(node.getName())));
		assertTrue(results.stream().anyMatch(node -> "Child2".equals(node.getName())));
	}

	@Test
	@DisplayName("Test multiple selectors with overlapping results")
	void testMultipleSelectorsWithOverlappingResults() {
		List<TreeNode> results = selector.select("/Root/Child1|/Root/*");

		// Should return 2 nodes (Child1 and Child2) without duplicates
		assertEquals(2, results.size());
		assertTrue(results.stream().anyMatch(node -> "Child1".equals(node.getName())));
		assertTrue(results.stream().anyMatch(node -> "Child2".equals(node.getName())));
	}

	@Test
	@DisplayName("Test multiple selectors with one invalid selector")
	void testMultipleSelectorsWithOneInvalidSelector() {
		List<TreeNode> results = selector.select("/Root/Child1|/NonExistent");

		// Should still return Child1 even though the second selector is invalid
		assertEquals(1, results.size());
		assertEquals("Child1", results.get(0).getName());
	}

	@Test
	@DisplayName("Test multiple selectors with all invalid selectors")
	void testMultipleSelectorsWithAllInvalidSelectors() {
		List<TreeNode> results = selector.select("/NonExistent1|/NonExistent2");

		// Should return empty list as no selectors match
		assertTrue(results.isEmpty());
	}

	@Test
	@DisplayName("Test multiple selectors with attribute filters")
	void testMultipleSelectorsWithAttributeFilters() {
		List<TreeNode> results = selector.select("/Root/*{type=component,variant=primary}|/Root/*{type=component,variant=secondary}");

		assertEquals(2, results.size());
		assertTrue(results.stream().anyMatch(node -> "Child1".equals(node.getName())));
		assertTrue(results.stream().anyMatch(node -> "Child2".equals(node.getName())));
	}

	@Test
	@DisplayName("Test multiple selectors at different tree levels")
	void testMultipleSelectorsAtDifferentTreeLevels() {
		List<TreeNode> results = selector.select("/Root|/Root/Child1/GrandChild1");

		assertEquals(2, results.size());
		assertTrue(results.stream().anyMatch(node -> "Root".equals(node.getName())));
		assertTrue(results.stream().anyMatch(node -> "GrandChild1".equals(node.getName())));
	}

	@Test
	@DisplayName("Test multiple complex selectors")
	void testMultipleComplexSelectors() {
		List<TreeNode> results = selector.select(
				"/Root/*{type=component}/GrandChild1|" +
						"/*{type=container}/Child2/*{version=3.0.0}|" +
						"/Root/*/*{variant='button-alt'}"
		);

		assertEquals(3, results.size());
		assertTrue(results.stream().filter(node -> "GrandChild1".equals(node.getName())).count() == 2);
		assertTrue(results.stream().anyMatch(node -> "GrandChild2".equals(node.getName())));
	}
}