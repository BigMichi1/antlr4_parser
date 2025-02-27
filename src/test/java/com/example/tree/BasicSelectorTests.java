package com.example.tree;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Basic tests for tree selector expressions.
 */
public class BasicSelectorTests extends TreeSelectorTestBase {

	@Test
	@DisplayName("Select root node")
	void testSelectRoot() {
		List<TreeNode> results = selector.select("/Root");

		assertEquals(1, results.size());
		assertEquals("Root", results.get(0).getName());
	}

	@Test
	@DisplayName("Select direct child nodes")
	void testSelectDirectChild() {
		List<TreeNode> results = selector.select("/Root/Child1");

		assertEquals(1, results.size());
		assertEquals("Child1", results.get(0).getName());
	}

	@Test
	@DisplayName("Select child that doesn't exist")
	void testSelectNonExistentChild() {
		List<TreeNode> results = selector.select("/Root/NonExistentChild");

		assertTrue(results.isEmpty());
	}

	@Test
	@DisplayName("Select grandchild nodes")
	void testSelectGrandchild() {
		List<TreeNode> results = selector.select("/Root/Child2/GrandChild2");

		assertEquals(1, results.size());
		assertEquals("GrandChild2", results.get(0).getName());
	}
}