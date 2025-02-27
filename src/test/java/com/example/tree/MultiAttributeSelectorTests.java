package com.example.tree;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for multiple attribute selector functionality.
 */
public class MultiAttributeSelectorTests extends TreeSelectorTestBase {

	@Test
	@DisplayName("Test multiple attribute selector - all match")
	void testMultipleAttributeSelector() {
		List<TreeNode> results = selector.select("/Root/*{type=component, variant=primary}");

		assertEquals(1, results.size());
		assertEquals("Child1", results.get(0).getName());
		assertEquals("component", results.get(0).getType());
		assertEquals("primary", results.get(0).getVariant());
	}

	@Test
	@DisplayName("Test multiple attribute selector - partial match")
	void testMultipleAttributeSelectorPartialMatch() {
		// Should return zero results because all attributes must match
		List<TreeNode> results = selector.select("/Root/*{type=component, variant=unknown}");

		assertTrue(results.isEmpty(), "Should not match any nodes when one attribute doesn't match");
	}

	@Test
	@DisplayName("Test multiple attribute selector with built-in and custom attributes")
	void testMultipleAttributeSelectorMixedTypes() {
		List<TreeNode> results = selector.select("/Root/*{type=component, visible=true, variant=secondary}");

		assertEquals(1, results.size());
		assertEquals("Child2", results.get(0).getName());
	}

	@Test
	@DisplayName("Test multiple attribute selector with version")
	void testMultipleAttributeSelectorWithVersion() {
		List<TreeNode> results = selector.select("/Root/*{version=2.0.0, type=component}");

		assertEquals(1, results.size());
		assertEquals("Child1", results.get(0).getName());
	}

	@Test
	@DisplayName("Test multiple attribute selector with quotes")
	void testMultipleAttributeSelectorWithQuotes() {
		List<TreeNode> results = selector.select("/Root/*{'type'='component', 'variant'='primary'}");

		assertEquals(1, results.size());
		assertEquals("Child1", results.get(0).getName());
	}

	@Test
	@DisplayName("Test wildcard with multiple attributes and deep path")
	void testWildcardWithMultipleAttributesAndDeepPath() {
		List<TreeNode> results = selector.select("/*{type=container}/Child2/*{type=element, version=3.0.0}");

		assertEquals(1, results.size());
		assertEquals("GrandChild2", results.get(0).getName());
	}

	@Test
	@DisplayName("Test multiple attribute selectors at different levels")
	void testMultipleAttributeSelectorsAtDifferentLevels() {
		List<TreeNode> results = selector.select("/Root{type=container}/Child2{type=component,version='2.1.0'}/GrandChild1{variant='button-alt'}");

		assertEquals(1, results.size());
		assertEquals("GrandChild1", results.get(0).getName());
		assertEquals("button-alt", results.get(0).getVariant());
	}
}