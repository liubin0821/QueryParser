package com.myhexin.qparser.phrase.util;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Traditional2simplifiedTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testLoadDict() throws IOException {
		//Traditional2simplified.loadDict("./src/test/com/myhexin/qparser/phrase/util/traditional_simplified_mapping.txt");
		HashMap<String, String> expected = new HashMap<String, String>();
		expected.put("愛", "爱");
		expected.put("癡", "痴");
		assertEquals(expected, Traditional2simplified.mapping);
	}

	@Test
	public void testToSimplified() {
		assertEquals(null, Traditional2simplified.toSimplified(null));
		assertEquals("痴", Traditional2simplified.toSimplified("癡"));
		assertEquals("痴", Traditional2simplified.toSimplified("痴"));
		assertEquals("動", Traditional2simplified.toSimplified("動"));
		assertEquals("動物都比较痴", Traditional2simplified.toSimplified("動物都比较癡"));
	}

}
