package com.myhexin.qparser.tokenize;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.myhexin.qparser.trietree.TrieTree;

public class NoParserMatchTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testLoadDict() {
		String[] patterns = {"均线","中国制造","中国人","中国人民","中国人民的","中华民族","好样的", "人民", "线人", "国人", "民族"};
		NoParserMatch.loadDict(Arrays.asList(patterns));
		
		NoParserMatch.noParserTrieTree.printTree();
		
        
        for (String pattern : patterns) {
        	if (pattern != null && !pattern.equals(""))
        		assertEquals(true, NoParserMatch.noParserTrieTree.mpMatchingOne(pattern));
        	else
        		assertEquals(false, NoParserMatch.noParserTrieTree.mpMatchingOne(pattern));
        }
        System.out.println("------------");
        assertEquals(false, NoParserMatch.noParserTrieTree.mpMatchingOne("国中"));
        assertEquals(true, NoParserMatch.noParserTrieTree.mpMatchingOne("中国人都是好样的"));
        assertEquals(5, NoParserMatch.noParserTrieTree.mpMatchingResult("中国人民的未来").size()); // 人民:[2:3] 国人:[1:2] 中国人:[0:2] 中国人民:[0:3] 中国人民的:[0:4]
	}

}
